package com.govipos.cashier.model;

import java.math.BigDecimal;

/**
 * Agro-retail product batch catalog entry (all monetary values in LKR).
 */
public record Product(
        String productId,
        String barcode,
        String batchNo,
        String name,
        String unitLabel,
        BigDecimal unitPrice,
        boolean restricted,
        String schemeId
) {}
