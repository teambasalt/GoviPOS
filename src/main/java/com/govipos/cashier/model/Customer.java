package com.govipos.cashier.model;

import java.math.BigDecimal;

/**
 * Customer profile and agro-retail credit tier allowance (all monetary values in LKR).
 */
public record Customer(
        String customerId,
        String name,
        String phone,
        CreditTier creditTier,
        BigDecimal creditLimit,
        BigDecimal creditUsed,
        CreditStatus creditStatus
) {
    public enum CreditTier {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }

    public enum CreditStatus {
        PAYMENT_DUE, PAYMENT_COMPLETE, DEFAULT
    }

    public BigDecimal availableCredit() {
        if (creditLimit == null || creditUsed == null) {
            return BigDecimal.ZERO;
        }
        return creditLimit.subtract(creditUsed).max(BigDecimal.ZERO);
    }
}
