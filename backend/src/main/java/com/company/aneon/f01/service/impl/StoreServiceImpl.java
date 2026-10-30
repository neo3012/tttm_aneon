package com.company.aneon.f01.service.impl;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.dto.request.ChangeStoreStatusRequest;
import com.company.aneon.f01.dto.request.CreateStoreRequest;
import com.company.aneon.f01.dto.request.UpdateStoreRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.StoreResponse;
import com.company.aneon.f01.entity.CuaHangSap;
import com.company.aneon.f01.entity.MatBang;
import com.company.aneon.f01.entity.NganhHang;
import com.company.aneon.f01.entity.ViTri;
import com.company.aneon.f01.integration.AccessScopePort;
import com.company.aneon.f01.integration.DigitalMapPort;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.CuaHangSapRepository;
import com.company.aneon.f01.repository.F01Specifications;
import com.company.aneon.f01.repository.MatBangRepository;
import com.company.aneon.f01.repository.NganhHangRepository;
import com.company.aneon.f01.repository.ViTriRepository;
import com.company.aneon.f01.service.HistoryService;
import com.company.aneon.f01.service.StatusTransitionService;
import com.company.aneon.f01.service.StoreService;
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
public class StoreServiceImpl implements StoreService {

    private static final UUID NO_EXCLUDED_STORE = new UUID(0L, 0L);

    private final CuaHangSapRepository storeRepository;
    private final MatBangRepository premiseRepository;
    private final NganhHangRepository categoryRepository;
    private final ViTriRepository locationRepository;
    private final F01Mapper mapper;
    private final HistoryService historyService;
    private final StatusTransitionService transitionService;
    private final AccessScopePort accessScopePort;
    private final DigitalMapPort digitalMapPort;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> list(
            String keyword, UUID premiseId, StoreStatus status, Pageable pageable) {
        return PageResponse.from(storeRepository
                .findAll(F01Specifications.stores(
                        keyword, premiseId, status, accessScopePort.getAllowedZoneIds()), pageable)
                .map(mapper::toStoreResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse get(UUID id) {
        return mapper.toStoreResponse(findAccessible(id));
    }

    @Override
    @Transactional
    public StoreResponse create(CreateStoreRequest request) {
        String code = normalizeCode(request.code());
        ensureCodeAvailable(code, null);
        MatBang premise = findLockedPremise(request.premiseId());
        validateArea(premise, request.area(), NO_EXCLUDED_STORE);
        ViTri location = resolveLocation(request.locationId(), premise);
        NganhHang category = resolveCategory(request.categoryId());

        CuaHangSap store = new CuaHangSap();
        store.setCode(code);
        store.setName(request.name().trim());
        store.setPremise(premise);
        store.setArea(request.area());
        store.setCategory(category);
        store.setLocation(location);
        store.setStatus(request.status());
        store.setOperatingHours(request.operatingHours());
        store.setDescription(request.description());
        store.setPublicVisible(request.publicVisible());

        CuaHangSap saved = storeRepository.save(store);
        historyService.record("CUA_HANG", saved.getId(), "CREATE", null, snapshot(saved), null);
        return mapper.toStoreResponse(saved);
    }

    @Override
    @Transactional
    public StoreResponse update(UUID id, UpdateStoreRequest request) {
        CuaHangSap store = findAccessible(id);
        checkVersion(store.getVersion(), request.version());
        String code = normalizeCode(request.code());
        ensureCodeAvailable(code, id);
        MatBang premise = findLockedPremise(request.premiseId());
        validateArea(premise, request.area(), id);
        ViTri location = resolveLocation(request.locationId(), premise);
        NganhHang category = resolveCategory(request.categoryId());
        Map<String, Object> oldValue = snapshot(store);

        store.setCode(code);
        store.setName(request.name().trim());
        store.setPremise(premise);
        store.setArea(request.area());
        store.setCategory(category);
        store.setLocation(location);
        store.setOperatingHours(request.operatingHours());
        store.setDescription(request.description());
        store.setPublicVisible(request.publicVisible());

        CuaHangSap saved = storeRepository.save(store);
        historyService.record("CUA_HANG", id, "UPDATE", oldValue, snapshot(saved), null);
        return mapper.toStoreResponse(saved);
    }

    @Override
    @Transactional
    public StoreResponse changeStatus(UUID id, ChangeStoreStatusRequest request) {
        CuaHangSap store = findAccessible(id);
        checkVersion(store.getVersion(), request.version());
        transitionService.validateStore(store.getStatus(), request.status());
        Map<String, Object> oldValue = snapshot(store);
        store.setStatus(request.status());
        CuaHangSap saved = storeRepository.save(store);
        historyService.record("CUA_HANG", id, "CHANGE_STATUS", oldValue, snapshot(saved), request.reason());
        return mapper.toStoreResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CodeAvailabilityResponse checkCodeAvailable(String code, UUID excludeId) {
        String normalized = normalizeCode(code);
        boolean available = excludeId == null
                ? !storeRepository.existsByCodeIgnoreCase(normalized)
                : !storeRepository.existsByCodeIgnoreCaseAndIdNot(normalized, excludeId);
        return new CodeAvailabilityResponse(normalized, available);
    }

    private CuaHangSap findAccessible(UUID id) {
        CuaHangSap store = storeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy cửa hàng"));
        ensureZoneAccessible(store.getPremise().getZone().getId());
        return store;
    }

    private MatBang findLockedPremise(UUID id) {
        MatBang premise = premiseRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy mặt bằng"));
        ensureZoneAccessible(premise.getZone().getId());
        return premise;
    }

    private void validateArea(MatBang premise, BigDecimal requestedArea, UUID excludeId) {
        BigDecimal allocated = storeRepository.sumAreaByPremiseExcluding(premise.getId(), excludeId);
        if (allocated.add(requestedArea).compareTo(premise.getTotalArea()) > 0) {
            throw new BusinessException(
                    ErrorCode.AREA_EXCEEDED,
                    "Tổng diện tích cửa hàng vượt quá diện tích mặt bằng khả dụng");
        }
    }

    private ViTri resolveLocation(UUID id, MatBang premise) {
        if (id == null) {
            return null;
        }
        ViTri location = locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy vị trí"));
        if (!location.getZone().getId().equals(premise.getZone().getId())
                || !digitalMapPort.isLocationMappable(id)) {
            throw new BusinessException(ErrorCode.INVALID_LOCATION);
        }
        return location;
    }

    private NganhHang resolveCategory(UUID id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành hàng"));
    }

    private void ensureCodeAvailable(String code, UUID excludeId) {
        if (!checkCodeAvailable(code, excludeId).available()) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE, "Mã cửa hàng đã tồn tại");
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
            throw new OptimisticLockingFailureException("Store version mismatch");
        }
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private Map<String, Object> snapshot(CuaHangSap store) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", store.getId());
        value.put("code", store.getCode());
        value.put("name", store.getName());
        value.put("premiseId", store.getPremise().getId());
        value.put("area", store.getArea());
        value.put("categoryId", store.getCategory() == null ? null : store.getCategory().getId());
        value.put("locationId", store.getLocation() == null ? null : store.getLocation().getId());
        value.put("status", store.getStatus());
        value.put("operatingHours", store.getOperatingHours());
        value.put("publicVisible", store.isPublicVisible());
        return value;
    }
}

