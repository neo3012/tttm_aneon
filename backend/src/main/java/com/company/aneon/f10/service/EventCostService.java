package com.company.aneon.f10.service;
import com.company.aneon.f10.dto.request.CreateEventCostRequest;
import com.company.aneon.f10.dto.response.EventCostResponse;
import com.company.aneon.f10.dto.response.EventRoiResponse;
import java.util.List;
import java.util.UUID;
public interface EventCostService {
    List<EventCostResponse> list(UUID eventId);
    EventCostResponse create(UUID eventId, CreateEventCostRequest request);
    EventRoiResponse roi(UUID eventId);
}
