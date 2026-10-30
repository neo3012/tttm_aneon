package com.company.aneon.f10.service.impl;
import com.company.aneon.common.exception.BusinessException;
import com.company.aneon.common.exception.ErrorCode;
import com.company.aneon.f10.dto.request.CreateEventCostRequest;
import com.company.aneon.f10.dto.response.EventCostResponse;
import com.company.aneon.f10.dto.response.EventRoiResponse;
import com.company.aneon.f10.entity.ChiPhiThucTe;
import com.company.aneon.f10.entity.SuKien;
import com.company.aneon.f10.repository.EventCostRepository;
import com.company.aneon.f10.repository.SuKienRepository;
import com.company.aneon.f10.service.EventCostService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class EventCostServiceImpl implements EventCostService {
    private final EventCostRepository costRepository;
    private final SuKienRepository eventRepository;
    @Override @Transactional(readOnly = true)
    public List<EventCostResponse> list(UUID eventId) { findEvent(eventId); return costRepository.findByEventIdOrderByIncurredAtDesc(eventId).stream().map(this::response).toList(); }
    @Override @Transactional
    public EventCostResponse create(UUID eventId, CreateEventCostRequest r) {
        SuKien event = findEvent(eventId); ChiPhiThucTe c = new ChiPhiThucTe(); c.setEvent(event);
        c.setSupplierName(r.supplierName()); c.setInvoiceNumber(r.invoiceNumber()); c.setAmount(r.amount());
        c.setIncurredAt(r.incurredAt()); c.setAttachmentId(r.attachmentId()); c.setDescription(r.description());
        return response(costRepository.save(c));
    }
    @Override @Transactional(readOnly = true)
    public EventRoiResponse roi(UUID eventId) {
        SuKien event = findEvent(eventId); BigDecimal budget = event.getApprovedBudget() == null ? BigDecimal.ZERO : event.getApprovedBudget();
        BigDecimal cost = costRepository.totalByEventId(eventId); BigDecimal variance = budget.subtract(cost);
        boolean zero = cost.signum() == 0; BigDecimal rate = zero ? null : variance.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return new EventRoiResponse(eventId, budget, cost, variance, rate, zero);
    }
    private SuKien findEvent(UUID id) { return eventRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Event not found")); }
    private EventCostResponse response(ChiPhiThucTe c) { return new EventCostResponse(c.getId(), c.getEvent().getId(), c.getSupplierName(), c.getInvoiceNumber(), c.getAmount(), c.getIncurredAt(), c.getAttachmentId(), c.getDescription()); }
}
