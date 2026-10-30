package com.company.aneon.f01.dto.request;

import com.company.aneon.f01.constant.StoreStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeStoreStatusRequest(
        @NotNull StoreStatus status,
        @Size(max = 500) String reason,
        @NotNull Long version) {
}

