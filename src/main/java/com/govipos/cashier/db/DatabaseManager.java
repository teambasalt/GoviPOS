package com.govipos.cashier.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Offline-first SQLite / SQLCipher connection manager and schema migration runner.
 */
public final class DatabaseManager {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:govipos_cashier.db";
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private final String dbUrl;
    private final String cipherKey;

    public DatabaseManager() {
        this(DEFAULT_DB_URL, System.getenv("GOVIPOS_DB_KEY"));
    }

    public DatabaseManager(String dbUrl, String cipherKey) {
        this.dbUrl = dbUrl;
        this.cipherKey = cipherKey;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        try (Statement stmt = conn.createStatement()) {
            if (cipherKey != null && !cipherKey.isBlank()) {
                stmt.execute("PRAGMA key = '" + cipherKey.replace("'", "''") + "';");
            }
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public void runMigrations() throws Exception {
        InputStream in = getClass().getResourceAsStream(SCHEMA_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("Migration script not found: " + SCHEMA_RESOURCE);
        }
        String sql;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            sql = reader.lines()
                    .filter(line -> !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));
        }
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}
