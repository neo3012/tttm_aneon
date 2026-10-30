package com.company.aneon.f10.integration;
import java.math.BigDecimal;
import java.util.UUID;
public interface InvoiceQueryPort {
    InvoiceData getInvoice(UUID invoiceId);
    record InvoiceData(UUID id, UUID customerId, UUID storeId, BigDecimal totalAmount) {}
}
