package com.govipos.inventory.shell;

import com.govipos.inventory.db.InventoryDatabaseManager;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * GoviPOS Inventory Manager Dashboard JavaFX Application Entrypoint.
 *
 * <p>Does not directly extend {@link Application} so that running {@code main()} from an IDE
 * (IntelliJ IDEA / Eclipse / VS Code) without manual {@code --module-path} VM options does not
 * fail with "JavaFX runtime components are missing, and are required to run this application".
 */
public class InventoryApp {

    private static final String[] BUNDLED_FONTS = {
            "/fonts/Inter-Bold.ttf",
            "/fonts/Manrope-Medium.ttf",
            "/fonts/Manrope-SemiBold.ttf",
            "/fonts/Manrope-Bold.ttf",
            "/fonts/SpaceGrotesk-Medium.ttf",
            "/fonts/SpaceGrotesk-Bold.ttf"
    };

    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }

    public static void loadBundledFonts() {
        for (String fontPath : BUNDLED_FONTS) {
            try (InputStream in = InventoryApp.class.getResourceAsStream(fontPath)) {
                if (in != null) {
                    Font.loadFont(in, 12);
                }
            } catch (Exception ignored) {
                // Fallback to system fonts if resource stream fails
            }
        }
    }

    /**
     * Inner JavaFX {@link Application} subclass launched by {@link InventoryApp#main(String[])}.
     */
    public static class FxApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            loadBundledFonts();

            InventoryDatabaseManager dbManager = new InventoryDatabaseManager();
            dbManager.runMigrations();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/govipos/inventory/shell/InventoryShell.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1366, 768);
            String cssUrl = getClass().getResource("/com/govipos/inventory/shell/inventory.css").toExternalForm();
            scene.getStylesheets().add(cssUrl);

            primaryStage.setTitle("GoviPOS - Inventory Manager Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(640);
            primaryStage.show();
        }
    }
}
