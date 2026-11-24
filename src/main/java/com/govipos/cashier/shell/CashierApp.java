package com.govipos.cashier.shell;

import com.govipos.cashier.db.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * GoviPOS Cashier Dashboard JavaFX Application Entrypoint.
 */
public class CashierApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.runMigrations();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/govipos/cashier/shell/CashierShell.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("GoviPOS - Cashier Dashboard");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
