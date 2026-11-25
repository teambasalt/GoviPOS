package com.govipos.inventory.dashboard;

import com.govipos.inventory.db.InventoryDatabaseManager;
import com.govipos.inventory.model.InventoryBatch;
import com.govipos.inventory.model.WarehouseSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * SQLite DAO for loading and persisting warehouse inventory batch summaries.
 */
public class InventoryBatchDao {

    private final InventoryDatabaseManager dbManager;

    public InventoryBatchDao(InventoryDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Optional<WarehouseSummary> findSummaryByBranch(String branchCode) throws SQLException {
        String sql = """
                SELECT active_batches, low_count_alerts, expired_batches
                FROM warehouse_stats
                WHERE branch_code = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, branchCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int active = rs.getInt("active_batches");
                    int low = rs.getInt("low_count_alerts");
                    int expired = rs.getInt("expired_batches");
                    return Optional.of(new WarehouseSummary(active, low, expired, low > 0));
                }
                return Optional.empty();
            }
        }
    }

    public void saveSummary(String branchCode, WarehouseSummary summary) throws SQLException {
        String sql = """
                INSERT INTO warehouse_stats (branch_code, active_batches, low_count_alerts, expired_batches)
                VALUES (?, ?, ?, ?)
                ON CONFLICT(branch_code) DO UPDATE SET
                    active_batches = excluded.active_batches,
                    low_count_alerts = excluded.low_count_alerts,
                    expired_batches = excluded.expired_batches
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, branchCode);
            ps.setInt(2, summary.activeBatches());
            ps.setInt(3, summary.lowCountAlerts());
            ps.setInt(4, summary.expiredBatches());
            ps.executeUpdate();
        }
    }

    public void saveBatch(InventoryBatch batch) throws SQLException {
        String sql = """
                INSERT INTO inventory_batches (batch_no, product_id, product_name, quantity, reorder_threshold, expiry_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(batch_no) DO UPDATE SET
                    product_id = excluded.product_id,
                    product_name = excluded.product_name,
                    quantity = excluded.quantity,
                    reorder_threshold = excluded.reorder_threshold,
                    expiry_date = excluded.expiry_date,
                    status = excluded.status
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batch.batchNo());
            ps.setString(2, batch.productId());
            ps.setString(3, batch.productName());
            ps.setInt(4, batch.quantity());
            ps.setInt(5, batch.reorderThreshold());
            ps.setString(6, batch.expiryDate() != null ? batch.expiryDate().toString() : null);
            ps.setString(7, batch.status().name());
            ps.executeUpdate();
        }
    }
}
