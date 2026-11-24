package com.govipos.cashier.shell;

import com.govipos.cashier.db.DatabaseManager;
import com.govipos.cashier.txmode.CustomerCreditDao;
import com.govipos.cashier.txmode.TransactionModeService;
import com.govipos.cashier.txmode.TxModeController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Root shell controller holding placeholder slots for modular sub-branch views.
 */
public class CashierShellController {

    @FXML private BorderPane rootShell;
    @FXML private Pane topBar;
    @FXML private Pane txModePane;
    @FXML private Pane schemesPane;
    @FXML private Pane checkoutPane;
    @FXML private Pane paymentPane;

    private final CashierSessionContext sessionContext = new CashierSessionContext();

    @FXML
    private void initialize() {
        loadTxModeModule();
    }

    private void loadTxModeModule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/govipos/cashier/txmode/TxModeView.fxml"));
            Node view = loader.load();
            TxModeController controller = loader.getController();
            TransactionModeService service = new TransactionModeService(new CustomerCreditDao(new DatabaseManager()));
            controller.init(sessionContext, service);
            setTxModeContent(view);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load TxModeView.fxml", e);
        }
    }

    public CashierSessionContext getSessionContext() {
        return sessionContext;
    }

    public void setTopBarContent(Node node) {
        topBar.getChildren().setAll(node);
    }

    public void setTxModeContent(Node node) {
        txModePane.getChildren().setAll(node);
    }

    public void setSchemesContent(Node node) {
        schemesPane.getChildren().setAll(node);
    }

    public void setCheckoutContent(Node node) {
        checkoutPane.getChildren().setAll(node);
    }

    public void setPaymentContent(Node node) {
        paymentPane.getChildren().setAll(node);
    }
}
