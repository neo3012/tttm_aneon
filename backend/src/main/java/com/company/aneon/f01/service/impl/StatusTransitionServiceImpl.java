package com.company.aneon.f01.service.impl;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.service.StatusTransitionService;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StatusTransitionServiceImpl implements StatusTransitionService {

    private static final Map<PremiseStatus, Set<PremiseStatus>> PREMISE_TRANSITIONS =
            new EnumMap<>(PremiseStatus.class);
    private static final Map<StoreStatus, Set<StoreStatus>> STORE_TRANSITIONS =
            new EnumMap<>(StoreStatus.class);

    static {
        PREMISE_TRANSITIONS.put(
                PremiseStatus.TRONG,
                EnumSet.of(PremiseStatus.DANG_THUE, PremiseStatus.DA_THUE,
                        PremiseStatus.BAO_TRI, PremiseStatus.NGUNG_KHAI_THAC));
        PREMISE_TRANSITIONS.put(
                PremiseStatus.DANG_THUE,
                EnumSet.of(PremiseStatus.TRONG, PremiseStatus.DA_THUE, PremiseStatus.NGUNG_KHAI_THAC));
        PREMISE_TRANSITIONS.put(
                PremiseStatus.DA_THUE,
                EnumSet.of(PremiseStatus.TRONG, PremiseStatus.BAO_TRI, PremiseStatus.NGUNG_KHAI_THAC));
        PREMISE_TRANSITIONS.put(
                PremiseStatus.BAO_TRI,
                EnumSet.of(PremiseStatus.TRONG, PremiseStatus.NGUNG_KHAI_THAC));
        PREMISE_TRANSITIONS.put(PremiseStatus.NGUNG_KHAI_THAC, EnumSet.of(PremiseStatus.TRONG));

        STORE_TRANSITIONS.put(
                StoreStatus.TRONG,
                EnumSet.of(StoreStatus.CHO_THUE, StoreStatus.DANG_SUA_CHUA, StoreStatus.TAM_NGUNG));
        STORE_TRANSITIONS.put(
                StoreStatus.CHO_THUE,
                EnumSet.of(StoreStatus.DA_THUE, StoreStatus.DANG_SUA_CHUA, StoreStatus.TAM_NGUNG));
        STORE_TRANSITIONS.put(
                StoreStatus.DA_THUE,
                EnumSet.of(StoreStatus.CHO_THUE, StoreStatus.DANG_SUA_CHUA, StoreStatus.TAM_NGUNG));
        STORE_TRANSITIONS.put(
                StoreStatus.DANG_SUA_CHUA,
                EnumSet.of(StoreStatus.TRONG, StoreStatus.CHO_THUE, StoreStatus.TAM_NGUNG));
        STORE_TRANSITIONS.put(
                StoreStatus.TAM_NGUNG,
                EnumSet.of(StoreStatus.TRONG, StoreStatus.CHO_THUE, StoreStatus.DANG_SUA_CHUA));
    }

    @Override
    public void validatePremise(PremiseStatus current, PremiseStatus requested) {
        validate(current, requested, PREMISE_TRANSITIONS);
    }

    @Override
    public void validateStore(StoreStatus current, StoreStatus requested) {
        validate(current, requested, STORE_TRANSITIONS);
    }

    private <T extends Enum<T>> void validate(T current, T requested, Map<T, Set<T>> transitions) {
        if (current == requested) {
            return;
        }
        if (!transitions.getOrDefault(current, Set.of()).contains(requested)) {
            throw new BusinessException(
                    ErrorCode.INVALID_STATUS_TRANSITION,
                    "Không thể chuyển trạng thái từ " + current + " sang " + requested);
        }
    }
}

