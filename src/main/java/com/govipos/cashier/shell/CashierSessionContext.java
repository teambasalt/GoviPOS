package com.govipos.cashier.shell;

import com.govipos.cashier.model.CartItem;
import com.govipos.cashier.model.Customer;
import com.govipos.cashier.model.SaleTransaction.PaymentMethod;
import com.govipos.cashier.model.SaleTransaction.TransactionMode;
import com.govipos.cashier.model.Scheme;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Shared observable session state holder allowing cashier sub-modules
 * (topBar, txModePane, schemesPane, checkoutPane, paymentPane) to communicate
 * without tight coupling.
 */
public final class CashierSessionContext {

    private final ObjectProperty<TransactionMode> transactionMode =
            new SimpleObjectProperty<>(TransactionMode.CASH);
    private final ObjectProperty<Customer> selectedCustomer =
            new SimpleObjectProperty<>(null);
    private final ObjectProperty<PaymentMethod> paymentMethod =
            new SimpleObjectProperty<>(PaymentMethod.CASH);
    private final ObservableList<CartItem> cartItems =
            FXCollections.observableArrayList();
    private final ObservableList<Scheme> activeSchemes =
            FXCollections.observableArrayList();
    private final BooleanProperty online =
            new SimpleBooleanProperty(true);
    private final IntegerProperty pendingSyncCount =
            new SimpleIntegerProperty(0);

    public ObjectProperty<TransactionMode> transactionModeProperty() {
        return transactionMode;
    }

    public TransactionMode getTransactionMode() {
        return transactionMode.get();
    }

    public void setTransactionMode(TransactionMode mode) {
        this.transactionMode.set(mode);
    }

    public ObjectProperty<Customer> selectedCustomerProperty() {
        return selectedCustomer;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer.get();
    }

    public void setSelectedCustomer(Customer customer) {
        this.selectedCustomer.set(customer);
    }

    public ObjectProperty<PaymentMethod> paymentMethodProperty() {
        return paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(PaymentMethod method) {
        this.paymentMethod.set(method);
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    public ObservableList<Scheme> getActiveSchemes() {
        return activeSchemes;
    }

    public BooleanProperty onlineProperty() {
        return online;
    }

    public boolean isOnline() {
        return online.get();
    }

    public void setOnline(boolean isOnline) {
        this.online.set(isOnline);
    }

    public IntegerProperty pendingSyncCountProperty() {
        return pendingSyncCount;
    }

    public int getPendingSyncCount() {
        return pendingSyncCount.get();
    }

    public void setPendingSyncCount(int count) {
        this.pendingSyncCount.set(count);
    }

    public void clearTransaction() {
        cartItems.clear();
        selectedCustomer.set(null);
        paymentMethod.set(PaymentMethod.CASH);
    }
}
