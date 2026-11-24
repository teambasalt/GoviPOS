package com.govipos.cashier.txmode;

import com.govipos.cashier.db.DatabaseManager;
import com.govipos.cashier.model.Customer;
import com.govipos.cashier.model.Customer.CreditStatus;
import com.govipos.cashier.model.Customer.CreditTier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * SQLite DAO for customer credit profile lookup and tier/credit status persistence.
 */
public class CustomerCreditDao {

    private final DatabaseManager dbManager;

    public CustomerCreditDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public Optional<Customer> findById(String customerId) throws SQLException {
        String sql = """
                SELECT customer_id, name, phone, credit_tier, credit_limit, credit_used, credit_status
                FROM customers
                WHERE customer_id = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void saveCustomer(Customer customer) throws SQLException {
        String sql = """
                INSERT INTO customers (customer_id, name, phone, credit_tier, credit_limit, credit_used, credit_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(customer_id) DO UPDATE SET
                    name = excluded.name,
                    phone = excluded.phone,
                    credit_tier = excluded.credit_tier,
                    credit_limit = excluded.credit_limit,
                    credit_used = excluded.credit_used,
                    credit_status = excluded.credit_status
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.customerId());
            ps.setString(2, customer.name());
            ps.setString(3, customer.phone());
            ps.setString(4, customer.creditTier().name());
            ps.setBigDecimal(5, customer.creditLimit());
            ps.setBigDecimal(6, customer.creditUsed());
            ps.setString(7, customer.creditStatus().name());
            ps.executeUpdate();
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("customer_id"),
                rs.getString("name"),
                rs.getString("phone"),
                CreditTier.valueOf(rs.getString("credit_tier")),
                new BigDecimal(rs.getString("credit_limit")),
                new BigDecimal(rs.getString("credit_used")),
                CreditStatus.valueOf(rs.getString("credit_status"))
        );
    }
}
