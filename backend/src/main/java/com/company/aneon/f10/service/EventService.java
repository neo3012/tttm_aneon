package com.company.aneon.f10.service;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f10.constant.EventStatus;
import com.company.aneon.f10.dto.request.ApprovalRequest;
import com.company.aneon.f10.dto.request.CheckInRequest;
import com.company.aneon.f10.dto.request.CreateActivityRequest;
import com.company.aneon.f10.dto.request.CreateEventRequest;
import com.company.aneon.f10.dto.response.ActivityResponse;
import com.company.aneon.f10.dto.response.CheckInResponse;
import com.company.aneon.f10.dto.response.EventResponse;
import com.company.aneon.f10.dto.response.QrResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface EventService {
    PageResponse<EventResponse> list(String keyword, EventStatus status, Pageable pageable);
    EventResponse get(UUID id);
    EventResponse create(CreateEventRequest request);
    EventResponse submit(UUID id, long version);
    EventResponse approve(UUID id, ApprovalRequest request);
    List<ActivityResponse> listActivities(UUID eventId);
    ActivityResponse addActivity(UUID eventId, CreateActivityRequest request);
    QrResponse generateQr(UUID eventId);
    CheckInResponse checkIn(CheckInRequest request);
}
