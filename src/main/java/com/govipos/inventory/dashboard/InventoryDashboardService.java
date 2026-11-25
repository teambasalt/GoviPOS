package com.govipos.inventory.dashboard;

import com.govipos.inventory.model.InventoryAction;
import com.govipos.inventory.model.WarehouseSummary;
import com.govipos.inventory.shell.InventorySessionContext;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Business logic for loading warehouse batch summaries, formatting counters,
 * and handling primary inventory navigation actions (Add GRNs, Disposal, Low Count).
 */
public class InventoryDashboardService {

    private final InventoryBatchDao inventoryBatchDao;

    public InventoryDashboardService(InventoryBatchDao inventoryBatchDao) {
        this.inventoryBatchDao = Objects.requireNonNull(inventoryBatchDao);
    }

    public WarehouseSummary loadWarehouseSummary(InventorySessionContext context) {
        Objects.requireNonNull(context, "Session context is required");
        WarehouseSummary summary = WarehouseSummary.defaultSummary();
        try {
            summary = inventoryBatchDao.findSummaryByBranch(context.getBranchName())
                    .orElse(WarehouseSummary.defaultSummary());
        } catch (SQLException ignored) {
            // Fall back to default summary if database is not initialized
        }
        context.applySummary(summary);
        return summary;
    }

    public void updateWarehouseSummary(InventorySessionContext context, WarehouseSummary summary) throws SQLException {
        Objects.requireNonNull(context, "Session context is required");
        Objects.requireNonNull(summary, "Warehouse summary is required");
        inventoryBatchDao.saveSummary(context.getBranchName(), summary);
        context.applySummary(summary);
    }

    public void selectAction(InventorySessionContext context, InventoryAction action) {
        Objects.requireNonNull(context, "Session context is required");
        Objects.requireNonNull(action, "Inventory action is required");
        context.setCurrentAction(action);
    }

    public static String formatCount(int count) {
        return NumberFormat.getIntegerInstance(Locale.US).format(count);
    }
}
