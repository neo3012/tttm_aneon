package com.company.aneon.f01.service;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.dto.response.HistoryResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface HistoryService {

    void record(String targetType, UUID targetId, String action, Object oldValue, Object newValue, String reason);

    PageResponse<HistoryResponse> list(
            String targetType, UUID targetId, Instant from, Instant to, Pageable pageable);
}

