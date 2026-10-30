package com.company.aneon.f10.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.common.security.CurrentUser;
import com.company.aneon.f10.constant.EventStatus;
import com.company.aneon.f10.dto.request.ApprovalRequest;
import com.company.aneon.f10.dto.request.CreateActivityRequest;
import com.company.aneon.f10.dto.request.CreateEventRequest;
import com.company.aneon.f10.entity.SuKien;
import com.company.aneon.f10.repository.HoatDongSuKienRepository;
import com.company.aneon.f10.repository.LuotThamGiaSuKienRepository;
import com.company.aneon.f10.repository.MaQrThamGiaRepository;
import com.company.aneon.f10.repository.SuKienRepository;
import com.company.aneon.f10.service.impl.EventServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock private SuKienRepository eventRepository;
    @Mock private HoatDongSuKienRepository activityRepository;
    @Mock private MaQrThamGiaRepository qrRepository;
    @Mock private LuotThamGiaSuKienRepository attendanceRepository;
    @Mock private CurrentUser currentUser;
    private EventServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EventServiceImpl(
                eventRepository, activityRepository, qrRepository, attendanceRepository, currentUser);
    }

    @Test
    void createStartsAsDraftAndUsesCurrentUser() {
        UUID actor = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(actor);
        when(eventRepository.save(any(SuKien.class))).thenAnswer(invocation -> {
            SuKien event = invocation.getArgument(0);
            event.setId(UUID.randomUUID());
            event.setVersion(0L);
            return event;
        });
        Instant start = Instant.now().plusSeconds(3600);

        var result = service.create(new CreateEventRequest(
                " ev-01 ", "Opening", null, null, start, start.plusSeconds(3600),
                BigDecimal.TEN, 100, true));

        assertThat(result.code()).isEqualTo("EV-01");
        assertThat(result.status()).isEqualTo(EventStatus.BAN_NHAP);
        assertThat(result.proposedBy()).isEqualTo(actor);
    }

    @Test
    void approvalRequiresBudget() {
        SuKien event = event(EventStatus.CHO_DUYET);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.approve(
                event.getId(), new ApprovalRequest(EventStatus.DA_DUYET, null, null, 0L)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.VALIDATION_ERROR);
    }

    @Test
    void activityMustStayWithinEventSchedule() {
        SuKien event = event(EventStatus.BAN_NHAP);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.addActivity(event.getId(), new CreateActivityRequest(
                "Setup", event.getStartAt().minusSeconds(1), event.getStartAt().plusSeconds(60),
                UUID.randomUUID(), null, null)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_DATE_RANGE);
    }

    @Test
    void activityRejectsOverlappingLocation() {
        SuKien event = event(EventStatus.BAN_NHAP);
        UUID locationId = UUID.randomUUID();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(activityRepository.hasScheduleConflict(locationId, event.getStartAt(), event.getEndAt()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.addActivity(event.getId(), new CreateActivityRequest(
                "Show", event.getStartAt(), event.getEndAt(), locationId, null, null)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.SCHEDULE_CONFLICT);
    }

    private SuKien event(EventStatus status) {
        SuKien event = new SuKien();
        event.setId(UUID.randomUUID());
        event.setVersion(0L);
        event.setCode("EV-01");
        event.setName("Opening");
        event.setStatus(status);
        event.setStartAt(Instant.now().plusSeconds(3600));
        event.setEndAt(Instant.now().plusSeconds(7200));
        return event;
    }
}
