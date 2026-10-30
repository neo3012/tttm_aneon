package com.company.aneon.f10.service.impl;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.common.security.CurrentUser;
import com.company.aneon.f10.constant.EventStatus;
import com.company.aneon.f10.dto.request.ApprovalRequest;
import com.company.aneon.f10.dto.request.CheckInRequest;
import com.company.aneon.f10.dto.request.CreateActivityRequest;
import com.company.aneon.f10.dto.request.CreateEventRequest;
import com.company.aneon.f10.dto.response.ActivityResponse;
import com.company.aneon.f10.dto.response.CheckInResponse;
import com.company.aneon.f10.dto.response.EventResponse;
import com.company.aneon.f10.dto.response.QrResponse;
import com.company.aneon.f10.entity.HoatDongSuKien;
import com.company.aneon.f10.entity.LuotThamGiaSuKien;
import com.company.aneon.f10.entity.MaQrThamGia;
import com.company.aneon.f10.entity.SuKien;
import com.company.aneon.f10.repository.HoatDongSuKienRepository;
import com.company.aneon.f10.repository.LuotThamGiaSuKienRepository;
import com.company.aneon.f10.repository.MaQrThamGiaRepository;
import com.company.aneon.f10.repository.SuKienRepository;
import com.company.aneon.f10.service.EventService;
import jakarta.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SuKienRepository eventRepository;
    private final HoatDongSuKienRepository activityRepository;
    private final MaQrThamGiaRepository qrRepository;
    private final LuotThamGiaSuKienRepository attendanceRepository;
    private final CurrentUser currentUser;

    @Value("${aneon.f10.qr-secret}")
    private String qrSecret;
    @Value("${aneon.f10.check-in-base-url:http://localhost:8080/api/v1/f10/check-ins}")
    private String checkInBaseUrl;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> list(String keyword, EventStatus status, Pageable pageable) {
        return PageResponse.from(eventRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("name")), pattern)));
            }
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageable).map(this::toResponse));
    }

    @Override @Transactional(readOnly = true)
    public EventResponse get(UUID id) { return toResponse(findEvent(id)); }

    @Override
    @Transactional
    public EventResponse create(CreateEventRequest request) {
        validateRange(request.startAt(), request.endAt());
        String code = request.code().trim().toUpperCase();
        if (eventRepository.existsByCodeIgnoreCase(code)) throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        SuKien event = new SuKien();
        event.setCode(code);
        event.setName(request.name().trim());
        event.setType(request.type());
        event.setLocationId(request.locationId());
        event.setStartAt(request.startAt());
        event.setEndAt(request.endAt());
        event.setEstimatedBudget(request.estimatedBudget());
        event.setTargetAttendees(request.targetAttendees());
        event.setPublicVisible(request.publicVisible());
        event.setStatus(EventStatus.BAN_NHAP);
        event.setProposedBy(currentUser.getId());
        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse submit(UUID id, long version) {
        SuKien event = findEvent(id);
        checkVersion(event, version);
        requireStatus(event, EventStatus.BAN_NHAP);
        event.setStatus(EventStatus.CHO_DUYET);
        return toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse approve(UUID id, ApprovalRequest request) {
        SuKien event = findEvent(id);
        checkVersion(event, request.version());
        requireStatus(event, EventStatus.CHO_DUYET);
        if (request.decision() == EventStatus.DA_DUYET) {
            if (request.approvedBudget() == null) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "approvedBudget is required for approval");
            }
            event.setApprovedBudget(request.approvedBudget());
            event.setRejectionReason(null);
        } else if (request.decision() == EventStatus.TU_CHOI) {
            if (request.rejectionReason() == null || request.rejectionReason().isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "rejectionReason is required for rejection");
            }
            event.setRejectionReason(request.rejectionReason().trim());
            event.setApprovedBudget(null);
        } else {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
        event.setStatus(request.decision());
        event.setApprovedBy(currentUser.getId());
        event.setApprovedAt(Instant.now());
        return toResponse(eventRepository.save(event));
    }

    @Override @Transactional(readOnly = true)
    public List<ActivityResponse> listActivities(UUID eventId) {
        findEvent(eventId);
        return activityRepository.findByEventIdOrderByStartAtAsc(eventId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ActivityResponse addActivity(UUID eventId, CreateActivityRequest request) {
        SuKien event = findEvent(eventId);
        validateRange(request.startAt(), request.endAt());
        if (request.startAt().isBefore(event.getStartAt()) || request.endAt().isAfter(event.getEndAt())) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE, "Activity must be within event schedule");
        }
        if (activityRepository.hasScheduleConflict(request.locationId(), request.startAt(), request.endAt())) {
            throw new BusinessException(ErrorCode.SCHEDULE_CONFLICT);
        }
        HoatDongSuKien activity = new HoatDongSuKien();
        activity.setEvent(event);
        activity.setName(request.name().trim());
        activity.setStartAt(request.startAt());
        activity.setEndAt(request.endAt());
        activity.setLocationId(request.locationId());
        activity.setPersonInCharge(request.personInCharge());
        activity.setDescription(request.description());
        return toResponse(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public QrResponse generateQr(UUID eventId) {
        SuKien event = findEvent(eventId);
        if (event.getStatus() != EventStatus.DA_DUYET) throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        MaQrThamGia qr = new MaQrThamGia();
        qr.setEvent(event);
        qr.setTokenHash(hash(token));
        qr.setCheckInUrl(checkInBaseUrl);
        qr.setExpiresAt(event.getEndAt());
        qr.setActive(true);
        MaQrThamGia saved = qrRepository.save(qr);
        return new QrResponse(saved.getId(), eventId, token, saved.getCheckInUrl(), saved.getExpiresAt());
    }

    @Override
    @Transactional
    public CheckInResponse checkIn(CheckInRequest request) {
        MaQrThamGia qr = qrRepository.findByTokenHashAndActiveTrue(hash(request.token()))
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        if (qr.getExpiresAt() != null && !qr.getExpiresAt().isAfter(Instant.now())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        UUID eventId = qr.getEvent().getId();
        if (attendanceRepository.existsByEventIdAndCustomerId(eventId, request.customerId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CHECK_IN);
        }
        LuotThamGiaSuKien attendance = new LuotThamGiaSuKien();
        attendance.setEvent(qr.getEvent());
        attendance.setCustomerId(request.customerId());
        attendance.setQrCode(qr);
        attendance.setCheckInAt(Instant.now());
        attendance.setScanningDevice(request.scanningDevice());
        attendance.setCreatedBy(currentUser.getId());
        LuotThamGiaSuKien saved = attendanceRepository.save(attendance);
        return new CheckInResponse(saved.getId(), eventId, saved.getCustomerId(), saved.getCheckInAt());
    }

    private SuKien findEvent(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Event not found"));
    }
    private void validateRange(Instant start, Instant end) {
        if (!start.isBefore(end)) throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
    }
    private void requireStatus(SuKien event, EventStatus expected) {
        if (event.getStatus() != expected) throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
    }
    private void checkVersion(SuKien event, long version) {
        if (!event.getVersion().equals(version)) throw new OptimisticLockingFailureException("Event version mismatch");
    }
    private String hash(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(qrSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("Cannot initialize QR token hashing", exception);
        }
    }
    private EventResponse toResponse(SuKien e) {
        return new EventResponse(e.getId(), e.getCode(), e.getName(), e.getType(), e.getLocationId(), e.getStartAt(),
                e.getEndAt(), e.getEstimatedBudget(), e.getApprovedBudget(), e.getTargetAttendees(), e.getStatus(),
                e.getProposedBy(), e.getApprovedBy(), e.getApprovedAt(), e.getRejectionReason(),
                e.isPublicVisible(), e.getVersion());
    }
    private ActivityResponse toResponse(HoatDongSuKien a) {
        return new ActivityResponse(a.getId(), a.getEvent().getId(), a.getName(), a.getStartAt(), a.getEndAt(),
                a.getLocationId(), a.getPersonInCharge(), a.getDescription(), a.getVersion());
    }
}
