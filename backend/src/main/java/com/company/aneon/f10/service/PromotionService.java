package com.company.aneon.f10.service;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f10.dto.request.ApplyPromotionRequest;
import com.company.aneon.f10.dto.request.CreatePromotionRequest;
import com.company.aneon.f10.dto.request.CreateVoucherRequest;
import com.company.aneon.f10.dto.request.IssueVoucherRequest;
import com.company.aneon.f10.dto.response.PromotionApplicationResponse;
import com.company.aneon.f10.dto.response.PromotionResponse;
import com.company.aneon.f10.dto.response.VoucherIssueResponse;
import com.company.aneon.f10.dto.response.VoucherResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
public interface PromotionService {
    PageResponse<PromotionResponse> list(Pageable pageable);
    PromotionResponse createPromotion(CreatePromotionRequest request);
    VoucherResponse createVoucher(CreateVoucherRequest request);
    VoucherIssueResponse issueVoucher(UUID voucherId, String key, IssueVoucherRequest request);
    PromotionApplicationResponse apply(UUID promotionId, String key, ApplyPromotionRequest request);
}
