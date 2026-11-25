-- GoviPOS Inventory Operations Local SQLite Schema
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS inventory_batches (
    batch_no          TEXT PRIMARY KEY,
    product_id        TEXT NOT NULL,
    product_name      TEXT NOT NULL,
    quantity          INTEGER NOT NULL DEFAULT 0,
    reorder_threshold INTEGER NOT NULL DEFAULT 10,
    expiry_date       TEXT,
    status            TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'DISPOSED'))
);

CREATE TABLE IF NOT EXISTS warehouse_stats (
    branch_code      TEXT PRIMARY KEY,
    active_batches   INTEGER NOT NULL DEFAULT 1420,
    low_count_alerts INTEGER NOT NULL DEFAULT 3,
    expired_batches  INTEGER NOT NULL DEFAULT 2
);

INSERT OR IGNORE INTO warehouse_stats (branch_code, active_batches, low_count_alerts, expired_batches)
VALUES ('Piliyandala #01', 1420, 3, 2);
