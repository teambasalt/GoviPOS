package com.govipos.inventory.model;

/**
 * Quick warehouse summary counts displayed in the stat ribbon.
 */
public record WarehouseSummary(
        int activeBatches,
        int lowCountAlerts,
        int expiredBatches,
        boolean needsPurchaseOrder
) {
    public static WarehouseSummary defaultSummary() {
        return new WarehouseSummary(1420, 3, 2, true);
    }
}
