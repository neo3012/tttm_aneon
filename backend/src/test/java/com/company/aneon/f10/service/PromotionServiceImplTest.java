package com.company.aneon.f10.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.common.security.CurrentUser;
import com.company.aneon.f10.constant.PromotionType;
import com.company.aneon.f10.constant.VoucherDiscountType;
import com.company.aneon.f10.dto.request.CreatePromotionRequest;
import com.company.aneon.f10.dto.request.IssueVoucherRequest;
import com.company.aneon.f10.entity.ChuongTrinhKhuyenMai;
import com.company.aneon.f10.entity.PhieuUuDai;
import com.company.aneon.f10.integration.InvoiceQueryPort;
import com.company.aneon.f10.integration.LoyaltyPort;
import com.company.aneon.f10.repository.CustomerVoucherRepository;
import com.company.aneon.f10.repository.F10IdempotencyRepository;
import com.company.aneon.f10.repository.PromotionApplicationRepository;
import com.company.aneon.f10.repository.PromotionRepository;
import com.company.aneon.f10.repository.PromotionStoreRepository;
import com.company.aneon.f10.repository.VoucherRepository;
import com.company.aneon.f10.repository.VoucherStoreRepository;
import com.company.aneon.f10.service.impl.PromotionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {
    @Mock PromotionRepository promotionRepository; @Mock VoucherRepository voucherRepository;
    @Mock CustomerVoucherRepository customerVoucherRepository; @Mock PromotionApplicationRepository applicationRepository;
    @Mock F10IdempotencyRepository idempotencyRepository; @Mock InvoiceQueryPort invoiceQueryPort;
    @Mock LoyaltyPort loyaltyPort; @Mock StringRedisTemplate redis; @Mock CurrentUser currentUser;
    @Mock PromotionStoreRepository promotionStoreRepository; @Mock VoucherStoreRepository voucherStoreRepository;
    @Mock ValueOperations<String, String> valueOperations;
    PromotionServiceImpl service;
    @BeforeEach void setUp() {
        service = new PromotionServiceImpl(promotionRepository, voucherRepository, customerVoucherRepository,
                applicationRepository, idempotencyRepository, promotionStoreRepository, voucherStoreRepository,
                invoiceQueryPort, loyaltyPort, redis,
                currentUser, new ObjectMapper());
    }
    @Test void createsJoinedDiscountPromotion() {
        when(promotionRepository.save(any())).thenAnswer(invocation -> {
            ChuongTrinhKhuyenMai p = invocation.getArgument(0); p.setId(UUID.randomUUID()); p.setVersion(0L); return p;
        });
        Instant start = Instant.now().minusSeconds(60);
        var response = service.createPromotion(new CreatePromotionRequest("sale-1", "Sale", PromotionType.GIAM_GIA,
                start, start.plusSeconds(3600), BigDecimal.ZERO, Set.of("GOLD"), null, true,
                new BigDecimal("0.10"), BigDecimal.TEN, null, null, null, null, null, Set.of()));
        assertThat(response.code()).isEqualTo("SALE-1");
        assertThat(response.type()).isEqualTo(PromotionType.GIAM_GIA);
    }
    @Test void rejectsDiscountRateAboveOne() {
        Instant start = Instant.now();
        assertThatThrownBy(() -> service.createPromotion(new CreatePromotionRequest("sale", "Sale",
                PromotionType.GIAM_GIA, start, start.plusSeconds(60), BigDecimal.ZERO, Set.of(), null, false,
                new BigDecimal("1.01"), null, null, null, null, null, null, Set.of())))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.VALIDATION_ERROR);
    }
    @Test void exhaustedRedisCounterIsCompensatedAndRejected() {
        UUID id = UUID.randomUUID(); PhieuUuDai voucher = new PhieuUuDai(); voucher.setId(id);
        voucher.setDiscountType(VoucherDiscountType.FIXED_AMOUNT); voucher.setValue(BigDecimal.ONE);
        voucher.setActive(true); voucher.setValidFrom(Instant.now().minusSeconds(60));
        voucher.setExpiresAt(Instant.now().plusSeconds(3600)); voucher.setIssuedLimit(1); voucher.setPerCustomerLimit(1);
        when(idempotencyRepository.findByOperationNameAndIdempotencyKey("ISSUE_VOUCHER", "key-1"))
                .thenReturn(Optional.empty());
        when(voucherRepository.findByIdForUpdate(id)).thenReturn(Optional.of(voucher));
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement("aneon:f10:voucher:" + id + ":remaining")).thenReturn(-1L);
        assertThatThrownBy(() -> service.issueVoucher(id, "key-1", new IssueVoucherRequest(UUID.randomUUID(), null, null)))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.VOUCHER_EXHAUSTED);
    }
}
