-- GoviPOS Cashier Local SQLite/SQLCipher Schema (All monetary amounts in LKR)
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS customers (
    customer_id   TEXT PRIMARY KEY,
    name          TEXT NOT NULL,
    phone         TEXT UNIQUE,
    credit_tier   TEXT NOT NULL DEFAULT 'BRONZE' CHECK (credit_tier IN ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND')),
    credit_limit  NUMERIC(12, 2) NOT NULL DEFAULT 5000.00,
    credit_used   NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    credit_status TEXT NOT NULL DEFAULT 'PAYMENT_COMPLETE' CHECK (credit_status IN ('PAYMENT_DUE', 'PAYMENT_COMPLETE', 'DEFAULT'))
);

CREATE TABLE IF NOT EXISTS schemes (
    scheme_id        TEXT PRIMARY KEY,
    name             TEXT NOT NULL,
    condition_desc   TEXT NOT NULL,
    authority        TEXT NOT NULL,
    valid_until      TEXT NOT NULL,
    controlled_price NUMERIC(12, 2),
    discount_amount  NUMERIC(12, 2) DEFAULT 0.00,
    is_active        INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1))
);

CREATE TABLE IF NOT EXISTS products (
    product_id    TEXT PRIMARY KEY,
    barcode       TEXT NOT NULL,
    batch_no      TEXT NOT NULL,
    name          TEXT NOT NULL,
    unit_label    TEXT NOT NULL DEFAULT 'unit',
    unit_price    NUMERIC(12, 2) NOT NULL,
    is_restricted INTEGER NOT NULL DEFAULT 0 CHECK (is_restricted IN (0, 1)),
    scheme_id     TEXT REFERENCES schemes(scheme_id),
    UNIQUE (barcode, batch_no)
);

CREATE TABLE IF NOT EXISTS sales (
    sale_id         TEXT PRIMARY KEY,
    customer_id     TEXT REFERENCES customers(customer_id),
    tx_mode         TEXT NOT NULL CHECK (tx_mode IN ('CASH', 'CREDIT')),
    payment_method  TEXT NOT NULL CHECK (payment_method IN ('CASH', 'CARD', 'QR')),
    cash_amount     NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    credit_amount   NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    scheme_discount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    grand_total     NUMERIC(12, 2) NOT NULL,
    sync_status     TEXT NOT NULL DEFAULT 'PENDING' CHECK (sync_status IN ('PENDING', 'SYNCED', 'FAILED')),
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id TEXT PRIMARY KEY,
    sale_id      TEXT NOT NULL REFERENCES sales(sale_id) ON DELETE CASCADE,
    product_id   TEXT NOT NULL REFERENCES products(product_id),
    batch_no     TEXT NOT NULL,
    quantity     INTEGER NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(12, 2) NOT NULL,
    scheme_id    TEXT REFERENCES schemes(scheme_id),
    total_price  NUMERIC(12, 2) NOT NULL,
    approved_by  TEXT
);

CREATE TABLE IF NOT EXISTS refund_requests (
    refund_id     TEXT PRIMARY KEY,
    customer_name TEXT NOT NULL,
    batch_no      TEXT NOT NULL,
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    reason        TEXT NOT NULL,
    refund_value  NUMERIC(12, 2) NOT NULL,
    status        TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at    TEXT NOT NULL DEFAULT (datetime('now'))
);
