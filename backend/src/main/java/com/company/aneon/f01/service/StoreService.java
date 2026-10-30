package com.company.aneon.f01.service;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.dto.request.ChangeStoreStatusRequest;
import com.company.aneon.f01.dto.request.CreateStoreRequest;
import com.company.aneon.f01.dto.request.UpdateStoreRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.StoreResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    PageResponse<StoreResponse> list(
            String keyword, UUID premiseId, StoreStatus status, Pageable pageable);

    StoreResponse get(UUID id);

    StoreResponse create(CreateStoreRequest request);

    StoreResponse update(UUID id, UpdateStoreRequest request);

    StoreResponse changeStatus(UUID id, ChangeStoreStatusRequest request);

    CodeAvailabilityResponse checkCodeAvailable(String code, UUID excludeId);
}

