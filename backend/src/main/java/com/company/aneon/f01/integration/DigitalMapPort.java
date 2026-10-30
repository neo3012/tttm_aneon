package com.company.aneon.f01.integration;

import java.util.UUID;

public interface DigitalMapPort {
    boolean isLocationMappable(UUID locationId);
}

