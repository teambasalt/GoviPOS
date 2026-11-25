package com.govipos.inventory.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.govipos.inventory.db.InventoryDatabaseManager;
import com.govipos.inventory.model.InventoryAction;
import com.govipos.inventory.model.InventoryBatch;
import com.govipos.inventory.model.InventoryBatch.BatchStatus;
import com.govipos.inventory.model.WarehouseSummary;
import com.govipos.inventory.shell.InventorySessionContext;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InventoryDashboardServiceTest {

    @TempDir
    Path tempDir;

    private InventoryBatchDao inventoryBatchDao;
    private InventoryDashboardService dashboardService;

    @BeforeEach
    void setUp() throws Exception {
        String dbUrl = "jdbc:sqlite:" + tempDir.resolve("test_inventory.db");
        InventoryDatabaseManager dbManager = new InventoryDatabaseManager(dbUrl, null);
        dbManager.runMigrations();
        inventoryBatchDao = new InventoryBatchDao(dbManager);
        dashboardService = new InventoryDashboardService(inventoryBatchDao);
    }

    @Test
    void loadsDefaultWarehouseSummaryAndUpdatesSessionContext() throws Exception {
        InventorySessionContext context = new InventorySessionContext();
        WarehouseSummary summary = dashboardService.loadWarehouseSummary(context);

        assertEquals(1420, summary.activeBatches());
        assertEquals(3, summary.lowCountAlerts());
        assertEquals(2, summary.expiredBatches());
        assertTrue(summary.needsPurchaseOrder());

        assertEquals("1,420", InventoryDashboardService.formatCount(context.getActiveBatchesCount()));
        assertEquals("3", InventoryDashboardService.formatCount(context.getLowCountAlerts()));
        assertEquals("2", InventoryDashboardService.formatCount(context.getExpiredBatchesCount()));

        WarehouseSummary updated = new WarehouseSummary(1500, 0, 1, false);
        dashboardService.updateWarehouseSummary(context, updated);
        assertEquals(1500, context.getActiveBatchesCount());
        assertEquals(0, context.getLowCountAlerts());
        assertEquals(1, context.getExpiredBatchesCount());
    }

    @Test
    void switchesInventoryActionsAndEvaluatesBatchAlerts() throws Exception {
        InventorySessionContext context = new InventorySessionContext();
        assertEquals(InventoryAction.DASHBOARD, context.getCurrentAction());

        dashboardService.selectAction(context, InventoryAction.ADD_GRN);
        assertEquals(InventoryAction.ADD_GRN, context.getCurrentAction());

        dashboardService.selectAction(context, InventoryAction.DISPOSAL);
        assertEquals(InventoryAction.DISPOSAL, context.getCurrentAction());

        dashboardService.selectAction(context, InventoryAction.LOW_COUNT);
        assertEquals(InventoryAction.LOW_COUNT, context.getCurrentAction());

        InventoryBatch lowBatch = new InventoryBatch(
                "BATCH-101", "PROD-01", "Urea Fertilizer 50kg",
                4, 10, LocalDate.of(2027, 5, 1), BatchStatus.ACTIVE
        );
        inventoryBatchDao.saveBatch(lowBatch);
        assertTrue(lowBatch.isLowCount());
        assertFalse(lowBatch.isExpired(LocalDate.of(2026, 11, 25)));
    }
}
