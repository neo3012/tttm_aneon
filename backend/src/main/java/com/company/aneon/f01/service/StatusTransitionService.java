package com.company.aneon.f01.service;

import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.constant.StoreStatus;

public interface StatusTransitionService {
    void validatePremise(PremiseStatus current, PremiseStatus requested);

    void validateStore(StoreStatus current, StoreStatus requested);
}

