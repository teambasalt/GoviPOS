package com.govipos.inventory.shell;

import com.govipos.cashier.shell.CashierApp;
import javafx.application.Application;

/**
 * Convenience IDE launcher that does not extend {@link Application}.
 *
 * <p>Allows launching either the Inventory Manager Dashboard (default) or the Cashier Dashboard
 * (pass {@code --cashier} as a program argument) directly from any IDE's "Run" button without
 * triggering the JVM error: "JavaFX runtime components are missing, and are required to run this application".
 */
public final class InventoryLauncher {

    private InventoryLauncher() {}

    public static void main(String[] args) {
        for (String arg : args) {
            if ("--cashier".equalsIgnoreCase(arg)) {
                Application.launch(CashierApp.class, args);
                return;
            }
        }
        InventoryApp.main(args);
    }
}
