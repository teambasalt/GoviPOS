package com.govipos.cashier.txmode;

import com.govipos.cashier.model.Customer;
import com.govipos.cashier.model.Customer.CreditStatus;
import com.govipos.cashier.model.Customer.CreditTier;
import com.govipos.cashier.model.SaleTransaction.TransactionMode;
import com.govipos.cashier.shell.CashierSessionContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Business logic for switching transaction modes (Cash vs. Credit),
 * validating credit eligibility, and upgrading customer credit tiers upon repayment.
 */
public class TransactionModeService {

    private static final BigDecimal CREDIT_FEE_MULTIPLIER = new BigDecimal("1.05");

    private final CustomerCreditDao customerCreditDao;

    public TransactionModeService(CustomerCreditDao customerCreditDao) {
        this.customerCreditDao = Objects.requireNonNull(customerCreditDao);
    }

    public void switchMode(CashierSessionContext context, TransactionMode mode) {
        Objects.requireNonNull(context, "Session context is required");
        Objects.requireNonNull(mode, "Transaction mode is required");
        context.setTransactionMode(mode);
    }

    public boolean isTransactionAllowed(TransactionMode mode, Customer customer, BigDecimal amountLkr) {
        if (mode == TransactionMode.CASH) {
            return true; // Cash sales do not require a registered customer profile
        }
        if (customer == null || customer.creditStatus() == CreditStatus.DEFAULT) {
            return false;
        }
        BigDecimal required = amountLkr != null ? amountLkr : BigDecimal.ZERO;
        return required.compareTo(customer.availableCredit()) <= 0;
    }

    public BigDecimal calculateRepaymentWithFee(BigDecimal principalLkr) {
        Objects.requireNonNull(principalLkr, "Principal amount is required");
        return principalLkr.multiply(CREDIT_FEE_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }

    public Customer processRepaymentAndUpgrade(Customer customer, BigDecimal repaidPrincipalLkr) throws SQLException {
        Objects.requireNonNull(customer, "Customer is required");
        Objects.requireNonNull(repaidPrincipalLkr, "Repaid principal is required");

        BigDecimal remainingUsed = customer.creditUsed()
                .subtract(repaidPrincipalLkr)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        CreditTier nextTier = customer.creditTier();
        BigDecimal nextLimit = customer.creditLimit();

        // Upgrade tier when customer completes full repayment of current tier allowance
        if (remainingUsed.compareTo(BigDecimal.ZERO) == 0
                && repaidPrincipalLkr.compareTo(customer.creditLimit()) >= 0) {
            nextTier = upgradeTier(customer.creditTier());
            nextLimit = tierAllowanceLkr(nextTier);
        }

        CreditStatus nextStatus = remainingUsed.compareTo(BigDecimal.ZERO) == 0
                ? CreditStatus.PAYMENT_COMPLETE
                : CreditStatus.PAYMENT_DUE;

        Customer updated = new Customer(
                customer.customerId(),
                customer.name(),
                customer.phone(),
                nextTier,
                nextLimit,
                remainingUsed,
                nextStatus
        );
        customerCreditDao.saveCustomer(updated);
        return updated;
    }

    public static CreditTier upgradeTier(CreditTier current) {
        return switch (current) {
            case BRONZE -> CreditTier.SILVER;
            case SILVER -> CreditTier.GOLD;
            case GOLD -> CreditTier.PLATINUM;
            case PLATINUM, DIAMOND -> CreditTier.DIAMOND;
        };
    }

    public static BigDecimal tierAllowanceLkr(CreditTier tier) {
        return switch (tier) {
            case BRONZE -> new BigDecimal("5000.00");
            case SILVER -> new BigDecimal("10000.00");
            case GOLD -> new BigDecimal("25000.00");
            case PLATINUM -> new BigDecimal("50000.00");
            case DIAMOND -> new BigDecimal("100000.00");
        };
    }
}
