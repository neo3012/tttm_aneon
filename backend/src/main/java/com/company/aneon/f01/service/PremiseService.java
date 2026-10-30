package com.company.aneon.f01.service;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.dto.request.ChangePremiseStatusRequest;
import com.company.aneon.f01.dto.request.CreatePremiseRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseLocationRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.PremiseResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface PremiseService {

    PageResponse<PremiseResponse> list(
            String keyword, UUID floorId, UUID zoneId, PremiseStatus status, Pageable pageable);

    PremiseResponse get(UUID id);

    PremiseResponse create(CreatePremiseRequest request);

    PremiseResponse update(UUID id, UpdatePremiseRequest request);

    PremiseResponse changeStatus(UUID id, ChangePremiseStatusRequest request);

    PremiseResponse updateLocation(UUID id, UpdatePremiseLocationRequest request);

    PremiseResponse syncStatusFromContract(UUID id);

    CodeAvailabilityResponse checkCodeAvailable(String code, UUID excludeId);
}

