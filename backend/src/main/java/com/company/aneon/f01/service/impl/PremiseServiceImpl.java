package com.company.aneon.f01.service.impl;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.dto.request.ChangePremiseStatusRequest;
import com.company.aneon.f01.dto.request.CreatePremiseRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseLocationRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.PremiseResponse;
import com.company.aneon.f01.entity.KhuVuc;
import com.company.aneon.f01.entity.MatBang;
import com.company.aneon.f01.entity.ViTri;
import com.company.aneon.f01.integration.AccessScopePort;
import com.company.aneon.f01.integration.ContractQueryPort;
import com.company.aneon.f01.integration.DigitalMapPort;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.CuaHangSapRepository;
import com.company.aneon.f01.repository.F01Specifications;
import com.company.aneon.f01.repository.KhuVucRepository;
import com.company.aneon.f01.repository.MatBangRepository;
import com.company.aneon.f01.repository.ViTriRepository;
import com.company.aneon.f01.service.HistoryService;
import com.company.aneon.f01.service.PremiseService;
import com.company.aneon.f01.service.StatusTransitionService;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PremiseServiceImpl implements PremiseService {

    private final MatBangRepository premiseRepository;
    private final KhuVucRepository zoneRepository;
    private final ViTriRepository locationRepository;
    private final CuaHangSapRepository storeRepository;
    private final F01Mapper mapper;
    private final HistoryService historyService;
    private final StatusTransitionService transitionService;
    private final AccessScopePort accessScopePort;
    private final ContractQueryPort contractQueryPort;
    private final DigitalMapPort digitalMapPort;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PremiseResponse> list(
            String keyword, UUID floorId, UUID zoneId, PremiseStatus status, Pageable pageable) {
        Set<UUID> allowedZoneIds = accessScopePort.getAllowedZoneIds();
        return PageResponse.from(premiseRepository
                .findAll(F01Specifications.premises(
                        keyword, floorId, zoneId, status, allowedZoneIds), pageable)
                .map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PremiseResponse get(UUID id) {
        return toResponse(findAccessible(id));
    }

    @Override
    @Transactional
    public PremiseResponse create(CreatePremiseRequest request) {
        String code = normalizeCode(request.code());
        ensureCodeAvailable(code, null);
        KhuVuc zone = findZone(request.zoneId());
        ensureZoneAccessible(zone.getId());
        ViTri location = resolveLocation(request.locationId(), zone);

        MatBang premise = new MatBang();
        premise.setCode(code);
        premise.setName(request.name().trim());
        premise.setZone(zone);
        premise.setTotalArea(request.totalArea());
        premise.setType(request.type());
        premise.setStatus(PremiseStatus.TRONG);
        premise.setLocation(location);
        premise.setDescription(request.description());

        MatBang saved = premiseRepository.save(premise);
        PremiseResponse response = mapper.toPremiseResponse(saved, BigDecimal.ZERO);
        historyService.record("MAT_BANG", saved.getId(), "CREATE", null, snapshot(saved), null);
        return response;
    }

    @Override
    @Transactional
    public PremiseResponse update(UUID id, UpdatePremiseRequest request) {
        MatBang premise = findAccessible(id);
        checkVersion(premise.getVersion(), request.version());
        String code = normalizeCode(request.code());
        ensureCodeAvailable(code, id);
        KhuVuc zone = findZone(request.zoneId());
        ensureZoneAccessible(zone.getId());
        BigDecimal allocatedArea = storeRepository.sumAreaByPremise(id);
        if (allocatedArea.compareTo(request.totalArea()) > 0) {
            throw new BusinessException(
                    ErrorCode.AREA_EXCEEDED,
                    "Diện tích mới nhỏ hơn tổng diện tích cửa hàng đã phân bổ");
        }
        ViTri location = resolveLocation(request.locationId(), zone);
        Map<String, Object> oldValue = snapshot(premise);

        premise.setCode(code);
        premise.setName(request.name().trim());
        premise.setZone(zone);
        premise.setTotalArea(request.totalArea());
        premise.setType(request.type());
        premise.setLocation(location);
        premise.setDescription(request.description());

        MatBang saved = premiseRepository.save(premise);
        historyService.record("MAT_BANG", id, "UPDATE", oldValue, snapshot(saved), null);
        return mapper.toPremiseResponse(saved, allocatedArea);
    }

    @Override
    @Transactional
    public PremiseResponse changeStatus(UUID id, ChangePremiseStatusRequest request) {
        MatBang premise = findAccessible(id);
        checkVersion(premise.getVersion(), request.version());
        transitionService.validatePremise(premise.getStatus(), request.status());
        if (request.status() == PremiseStatus.TRONG && contractQueryPort.hasActiveContract(id)) {
            throw new BusinessException(
                    ErrorCode.INVALID_STATUS_TRANSITION,
                    "Mặt bằng còn hợp đồng hiệu lực nên không thể chuyển về TRONG");
        }
        Map<String, Object> oldValue = snapshot(premise);
        premise.setStatus(request.status());
        MatBang saved = premiseRepository.save(premise);
        historyService.record("MAT_BANG", id, "CHANGE_STATUS", oldValue, snapshot(saved), request.reason());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PremiseResponse updateLocation(UUID id, UpdatePremiseLocationRequest request) {
        MatBang premise = findAccessible(id);
        checkVersion(premise.getVersion(), request.version());
        ViTri location = resolveLocation(request.locationId(), premise.getZone());
        Map<String, Object> oldValue = snapshot(premise);
        premise.setLocation(location);
        MatBang saved = premiseRepository.save(premise);
        historyService.record("MAT_BANG", id, "UPDATE_LOCATION", oldValue, snapshot(saved), request.reason());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PremiseResponse syncStatusFromContract(UUID id) {
        MatBang premise = premiseRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy mặt bằng"));
        ensureZoneAccessible(premise.getZone().getId());
        if (premise.getStatus() == PremiseStatus.BAO_TRI
                || premise.getStatus() == PremiseStatus.NGUNG_KHAI_THAC) {
            return toResponse(premise);
        }
        PremiseStatus requested = contractQueryPort.hasActiveContract(id)
                ? PremiseStatus.DA_THUE
                : PremiseStatus.TRONG;
        if (premise.getStatus() != requested) {
            Map<String, Object> oldValue = snapshot(premise);
            transitionService.validatePremise(premise.getStatus(), requested);
            premise.setStatus(requested);
            premiseRepository.save(premise);
            historyService.record(
                    "MAT_BANG", id, "SYNC_CONTRACT_STATUS", oldValue, snapshot(premise), "Đồng bộ từ hợp đồng");
        }
        return toResponse(premise);
    }

    @Override
    @Transactional(readOnly = true)
    public CodeAvailabilityResponse checkCodeAvailable(String code, UUID excludeId) {
        String normalized = normalizeCode(code);
        boolean available = excludeId == null
                ? !premiseRepository.existsByCodeIgnoreCase(normalized)
                : !premiseRepository.existsByCodeIgnoreCaseAndIdNot(normalized, excludeId);
        return new CodeAvailabilityResponse(normalized, available);
    }

    private MatBang findAccessible(UUID id) {
        MatBang premise = premiseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy mặt bằng"));
        ensureZoneAccessible(premise.getZone().getId());
        return premise;
    }

    private KhuVuc findZone(UUID id) {
        return zoneRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khu vực hoạt động"));
    }

    private ViTri resolveLocation(UUID locationId, KhuVuc zone) {
        if (locationId == null) {
            return null;
        }
        ViTri location = locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy vị trí"));
        if (!location.getZone().getId().equals(zone.getId()) || !digitalMapPort.isLocationMappable(locationId)) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION);
        }
        return location;
    }

    private void ensureCodeAvailable(String code, UUID excludeId) {
        if (!checkCodeAvailable(code, excludeId).available()) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE, "Mã mặt bằng đã tồn tại");
        }
    }

    private void ensureZoneAccessible(UUID zoneId) {
        Set<UUID> allowed = accessScopePort.getAllowedZoneIds();
        if (allowed != null && !allowed.isEmpty() && !allowed.contains(zoneId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private void checkVersion(Long actual, Long requested) {
        if (!actual.equals(requested)) {
            throw new OptimisticLockingFailureException("Premise version mismatch");
        }
    }

    private PremiseResponse toResponse(MatBang premise) {
        return mapper.toPremiseResponse(premise, storeRepository.sumAreaByPremise(premise.getId()));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private Map<String, Object> snapshot(MatBang premise) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", premise.getId());
        value.put("code", premise.getCode());
        value.put("name", premise.getName());
        value.put("zoneId", premise.getZone().getId());
        value.put("totalArea", premise.getTotalArea());
        value.put("type", premise.getType());
        value.put("status", premise.getStatus());
        value.put("locationId", premise.getLocation() == null ? null : premise.getLocation().getId());
        value.put("description", premise.getDescription());
        return value;
    }
}

