package com.company.aneon.f01.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.service.impl.StatusTransitionServiceImpl;
import org.junit.jupiter.api.Test;

class StatusTransitionServiceImplTest {

    private final StatusTransitionServiceImpl service = new StatusTransitionServiceImpl();

    @Test
    void validatePremise_validTransition_doesNotThrow() {
        assertThatCode(() -> service.validatePremise(PremiseStatus.TRONG, PremiseStatus.BAO_TRI))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePremise_invalidTransition_throwsBusinessException() {
        assertThatThrownBy(() ->
                        service.validatePremise(PremiseStatus.NGUNG_KHAI_THAC, PremiseStatus.DA_THUE))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.INVALID_STATUS_TRANSITION));
    }
}

