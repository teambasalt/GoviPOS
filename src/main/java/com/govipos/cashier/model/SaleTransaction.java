package com.govipos.cashier.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Completed or pending cashier sale transaction with offline sync tracking (LKR).
 */
public record SaleTransaction(
        String saleId,
        String customerId,
        TransactionMode txMode,
        PaymentMethod paymentMethod,
        List<CartItem> items,
        BigDecimal cashAmount,
        BigDecimal creditAmount,
        BigDecimal schemeDiscount,
        BigDecimal grandTotal,
        SyncStatus syncStatus,
        LocalDateTime createdAt
) {
    public enum TransactionMode {
        CASH, CREDIT
    }

    public enum PaymentMethod {
        CASH, CARD, QR
    }

    public enum SyncStatus {
        PENDING, SYNCED, FAILED
    }
}
