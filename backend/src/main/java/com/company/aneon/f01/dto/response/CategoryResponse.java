package com.company.aneon.f01.dto.response;

import java.util.UUID;

public record CategoryResponse(UUID id, String code, String name, String description) {
}

