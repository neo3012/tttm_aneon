package com.company.aneon.f10.config;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f10.integration.InvoiceQueryPort;
import com.company.aneon.f10.integration.LoyaltyPort;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class F10ModuleConfig {
    @Bean @ConditionalOnMissingBean InvoiceQueryPort invoiceQueryPort() {
        return id -> { throw new BusinessException(ErrorCode.NOT_FOUND, "Invoice integration is not configured"); };
    }
    @Bean @ConditionalOnMissingBean LoyaltyPort loyaltyPort() {
        return new LoyaltyPort() {
            public String getCustomerTier(UUID id) { return null; }
            public void grantPoints(UUID id, long points, UUID referenceId) { }
        };
    }
}
