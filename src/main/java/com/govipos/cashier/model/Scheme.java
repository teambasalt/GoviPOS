package com.govipos.cashier.model;

import java.math.BigDecimal;

/**
 * Government or branch-defined agro-retail price scheme (all monetary values in LKR).
 */
public record Scheme(
        String schemeId,
        String name,
        String conditionDesc,
        String authority,
        String validUntil,
        BigDecimal controlledPrice,
        BigDecimal discountAmount,
        boolean active
) {}
