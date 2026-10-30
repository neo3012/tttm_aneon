package com.company.aneon.f01.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.dto.request.CreateStoreRequest;
import com.company.aneon.f01.entity.KhuVuc;
import com.company.aneon.f01.entity.MatBang;
import com.company.aneon.f01.integration.AccessScopePort;
import com.company.aneon.f01.integration.DigitalMapPort;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.CuaHangSapRepository;
import com.company.aneon.f01.repository.MatBangRepository;
import com.company.aneon.f01.repository.NganhHangRepository;
import com.company.aneon.f01.repository.ViTriRepository;
import com.company.aneon.f01.service.impl.StoreServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private CuaHangSapRepository storeRepository;
    @Mock
    private MatBangRepository premiseRepository;
    @Mock
    private NganhHangRepository categoryRepository;
    @Mock
    private ViTriRepository locationRepository;
    @Mock
    private HistoryService historyService;
    @Mock
    private StatusTransitionService transitionService;
    @Mock
    private AccessScopePort accessScopePort;
    @Mock
    private DigitalMapPort digitalMapPort;

    private StoreServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StoreServiceImpl(
                storeRepository,
                premiseRepository,
                categoryRepository,
                locationRepository,
                new F01Mapper(),
                historyService,
                transitionService,
                accessScopePort,
                digitalMapPort);
        when(accessScopePort.getAllowedZoneIds()).thenReturn(Set.of());
    }

    @Test
    void create_areaExceedsPremise_throwsAreaExceededException() {
        UUID premiseId = UUID.randomUUID();
        KhuVuc zone = new KhuVuc();
        zone.setId(UUID.randomUUID());
        MatBang premise = new MatBang();
        premise.setId(premiseId);
        premise.setZone(zone);
        premise.setTotalArea(new BigDecimal("100.00"));

        when(storeRepository.existsByCodeIgnoreCase("CH-001")).thenReturn(false);
        when(premiseRepository.findByIdForUpdate(premiseId)).thenReturn(Optional.of(premise));
        when(storeRepository.sumAreaByPremiseExcluding(any(), any()))
                .thenReturn(new BigDecimal("90.00"));

        CreateStoreRequest request = new CreateStoreRequest(
                "ch-001",
                "Cửa hàng 1",
                premiseId,
                new BigDecimal("20.00"),
                null,
                null,
                StoreStatus.CHO_THUE,
                null,
                null,
                true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AREA_EXCEEDED));

        verify(storeRepository, never()).save(any());
    }
}

