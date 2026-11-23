-- ============================================================================
-- GoviPOS Admin Management Module - SQLite Database Schema
-- Based on GoviPOS Admin Management Module ER Diagram
-- Convention: snake_case table names, camelCase column names
-- ============================================================================

PRAGMA foreign_keys = ON;

-- ----------------------------------------------------------------------------
-- 1. roles
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    code    TEXT    NOT NULL UNIQUE,
    name    TEXT    NOT NULL
);

-- ----------------------------------------------------------------------------
-- 2. permissions
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS permissions (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    code        TEXT    NOT NULL UNIQUE,
    description TEXT    NOT NULL
);

-- ----------------------------------------------------------------------------
-- 3. role_permissions (many-to-many join between roles and permissions)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role_permissions (
    permissionId    INTEGER NOT NULL,
    roleId          INTEGER NOT NULL,
    PRIMARY KEY (permissionId, roleId),
    FOREIGN KEY (permissionId) REFERENCES permissions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (roleId)       REFERENCES roles(id)       ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------------------------------------------------------
-- 4. branches
-- Note: managerId references user_accounts(id) (mutual nullable FK cycle).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS branches (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    managerId       INTEGER UNIQUE,
    branchCode      TEXT    NOT NULL UNIQUE,
    branchName      TEXT    NOT NULL,
    province        TEXT    NOT NULL,
    streetAddress   TEXT    NOT NULL,
    phone           TEXT    NOT NULL,
    status          TEXT    NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE', 'DEACTIVATED')),
    createdAt       TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    FOREIGN KEY (managerId) REFERENCES user_accounts(id)
                            ON DELETE SET NULL ON UPDATE CASCADE
                            DEFERRABLE INITIALLY DEFERRED
);

-- ----------------------------------------------------------------------------
-- 5. user_accounts
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_accounts (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    staffId         TEXT    NOT NULL UNIQUE,
    fullName        TEXT    NOT NULL,
    branchId        INTEGER,
    email           TEXT    UNIQUE,
    passwordHash    TEXT    NOT NULL,
    status          TEXT    NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    createdAt       TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    FOREIGN KEY (branchId) REFERENCES branches(id)
                           ON DELETE SET NULL ON UPDATE CASCADE
                           DEFERRABLE INITIALLY DEFERRED
);

-- ----------------------------------------------------------------------------
-- 6. user_roles
-- Note: branchId and assignedBy are nullable to support global Admin accounts
--       and bootstrap/seeded role creation.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    roleId      INTEGER NOT NULL,
    assignedAt  TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    revokedAt   TEXT,
    isActive    INTEGER NOT NULL DEFAULT 1
                        CHECK (isActive IN (0, 1)),
    userId      INTEGER NOT NULL,
    assignedBy  INTEGER,
    branchId    INTEGER,
    FOREIGN KEY (roleId)     REFERENCES roles(id)         ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (userId)     REFERENCES user_accounts(id) ON DELETE CASCADE  ON UPDATE CASCADE,
    FOREIGN KEY (assignedBy) REFERENCES user_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (branchId)   REFERENCES branches(id)      ON DELETE SET NULL ON UPDATE CASCADE
);

-- ----------------------------------------------------------------------------
-- 7. pos_terminals
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pos_terminals (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    branchId            INTEGER NOT NULL,
    terminalCode        TEXT    NOT NULL UNIQUE,
    operationalStatus   TEXT    NOT NULL DEFAULT 'ACTIVE'
                                CHECK (operationalStatus IN ('ACTIVE', 'DISABLED')),
    connectionStatus    TEXT    NOT NULL DEFAULT 'OFFLINE'
                                CHECK (connectionStatus IN ('ONLINE', 'OFFLINE')),
    registeredAt        TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    lastSeenAt          TEXT,
    FOREIGN KEY (branchId) REFERENCES branches(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ----------------------------------------------------------------------------
-- 8. pos_terminal_assignments
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pos_terminal_assignments (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    assignedAt      TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%SZ', 'now')),
    unassignedAt    TEXT,
    cashierId       INTEGER NOT NULL,
    assignedBy      INTEGER NOT NULL,
    terminalId      INTEGER NOT NULL,
    FOREIGN KEY (cashierId)  REFERENCES user_accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (assignedBy) REFERENCES user_accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (terminalId) REFERENCES pos_terminals(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ============================================================================
-- Indexes for foreign keys & common admin queries
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_role_permissions_roleId           ON role_permissions(roleId);
CREATE INDEX IF NOT EXISTS idx_user_accounts_branchId            ON user_accounts(branchId);
CREATE INDEX IF NOT EXISTS idx_user_accounts_status              ON user_accounts(status);
CREATE INDEX IF NOT EXISTS idx_branches_status                   ON branches(status);
CREATE INDEX IF NOT EXISTS idx_user_roles_userId                 ON user_roles(userId);
CREATE INDEX IF NOT EXISTS idx_user_roles_roleId                 ON user_roles(roleId);
CREATE INDEX IF NOT EXISTS idx_user_roles_branchId               ON user_roles(branchId);
CREATE INDEX IF NOT EXISTS idx_user_roles_active                 ON user_roles(userId, isActive);
CREATE INDEX IF NOT EXISTS idx_pos_terminals_branchId            ON pos_terminals(branchId);
CREATE INDEX IF NOT EXISTS idx_pos_terminal_assignments_cashier  ON pos_terminal_assignments(cashierId);
CREATE INDEX IF NOT EXISTS idx_pos_terminal_assignments_terminal ON pos_terminal_assignments(terminalId);
