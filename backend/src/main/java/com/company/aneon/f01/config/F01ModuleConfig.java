package com.company.aneon.f01.config;

import com.company.aneon.f01.integration.ContractQueryPort;
import com.company.aneon.f01.integration.DigitalMapPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class F01ModuleConfig {

    @Bean
    @ConditionalOnMissingBean
    ContractQueryPort contractQueryPort() {
        return premiseId -> false;
    }

    @Bean
    @ConditionalOnMissingBean
    DigitalMapPort digitalMapPort() {
        return locationId -> true;
    }
}

