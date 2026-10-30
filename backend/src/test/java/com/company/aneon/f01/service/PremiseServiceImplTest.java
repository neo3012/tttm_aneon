package com.company.aneon.f01.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f01.dto.request.CreatePremiseRequest;
import com.company.aneon.f01.entity.KhuVuc;
import com.company.aneon.f01.entity.MatBang;
import com.company.aneon.f01.entity.Tang;
import com.company.aneon.f01.entity.ToaNha;
import com.company.aneon.f01.integration.AccessScopePort;
import com.company.aneon.f01.integration.ContractQueryPort;
import com.company.aneon.f01.integration.DigitalMapPort;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.CuaHangSapRepository;
import com.company.aneon.f01.repository.KhuVucRepository;
import com.company.aneon.f01.repository.MatBangRepository;
import com.company.aneon.f01.repository.ViTriRepository;
import com.company.aneon.f01.service.impl.PremiseServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PremiseServiceImplTest {

    @Mock
    private MatBangRepository premiseRepository;
    @Mock
    private KhuVucRepository zoneRepository;
    @Mock
    private ViTriRepository locationRepository;
    @Mock
    private CuaHangSapRepository storeRepository;
    @Mock
    private HistoryService historyService;
    @Mock
    private StatusTransitionService transitionService;
    @Mock
    private AccessScopePort accessScopePort;
    @Mock
    private ContractQueryPort contractQueryPort;
    @Mock
    private DigitalMapPort digitalMapPort;

    private PremiseServiceImpl service;
    private UUID zoneId;

    @BeforeEach
    void setUp() {
        service = new PremiseServiceImpl(
                premiseRepository,
                zoneRepository,
                locationRepository,
                storeRepository,
                new F01Mapper(),
                historyService,
                transitionService,
                accessScopePort,
                contractQueryPort,
                digitalMapPort);
        zoneId = UUID.randomUUID();
    }

    @Test
    void create_duplicateCode_throwsDuplicateCodeException() {
        when(premiseRepository.existsByCodeIgnoreCase("MB-001")).thenReturn(true);
        CreatePremiseRequest request =
                new CreatePremiseRequest("mb-001", "Mặt bằng 1", zoneId, BigDecimal.TEN, null, null, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_CODE));

        verify(premiseRepository, never()).save(any());
    }

    @Test
    void create_validRequest_returnsCreatedPremise() {
        KhuVuc zone = createZone();
        when(accessScopePort.getAllowedZoneIds()).thenReturn(Set.of());
        when(premiseRepository.existsByCodeIgnoreCase("MB-001")).thenReturn(false);
        when(zoneRepository.findByIdAndActiveTrue(zoneId)).thenReturn(Optional.of(zone));
        when(premiseRepository.save(any(MatBang.class))).thenAnswer(invocation -> {
            MatBang premise = invocation.getArgument(0);
            premise.setId(UUID.randomUUID());
            premise.setVersion(0L);
            premise.setCreatedAt(Instant.now());
            return premise;
        });
        CreatePremiseRequest request =
                new CreatePremiseRequest("mb-001", "Mặt bằng 1", zoneId, BigDecimal.TEN, null, null, null);

        var response = service.create(request);

        assertThat(response.code()).isEqualTo("MB-001");
        assertThat(response.status().name()).isEqualTo("TRONG");
        assertThat(response.totalArea()).isEqualByComparingTo(BigDecimal.TEN);
        verify(historyService).record(
                org.mockito.ArgumentMatchers.eq("MAT_BANG"),
                any(UUID.class),
                org.mockito.ArgumentMatchers.eq("CREATE"),
                org.mockito.ArgumentMatchers.isNull(),
                any(),
                org.mockito.ArgumentMatchers.isNull());
    }

    private KhuVuc createZone() {
        ToaNha building = new ToaNha();
        building.setId(UUID.randomUUID());
        building.setName("ANEON");
        Tang floor = new Tang();
        floor.setId(UUID.randomUUID());
        floor.setName("Tầng 1");
        floor.setBuilding(building);
        KhuVuc zone = new KhuVuc();
        zone.setId(zoneId);
        zone.setName("Khu A");
        zone.setFloor(floor);
        return zone;
    }
}

