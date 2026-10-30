package com.company.aneon.f01.integration;

import java.util.UUID;

public interface ContractQueryPort {
    boolean hasActiveContract(UUID premiseId);
}

