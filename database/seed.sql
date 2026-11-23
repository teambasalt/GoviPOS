
PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

INSERT INTO roles (id, code, name) VALUES
    (1, 'ADMIN',             'Admin'),
    (2, 'BRANCH_MANAGER',    'Branch Manager'),
    (3, 'CASHIER',           'Cashier'),
    (4, 'INVENTORY_OFFICER', 'Inventory Officer');


INSERT INTO permissions (id, code, description) VALUES
    (1,  'BRANCH_CREATE',     'Register and configure new regional branches'),
    (2,  'BRANCH_VIEW',       'View branch details, status, and audit logs'),
    (3,  'BRANCH_UPDATE',     'Update branch metadata and branch manager assignment'),
    (4,  'BRANCH_DEACTIVATE', 'Deactivate or permanently remove branches'),
    (5,  'USER_CREATE',       'Create new staff user accounts'),
    (6,  'USER_VIEW',         'View active staff personnel directory'),
    (7,  'USER_MANAGE',       'Activate, suspend, or update staff accounts'),
    (8,  'ROLE_ASSIGN',       'Assign and revoke staff roles across branches'),
    (9,  'TERMINAL_MANAGE',   'Register, configure, and assign POS terminals'),
    (10, 'POS_OPERATE',       'Operate assigned POS terminals and process sales'),
    (11, 'INVENTORY_MANAGE',  'Manage branch stock levels and product expiry tracking');


INSERT INTO role_permissions (roleId, permissionId) VALUES
    -- ADMIN: full access
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
    -- BRANCH_MANAGER
    (2, 2), (2, 6), (2, 9), (2, 10), (2, 11),
    -- CASHIER
    (3, 10),
    -- INVENTORY_OFFICER
    (4, 2), (4, 11);


INSERT INTO branches (id, managerId, branchCode, branchName, province, streetAddress, phone, status, createdAt) VALUES
    (1, NULL, 'AGRO-HUB-102', 'Downtown AgroHub #102',         'Western Province',       'No. 12, Baseline Road, Colombo 09',                           '+94 11 269 4102', 'ACTIVE',      '2024-01-10T08:00:00Z'),
    (2, NULL, 'AGRO-HUB-105', 'Anuradhapura North Depot #105', 'North Central Province', 'No. 88, Maithripala Senanayake Mawatha, Anuradhapura',        '+94 25 222 1105', 'ACTIVE',      '2024-03-18T09:15:00Z'),
    (3, NULL, 'AGRO-HUB-107', 'Kandy Highlands Post #107',     'Central Province',       'No. 24, Peradeniya Road, Kandy',                              '+94 81 223 4107', 'DEACTIVATED', '2023-11-05T10:30:00Z'),
    (4, NULL, 'AGRO-HUB-001', 'Piliyandala #01',               'Western Province',       'No. 05, Horana Road, Piliyandala',                            '+94 11 261 4001', 'ACTIVE',      '2023-08-01T08:30:00Z');

INSERT INTO user_accounts (id, staffId, fullName, branchId, email, passwordHash, status, createdAt) VALUES
    (1, 'EMP-2021-001', 'Senaka Bandara',     NULL, 'senaka.bandara@govipos.lk',     'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'ACTIVE', '2021-06-01T08:00:00Z'),
    (2, 'EMP-2022-045', 'Kavinda Perera',     1,    'kavinda.perera@govipos.lk',     '61f29a8d40d2360be7f4a45d6d9f3590f6f51b71e172c97a7e031524148b7a5e', 'ACTIVE', '2022-04-12T08:30:00Z'),
    (3, 'EMP-2024-081', 'Priyantha Kumara',   1,    'priyantha.kumara@govipos.lk',   '61f29a8d40d2360be7f4a45d6d9f3590f6f51b71e172c97a7e031524148b7a5e', 'ACTIVE', '2024-02-20T09:00:00Z'),
    (4, 'EMP-2023-114', 'Roshan Jayawardena', 1,    'roshan.jayawardena@govipos.lk', '61f29a8d40d2360be7f4a45d6d9f3590f6f51b71e172c97a7e031524148b7a5e', 'ACTIVE', '2023-09-14T08:45:00Z');

-- Assign certified Branch Manager Kavinda Perera to Downtown AgroHub #102
UPDATE branches SET managerId = 2 WHERE id = 1;

INSERT INTO user_roles (id, roleId, assignedAt, revokedAt, isActive, userId, assignedBy, branchId) VALUES
    -- Senaka Bandara -> Global Admin (bootstrap: assignedBy = NULL, branchId = NULL)
    (1, 1, '2021-06-01T08:00:00Z', NULL, 1, 1, NULL, NULL),
    -- Kavinda Perera -> Branch Manager at Downtown AgroHub #102
    (2, 2, '2024-01-10T08:15:00Z', NULL, 1, 2, 1, 1),
    -- Priyantha Kumara -> Cashier at Downtown AgroHub #102
    (3, 3, '2024-02-20T09:10:00Z', NULL, 1, 3, 1, 1),
    -- Roshan Jayawardena -> Inventory Officer at Downtown AgroHub #102
    (4, 4, '2024-01-12T09:00:00Z', NULL, 1, 4, 1, 1);


INSERT INTO pos_terminals (id, branchId, terminalCode, operationalStatus, connectionStatus, registeredAt, lastSeenAt) VALUES
    -- Downtown AgroHub #102 (4 Online)
    (1, 1, 'POS-102-01', 'ACTIVE',   'ONLINE',  '2024-01-10T09:00:00Z', '2026-11-23T14:20:00Z'),
    (2, 1, 'POS-102-02', 'ACTIVE',   'ONLINE',  '2024-01-10T09:05:00Z', '2026-11-23T14:18:00Z'),
    (3, 1, 'POS-102-03', 'ACTIVE',   'ONLINE',  '2024-01-10T09:10:00Z', '2026-11-23T14:15:00Z'),
    (4, 1, 'POS-102-04', 'ACTIVE',   'ONLINE',  '2024-01-10T09:15:00Z', '2026-11-23T14:10:00Z'),
    -- Anuradhapura North Depot #105 (3 Online)
    (5, 2, 'POS-105-01', 'ACTIVE',   'ONLINE',  '2024-03-18T10:00:00Z', '2026-11-23T14:05:00Z'),
    (6, 2, 'POS-105-02', 'ACTIVE',   'ONLINE',  '2024-03-18T10:05:00Z', '2026-11-23T14:02:00Z'),
    (7, 2, 'POS-105-03', 'ACTIVE',   'ONLINE',  '2024-03-18T10:10:00Z', '2026-11-23T13:58:00Z'),
    -- Kandy Highlands Post #107 (0 active / Disabled & Offline)
    (8, 3, 'POS-107-01', 'DISABLED', 'OFFLINE', '2023-11-05T11:00:00Z', '2026-10-01T17:30:00Z');


INSERT INTO pos_terminal_assignments (id, assignedAt, unassignedAt, cashierId, assignedBy, terminalId) VALUES
    -- Historical assignment (completed)
    (1, '2024-02-20T09:30:00Z', '2024-06-30T18:00:00Z', 3, 1, 2),
    -- Current active assignment: Priyantha Kumara -> POS-102-01 (assigned by Kavinda Perera)
    (2, '2024-07-01T08:00:00Z', NULL,                   3, 2, 1);

COMMIT;
