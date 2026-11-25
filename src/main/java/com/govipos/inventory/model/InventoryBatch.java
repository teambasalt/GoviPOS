package com.govipos.inventory.model;

import java.time.LocalDate;

/**
 * Agro-retail warehouse inventory batch entry.
 */
public record InventoryBatch(
        String batchNo,
        String productId,
        String productName,
        int quantity,
        int reorderThreshold,
        LocalDate expiryDate,
        BatchStatus status
) {
    public enum BatchStatus {
        ACTIVE, EXPIRED, DISPOSED
    }

    public boolean isLowCount() {
        return status == BatchStatus.ACTIVE && quantity <= reorderThreshold;
    }

    public boolean isExpired(LocalDate referenceDate) {
        return status == BatchStatus.EXPIRED
                || (expiryDate != null && referenceDate != null && expiryDate.isBefore(referenceDate));
    }
}
