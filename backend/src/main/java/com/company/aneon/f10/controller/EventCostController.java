package com.company.aneon.f10.controller;
import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.f10.dto.request.CreateEventCostRequest;
import com.company.aneon.f10.dto.response.EventCostResponse;
import com.company.aneon.f10.dto.response.EventRoiResponse;
import com.company.aneon.f10.service.EventCostService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/f10/events/{eventId}")
public class EventCostController {
    private final EventCostService service;
    @GetMapping("/costs") @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<List<EventCostResponse>> list(@PathVariable UUID eventId) {
        return ApiResponse.success(200, "Event costs loaded", service.list(eventId));
    }
    @PostMapping("/costs") @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<EventCostResponse>> create(@PathVariable UUID eventId,
            @Valid @RequestBody CreateEventCostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "Event cost created", service.create(eventId, request)));
    }
    @GetMapping("/roi") @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ApiResponse<EventRoiResponse> roi(@PathVariable UUID eventId) {
        return ApiResponse.success(200, "Event ROI calculated", service.roi(eventId));
    }
}
