package com.company.aneon.f01.service.impl;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.common.security.CurrentUser;
import com.company.aneon.f01.dto.response.HistoryResponse;
import com.company.aneon.f01.entity.LichSuMatBangVaCuaHang;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.LichSuMatBangVaCuaHangRepository;
import com.company.aneon.f01.service.HistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final LichSuMatBangVaCuaHangRepository repository;
    private final F01Mapper mapper;
    private final ObjectMapper objectMapper;
    private final CurrentUser currentUser;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(
            String targetType,
            UUID targetId,
            String action,
            Object oldValue,
            Object newValue,
            String reason) {
        LichSuMatBangVaCuaHang history = new LichSuMatBangVaCuaHang();
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        history.setAction(action);
        history.setOldValueJson(toJson(oldValue));
        history.setNewValueJson(toJson(newValue));
        history.setChangedBy(currentUser.getId());
        history.setCreatedBy(currentUser.getId());
        history.setReason(reason);
        repository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<HistoryResponse> list(
            String targetType, UUID targetId, Instant from, Instant to, Pageable pageable) {
        Instant effectiveFrom = from == null ? Instant.EPOCH : from;
        Instant effectiveTo = to == null ? Instant.now() : to;
        if (effectiveFrom.isAfter(effectiveTo)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
        return PageResponse.from(repository
                .findByTargetTypeAndTargetIdAndCreatedAtBetween(
                        targetType, targetId, effectiveFrom, effectiveTo, pageable)
                .map(mapper::toHistoryResponse));
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Không thể ghi lịch sử thay đổi");
        }
    }
}

