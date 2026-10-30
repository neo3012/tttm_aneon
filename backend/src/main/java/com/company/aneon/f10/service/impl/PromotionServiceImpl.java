package com.company.aneon.f10.service.impl;

import com.company.aneon.common.api.PageResponse;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.common.security.CurrentUser;
import com.company.aneon.f10.constant.PromotionStatus;
import com.company.aneon.f10.constant.PromotionType;
import com.company.aneon.f10.constant.VoucherDiscountType;
import com.company.aneon.f10.constant.VoucherIssueStatus;
import com.company.aneon.f10.dto.request.ApplyPromotionRequest;
import com.company.aneon.f10.dto.request.CreatePromotionRequest;
import com.company.aneon.f10.dto.request.CreateVoucherRequest;
import com.company.aneon.f10.dto.request.IssueVoucherRequest;
import com.company.aneon.f10.dto.response.PromotionApplicationResponse;
import com.company.aneon.f10.dto.response.PromotionResponse;
import com.company.aneon.f10.dto.response.VoucherIssueResponse;
import com.company.aneon.f10.dto.response.VoucherResponse;
import com.company.aneon.f10.entity.ChuongTrinhGiamGia;
import com.company.aneon.f10.entity.ChuongTrinhKhuyenMai;
import com.company.aneon.f10.entity.ChuongTrinhTangPhieu;
import com.company.aneon.f10.entity.ChuongTrinhTichDiem;
import com.company.aneon.f10.entity.F10Idempotency;
import com.company.aneon.f10.entity.LanApDungKhuyenMai;
import com.company.aneon.f10.entity.KhuyenMaiCuaHang;
import com.company.aneon.f10.entity.PhieuUuDai;
import com.company.aneon.f10.entity.PhieuUuDaiCuaHang;
import com.company.aneon.f10.entity.PhieuUuDaiKhachHang;
import com.company.aneon.f10.integration.InvoiceQueryPort;
import com.company.aneon.f10.integration.LoyaltyPort;
import com.company.aneon.f10.repository.CustomerVoucherRepository;
import com.company.aneon.f10.repository.F10IdempotencyRepository;
import com.company.aneon.f10.repository.PromotionApplicationRepository;
import com.company.aneon.f10.repository.PromotionRepository;
import com.company.aneon.f10.repository.PromotionStoreRepository;
import com.company.aneon.f10.repository.VoucherRepository;
import com.company.aneon.f10.repository.VoucherStoreRepository;
import com.company.aneon.f10.service.PromotionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private static final String ISSUE_OPERATION = "ISSUE_VOUCHER";
    private static final String APPLY_OPERATION = "APPLY_PROMOTION";
    private final PromotionRepository promotionRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final PromotionApplicationRepository applicationRepository;
    private final F10IdempotencyRepository idempotencyRepository;
    private final PromotionStoreRepository promotionStoreRepository;
    private final VoucherStoreRepository voucherStoreRepository;
    private final InvoiceQueryPort invoiceQueryPort;
    private final LoyaltyPort loyaltyPort;
    private final StringRedisTemplate redis;
    private final CurrentUser currentUser;
    private final ObjectMapper objectMapper;

    @Override @Transactional(readOnly = true)
    public PageResponse<PromotionResponse> list(Pageable pageable) {
        return PageResponse.from(promotionRepository.findAll(pageable).map(this::promotionResponse));
    }

    @Override @Transactional
    public PromotionResponse createPromotion(CreatePromotionRequest r) {
        validateRange(r.startAt(), r.endAt());
        String code = r.code().trim().toUpperCase();
        if (promotionRepository.existsByCodeIgnoreCase(code)) throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        ChuongTrinhKhuyenMai p = switch (r.type()) {
            case GIAM_GIA -> discount(r);
            case TANG_PHIEU -> gift(r);
            case TICH_DIEM -> points(r);
        };
        p.setCode(code); p.setName(r.name().trim()); p.setType(r.type()); p.setStartAt(r.startAt());
        p.setEndAt(r.endAt()); p.setMinimumInvoiceAmount(zero(r.minimumInvoiceAmount()));
        p.setTargetTiers(json(r.targetTiers())); p.setTerms(r.terms()); p.setPublicVisible(r.publicVisible());
        p.setStatus(PromotionStatus.HOAT_DONG);
        p = promotionRepository.save(p);
        UUID promotionId = p.getId();
        if (r.storeIds() != null) r.storeIds().forEach(storeId -> {
            KhuyenMaiCuaHang scope = new KhuyenMaiCuaHang(); scope.setPromotionId(promotionId); scope.setStoreId(storeId);
            promotionStoreRepository.save(scope);
        });
        return promotionResponse(p);
    }

    @Override @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest r) {
        validateRange(r.validFrom(), r.expiresAt());
        String code = r.code().trim().toUpperCase();
        if (voucherRepository.existsByCodeIgnoreCase(code)) throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        if (r.discountType() == VoucherDiscountType.PERCENTAGE && r.value().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Percentage voucher value cannot exceed 100");
        }
        PhieuUuDai v = new PhieuUuDai();
        v.setCode(code); v.setName(r.name().trim()); v.setDiscountType(r.discountType()); v.setValue(r.value());
        v.setMaximumDiscount(r.maximumDiscount()); v.setMinimumInvoiceAmount(zero(r.minimumInvoiceAmount()));
        v.setIssuedLimit(r.issuedLimit()); v.setPerCustomerLimit(r.perCustomerLimit());
        v.setValidFrom(r.validFrom()); v.setExpiresAt(r.expiresAt()); v.setActive(true);
        v = voucherRepository.save(v);
        UUID voucherId = v.getId();
        if (r.storeIds() != null) r.storeIds().forEach(storeId -> {
            PhieuUuDaiCuaHang scope = new PhieuUuDaiCuaHang(); scope.setVoucherId(voucherId); scope.setStoreId(storeId);
            voucherStoreRepository.save(scope);
        });
        return voucherResponse(v);
    }

    @Override @Transactional
    public VoucherIssueResponse issueVoucher(UUID voucherId, String key, IssueVoucherRequest r) {
        requireKey(key);
        var previous = idempotencyRepository.findByOperationNameAndIdempotencyKey(ISSUE_OPERATION, key);
        if (previous.isPresent()) return issueResponse(customerVoucherRepository.findById(previous.get().getResultId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND)));
        PhieuUuDai v = voucherRepository.findByIdForUpdate(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Voucher not found"));
        Instant now = Instant.now();
        if (!v.isActive() || now.isBefore(v.getValidFrom()) || !now.isBefore(v.getExpiresAt()))
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE, "Voucher is outside its validity period");
        if (customerVoucherRepository.countByVoucherIdAndCustomerId(voucherId, r.customerId()) >= v.getPerCustomerLimit())
            throw new BusinessException(ErrorCode.VOUCHER_EXHAUSTED, "Customer voucher limit has been reached");
        String redisKey = "aneon:f10:voucher:" + voucherId + ":remaining";
        redis.opsForValue().setIfAbsent(redisKey, Integer.toString(v.getIssuedLimit() - v.getIssuedCount()),
                Duration.between(now, v.getExpiresAt()));
        Long remaining = redis.opsForValue().decrement(redisKey);
        if (remaining == null || remaining < 0) {
            if (remaining != null) redis.opsForValue().increment(redisKey);
            throw new BusinessException(ErrorCode.VOUCHER_EXHAUSTED);
        }
        try {
            if (v.getIssuedCount() >= v.getIssuedLimit()) throw new BusinessException(ErrorCode.VOUCHER_EXHAUSTED);
            PhieuUuDaiKhachHang issue = new PhieuUuDaiKhachHang();
            issue.setVoucher(v); issue.setCustomerId(r.customerId()); issue.setPromotionId(r.promotionId());
            issue.setIssuedAt(now); issue.setStatus(VoucherIssueStatus.ISSUED); issue.setIssueReference(r.reference());
            issue = customerVoucherRepository.saveAndFlush(issue);
            v.setIssuedCount(v.getIssuedCount() + 1);
            saveIdempotency(ISSUE_OPERATION, key, issue.getId());
            return issueResponse(issue);
        } catch (RuntimeException exception) {
            redis.opsForValue().increment(redisKey);
            throw exception;
        }
    }

    @Override @Transactional
    public PromotionApplicationResponse apply(UUID promotionId, String key, ApplyPromotionRequest r) {
        requireKey(key);
        var previous = idempotencyRepository.findByOperationNameAndIdempotencyKey(APPLY_OPERATION, key);
        if (previous.isPresent()) return applicationResponse(applicationRepository.findById(previous.get().getResultId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND)));
        ChuongTrinhKhuyenMai p = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Promotion not found"));
        Instant now = Instant.now();
        if (p.getStatus() != PromotionStatus.HOAT_DONG || now.isBefore(p.getStartAt()) || !now.isBefore(p.getEndAt()))
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE, "Promotion is not active");
        InvoiceQueryPort.InvoiceData invoice = invoiceQueryPort.getInvoice(r.invoiceId());
        if (!invoice.customerId().equals(r.customerId())) throw new BusinessException(ErrorCode.FORBIDDEN);
        if (promotionStoreRepository.countByPromotionId(promotionId) > 0
                && !promotionStoreRepository.existsByPromotionIdAndStoreId(promotionId, invoice.storeId()))
            throw new BusinessException(ErrorCode.FORBIDDEN, "Promotion does not apply to this store");
        if (invoice.totalAmount().compareTo(zero(p.getMinimumInvoiceAmount())) < 0)
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Invoice does not meet the minimum amount");
        ensureTier(p, r.customerId());
        if (applicationRepository.existsByPromotionIdAndInvoiceId(promotionId, r.invoiceId()))
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Promotion already applied to invoice");
        BigDecimal discount = BigDecimal.ZERO; long points = 0;
        if (p instanceof ChuongTrinhGiamGia d) discount = percentDiscount(invoice.totalAmount(), d.getDiscountRate(), d.getMaximumDiscount());
        if (p instanceof ChuongTrinhTichDiem reward) {
            points = reward.getRewardPoints() == null ? calculatePoints(invoice.totalAmount(), reward) : reward.getRewardPoints();
        }
        PhieuUuDaiKhachHang customerVoucher = null;
        if (r.customerVoucherId() != null) {
            customerVoucher = redeemVoucher(r.customerVoucherId(), r.customerId(), r.invoiceId(),
                    invoice.storeId(), invoice.totalAmount());
            discount = discount.add(voucherDiscount(customerVoucher.getVoucher(), invoice.totalAmount()));
        }
        LanApDungKhuyenMai a = new LanApDungKhuyenMai();
        a.setPromotion(p); a.setCustomerId(r.customerId()); a.setInvoiceId(r.invoiceId()); a.setAppliedAt(now);
        a.setDiscountAmount(discount.min(invoice.totalAmount())); a.setPointsGranted(points); a.setIdempotencyKey(key);
        a.setCustomerVoucherId(customerVoucher == null ? null : customerVoucher.getId()); a.setCreatedBy(currentUser.getId());
        a = applicationRepository.saveAndFlush(a); saveIdempotency(APPLY_OPERATION, key, a.getId());
        if (points > 0) loyaltyPort.grantPoints(r.customerId(), points, a.getId());
        return applicationResponse(a);
    }

    private ChuongTrinhGiamGia discount(CreatePromotionRequest r) {
        if (r.discountRate() == null || r.discountRate().compareTo(BigDecimal.ONE) > 0)
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "discountRate must be between 0 and 1");
        ChuongTrinhGiamGia p = new ChuongTrinhGiamGia(); p.setDiscountRate(r.discountRate());
        p.setMaximumDiscount(r.maximumDiscount()); return p;
    }
    private ChuongTrinhTangPhieu gift(CreatePromotionRequest r) {
        if (r.voucherId() == null || r.totalToIssue() == null) throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        ChuongTrinhTangPhieu p = new ChuongTrinhTangPhieu(); p.setVoucherId(r.voucherId());
        p.setTotalToIssue(r.totalToIssue()); return p;
    }
    private ChuongTrinhTichDiem points(CreatePromotionRequest r) {
        if (r.rewardPoints() == null && (r.conversionAmount() == null || r.pointsPerConversion() == null))
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        ChuongTrinhTichDiem p = new ChuongTrinhTichDiem(); p.setRewardPoints(r.rewardPoints());
        p.setConversionAmount(r.conversionAmount()); p.setPointsPerConversion(r.pointsPerConversion()); return p;
    }
    private PhieuUuDaiKhachHang redeemVoucher(
            UUID id, UUID customerId, UUID invoiceId, UUID storeId, BigDecimal total) {
        PhieuUuDaiKhachHang issue = customerVoucherRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Customer voucher not found"));
        if (issue.getStatus() != VoucherIssueStatus.ISSUED || total.compareTo(zero(issue.getVoucher().getMinimumInvoiceAmount())) < 0)
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Voucher cannot be redeemed");
        UUID voucherId = issue.getVoucher().getId();
        if (voucherStoreRepository.countByVoucherId(voucherId) > 0
                && !voucherStoreRepository.existsByVoucherIdAndStoreId(voucherId, storeId))
            throw new BusinessException(ErrorCode.FORBIDDEN, "Voucher does not apply to this store");
        issue.setStatus(VoucherIssueStatus.REDEEMED); issue.setRedeemedAt(Instant.now()); issue.setPurchaseInvoiceId(invoiceId);
        issue.getVoucher().setUsedCount(issue.getVoucher().getUsedCount() + 1); return issue;
    }
    private BigDecimal voucherDiscount(PhieuUuDai v, BigDecimal total) {
        BigDecimal amount = v.getDiscountType() == VoucherDiscountType.FIXED_AMOUNT ? v.getValue()
                : total.multiply(v.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return v.getMaximumDiscount() == null ? amount : amount.min(v.getMaximumDiscount());
    }
    private BigDecimal percentDiscount(BigDecimal total, BigDecimal rate, BigDecimal maximum) {
        BigDecimal amount = total.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return maximum == null ? amount : amount.min(maximum);
    }
    private long calculatePoints(BigDecimal total, ChuongTrinhTichDiem p) {
        return total.divide(p.getConversionAmount(), 0, RoundingMode.DOWN).longValue() * p.getPointsPerConversion();
    }
    private void ensureTier(ChuongTrinhKhuyenMai p, UUID customerId) {
        if (p.getTargetTiers() == null || p.getTargetTiers().equals("[]")) return;
        String tier = loyaltyPort.getCustomerTier(customerId);
        if (tier == null || !p.getTargetTiers().contains("\"" + tier + "\"")) throw new BusinessException(ErrorCode.FORBIDDEN);
    }
    private void saveIdempotency(String operation, String key, UUID resultId) {
        F10Idempotency i = new F10Idempotency(); i.setOperationName(operation); i.setIdempotencyKey(key); i.setResultId(resultId);
        idempotencyRepository.saveAndFlush(i);
    }
    private void requireKey(String key) { if (key == null || key.isBlank() || key.length() > 120) throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Valid Idempotency-Key is required"); }
    private void validateRange(Instant start, Instant end) { if (!start.isBefore(end)) throw new BusinessException(ErrorCode.INVALID_DATE_RANGE); }
    private BigDecimal zero(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
    private String json(Set<String> tiers) { try { return objectMapper.writeValueAsString(tiers == null ? Set.of() : tiers); } catch (JsonProcessingException e) { throw new BusinessException(ErrorCode.VALIDATION_ERROR); } }
    private PromotionResponse promotionResponse(ChuongTrinhKhuyenMai p) { return new PromotionResponse(p.getId(), p.getCode(), p.getName(), p.getType(), p.getStartAt(), p.getEndAt(), p.getMinimumInvoiceAmount(), p.getStatus(), p.isPublicVisible(), p.getVersion()); }
    private VoucherResponse voucherResponse(PhieuUuDai v) { return new VoucherResponse(v.getId(), v.getCode(), v.getName(), v.getDiscountType(), v.getValue(), v.getMaximumDiscount(), v.getMinimumInvoiceAmount(), v.getIssuedLimit(), v.getIssuedCount(), v.getUsedCount(), v.getPerCustomerLimit(), v.getValidFrom(), v.getExpiresAt(), v.isActive(), v.getVersion()); }
    private VoucherIssueResponse issueResponse(PhieuUuDaiKhachHang i) { return new VoucherIssueResponse(i.getId(), i.getVoucher().getId(), i.getCustomerId(), i.getStatus(), i.getIssuedAt()); }
    private PromotionApplicationResponse applicationResponse(LanApDungKhuyenMai a) { return new PromotionApplicationResponse(a.getId(), a.getPromotion().getId(), a.getInvoiceId(), a.getCustomerId(), a.getDiscountAmount(), a.getPointsGranted(), a.getAppliedAt()); }
}
