package com.company.aneon.f10.integration;
import java.util.UUID;
public interface LoyaltyPort {
    String getCustomerTier(UUID customerId);
    void grantPoints(UUID customerId, long points, UUID referenceId);
}
