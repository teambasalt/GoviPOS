package com.govipos.cashier.txmode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.govipos.cashier.db.DatabaseManager;
import com.govipos.cashier.model.Customer;
import com.govipos.cashier.model.Customer.CreditStatus;
import com.govipos.cashier.model.Customer.CreditTier;
import com.govipos.cashier.model.SaleTransaction.TransactionMode;
import com.govipos.cashier.shell.CashierSessionContext;
import java.math.BigDecimal;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TransactionModeServiceTest {

    @TempDir
    Path tempDir;

    private CustomerCreditDao customerCreditDao;
    private TransactionModeService txModeService;

    @BeforeEach
    void setUp() throws Exception {
        String dbUrl = "jdbc:sqlite:" + tempDir.resolve("test_txmode.db");
        DatabaseManager dbManager = new DatabaseManager(dbUrl, null);
        dbManager.runMigrations();
        customerCreditDao = new CustomerCreditDao(dbManager);
        txModeService = new TransactionModeService(customerCreditDao);
    }

    @Test
    void switchModeAndValidateCashVsCreditEligibility() {
        CashierSessionContext context = new CashierSessionContext();
        assertEquals(TransactionMode.CASH, context.getTransactionMode());

        // Anonymous cash sale is permitted without a registered customer
        assertTrue(txModeService.isTransactionAllowed(TransactionMode.CASH, null, new BigDecimal("7500.00")));

        // Credit sale requires a registered non-default customer within available credit
        txModeService.switchMode(context, TransactionMode.CREDIT);
        assertEquals(TransactionMode.CREDIT, context.getTransactionMode());
        assertFalse(txModeService.isTransactionAllowed(TransactionMode.CREDIT, null, new BigDecimal("1000.00")));

        Customer bronzeCustomer = new Customer(
                "CUST-001", "Kevin Silva", "0771234567",
                CreditTier.BRONZE, new BigDecimal("5000.00"), new BigDecimal("2000.00"),
                CreditStatus.PAYMENT_DUE
        );
        assertTrue(txModeService.isTransactionAllowed(TransactionMode.CREDIT, bronzeCustomer, new BigDecimal("3000.00")));
        assertFalse(txModeService.isTransactionAllowed(TransactionMode.CREDIT, bronzeCustomer, new BigDecimal("3000.01")));
    }

    @Test
    void fullTierAllowanceRepaymentAppliesFivePercentFeeAndUpgradesBronzeToSilver() throws Exception {
        Customer bronzeCustomer = new Customer(
                "CUST-002", "Nimal Perera", "0719876543",
                CreditTier.BRONZE, new BigDecimal("5000.00"), new BigDecimal("5000.00"),
                CreditStatus.PAYMENT_DUE
        );
        customerCreditDao.saveCustomer(bronzeCustomer);

        // 5000.00 LKR + 5% credit fee = 5250.00 LKR
        assertEquals(new BigDecimal("5250.00"), txModeService.calculateRepaymentWithFee(new BigDecimal("5000.00")));

        Customer upgraded = txModeService.processRepaymentAndUpgrade(bronzeCustomer, new BigDecimal("5000.00"));

        assertEquals(CreditTier.SILVER, upgraded.creditTier());
        assertEquals(new BigDecimal("10000.00"), upgraded.creditLimit());
        assertEquals(new BigDecimal("0.00"), upgraded.creditUsed());
        assertEquals(CreditStatus.PAYMENT_COMPLETE, upgraded.creditStatus());

        Customer persisted = customerCreditDao.findById("CUST-002").orElseThrow();
        assertEquals(CreditTier.SILVER, persisted.creditTier());
        assertEquals(new BigDecimal("10000.00"), persisted.creditLimit());
    }
}
