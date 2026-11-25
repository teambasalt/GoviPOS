package com.govipos.inventory.shell;

import com.govipos.inventory.dashboard.InventoryBatchDao;
import com.govipos.inventory.dashboard.InventoryDashboardController;
import com.govipos.inventory.dashboard.InventoryDashboardService;
import com.govipos.inventory.db.InventoryDatabaseManager;
import com.govipos.inventory.header.InventoryHeaderController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Root shell controller for the GoviPOS Inventory Manager Dashboard.
 */
public class InventoryShellController {

    private static final double DESIGN_WIDTH = 1366.0;
    private static final double CONTAINER_WIDTH = 1046.0;
    private static final double DESIGN_TOP_GAP = 70.0;
    private static final double DESIGN_FREE_SPACE = 178.0;
    private static final double MIN_SIDE_MARGIN = 32.0;
    private static final double MIN_VERTICAL_MARGIN = 24.0;

    @FXML private BorderPane rootShell;
    @FXML private Pane topBar;
    @FXML private ScrollPane centerScrollPane;
    @FXML private Pane contentViewport;
    @FXML private Pane dashboardPane;

    private final InventorySessionContext sessionContext = new InventorySessionContext();

    @FXML
    private void initialize() {
        loadHeaderModule();
        loadDashboardModule();
        configureResponsiveLayout();
    }

    private void loadHeaderModule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/govipos/inventory/header/InventoryHeaderView.fxml"));
            Node view = loader.load();
            InventoryHeaderController controller = loader.getController();
            controller.init(sessionContext);
            setTopBarContent(view);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load InventoryHeaderView.fxml", e);
        }
    }

    private void loadDashboardModule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/govipos/inventory/dashboard/InventoryDashboardView.fxml"));
            Node view = loader.load();
            InventoryDashboardController controller = loader.getController();
            InventoryDashboardService service = new InventoryDashboardService(
                    new InventoryBatchDao(new InventoryDatabaseManager()));
            controller.init(sessionContext, service);
            setDashboardContent(view);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load InventoryDashboardView.fxml", e);
        }
    }

    private void configureResponsiveLayout() {
        if (contentViewport == null || dashboardPane == null) {
            return;
        }
        Runnable relayout = () -> {
            double w = contentViewport.getWidth();
            double h = contentViewport.getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            double proportional = w * CONTAINER_WIDTH / DESIGN_WIDTH;
            double fixed = Math.min(w - 2 * MIN_SIDE_MARGIN, CONTAINER_WIDTH);
            double cw = Math.max(0, Math.max(proportional, fixed));
            double ch = dashboardPane.prefHeight(cw);
            double free = Math.max(0, h - ch);
            double top = Math.max(free * DESIGN_TOP_GAP / DESIGN_FREE_SPACE, Math.min(MIN_VERTICAL_MARGIN, free / 2.0));
            dashboardPane.resizeRelocate(Math.round((w - cw) / 2.0), Math.round(top), cw, ch);
        };
        contentViewport.widthProperty().addListener((obs, oldV, newV) -> relayout.run());
        contentViewport.heightProperty().addListener((obs, oldV, newV) -> relayout.run());
        dashboardPane.layoutBoundsProperty().addListener((obs, oldV, newV) -> relayout.run());
    }

    public InventorySessionContext getSessionContext() {
        return sessionContext;
    }

    public void setTopBarContent(Node node) {
        topBar.getChildren().setAll(node);
    }

    public void setDashboardContent(Node node) {
        dashboardPane.getChildren().setAll(node);
    }
}
