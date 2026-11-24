package com.govipos.cashier.model;

import java.math.BigDecimal;

/**
 * Checkout line item with scheme pricing and restricted-item approval state (LKR).
 */
public record CartItem(
        Product product,
        Scheme appliedScheme,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        boolean approved,
        String approvedBy
) {
    public boolean requiresApproval() {
        return product != null && product.restricted() && !approved;
    }
}
