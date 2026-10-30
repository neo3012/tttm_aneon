package com.company.aneon.f10.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f10.constant.EventStatus;
import com.company.aneon.f10.dto.request.ApprovalRequest;
import com.company.aneon.f10.dto.request.CreateActivityRequest;
import com.company.aneon.f10.dto.request.CreateEventRequest;
import com.company.aneon.f10.dto.response.ActivityResponse;
import com.company.aneon.f10.dto.response.EventResponse;
import com.company.aneon.f10.dto.response.QrResponse;
import com.company.aneon.f10.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/f10/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    @Operation(summary = "List events")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<PageResponse<EventResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EventStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(200, "Events loaded", eventService.list(keyword, status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<EventResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(200, "Event loaded", eventService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<EventResponse>> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Event created", eventService.create(request)));
    }

    @PatchMapping("/{id}/submission")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<EventResponse> submit(@PathVariable UUID id, @RequestParam @PositiveOrZero long version) {
        return ApiResponse.success(200, "Event submitted", eventService.submit(id, version));
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ApiResponse<EventResponse> approve(
            @PathVariable UUID id, @Valid @RequestBody ApprovalRequest request) {
        return ApiResponse.success(200, "Approval processed", eventService.approve(id, request));
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<List<ActivityResponse>> activities(@PathVariable UUID id) {
        return ApiResponse.success(200, "Activities loaded", eventService.listActivities(id));
    }

    @PostMapping("/{id}/activities")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<ActivityResponse>> addActivity(
            @PathVariable UUID id, @Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Activity created", eventService.addActivity(id, request)));
    }

    @PostMapping("/{id}/qr-codes")
    @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<QrResponse>> generateQr(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "QR code generated", eventService.generateQr(id)));
    }
}
