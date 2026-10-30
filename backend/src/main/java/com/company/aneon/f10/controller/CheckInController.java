package com.company.aneon.f10.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.f10.dto.request.CheckInRequest;
import com.company.aneon.f10.dto.response.CheckInResponse;
import com.company.aneon.f10.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/f10/check-ins")
public class CheckInController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Check-in completed", eventService.checkIn(request)));
    }
}
