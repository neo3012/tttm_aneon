package com.company.aneon.f01.integration;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessScopeAdapter implements AccessScopePort {

    @Override
    public Set<UUID> getAllowedZoneIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        boolean admin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (admin) {
            return Set.of();
        }
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Token không chứa phạm vi dữ liệu F01");
        }
        Collection<String> zoneIds = jwt.getClaimAsStringList("zone_ids");
        if (zoneIds == null || zoneIds.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Người dùng chưa được cấp phạm vi khu vực");
        }
        try {
            return zoneIds.stream().map(UUID::fromString).collect(Collectors.toUnmodifiableSet());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Phạm vi khu vực trong token không hợp lệ");
        }
    }
}
