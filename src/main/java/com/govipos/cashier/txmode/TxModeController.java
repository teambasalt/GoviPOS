package com.govipos.cashier.txmode;

import com.govipos.cashier.model.SaleTransaction.TransactionMode;
import com.govipos.cashier.shell.CashierSessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * JavaFX Controller for the Transaction Mode selector (Cash Sale vs. Credit Sales).
 */
public class TxModeController {

    private static final String ACTIVE_BTN_STYLE =
            "-fx-background-color: #022c22; -fx-text-fill: #ffffff; -fx-font-weight: bold; "
            + "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: #10b981; "
            + "-fx-border-radius: 8; -fx-border-width: 1.5; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16;";

    private static final String INACTIVE_BTN_STYLE =
            "-fx-background-color: #ffffff; -fx-text-fill: #064e3b; -fx-font-weight: bold; "
            + "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-color: transparent; "
            + "-fx-border-radius: 8; -fx-border-width: 1.5; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16;";

    @FXML private Button cashSaleBtn;
    @FXML private Button creditSaleBtn;

    private CashierSessionContext sessionContext;
    private TransactionModeService txModeService;

    public void init(CashierSessionContext sessionContext, TransactionModeService txModeService) {
        this.sessionContext = sessionContext;
        this.txModeService = txModeService;

        this.sessionContext.transactionModeProperty().addListener((obs, oldMode, newMode) -> applyModeStyles(newMode));
        applyModeStyles(this.sessionContext.getTransactionMode());
    }

    @FXML
    private void onSelectCashSale() {
        if (txModeService != null && sessionContext != null) {
            txModeService.switchMode(sessionContext, TransactionMode.CASH);
        }
    }

    @FXML
    private void onSelectCreditSale() {
        if (txModeService != null && sessionContext != null) {
            txModeService.switchMode(sessionContext, TransactionMode.CREDIT);
        }
    }

    private void applyModeStyles(TransactionMode mode) {
        boolean isCash = (mode == TransactionMode.CASH);
        cashSaleBtn.setStyle(isCash ? ACTIVE_BTN_STYLE : INACTIVE_BTN_STYLE);
        creditSaleBtn.setStyle(isCash ? INACTIVE_BTN_STYLE : ACTIVE_BTN_STYLE);
    }
}
