package com.govipos.cashier.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cashier refund request pending Branch Manager approval (LKR).
 */
public record RefundRequest(
        String refundId,
        String customerName,
        String batchNo,
        int quantity,
        String reason,
        BigDecimal refundValue,
        RefundStatus status,
        LocalDateTime createdAt
) {
    public enum RefundStatus {
        PENDING, APPROVED, REJECTED
    }
}
