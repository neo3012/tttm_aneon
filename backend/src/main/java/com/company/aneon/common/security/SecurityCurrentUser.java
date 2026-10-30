package com.company.aneon.common.security;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUser implements CurrentUser {

    @Override
    public UUID getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String subject = authentication == null ? "system" : authentication.getName();
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            return UUID.nameUUIDFromBytes(subject.getBytes(StandardCharsets.UTF_8));
        }
    }
}

