package com.company.aneon.common.config;

import com.company.aneon.common.security.CurrentUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    AuditorAware<UUID> auditorAware(CurrentUser currentUser) {
        return () -> Optional.of(currentUser.getId());
    }
}

