package com.govipos.inventory.dashboard;

import com.govipos.inventory.model.InventoryAction;
import com.govipos.inventory.shell.InventorySessionContext;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * JavaFX Controller for the Central Inventory Operations dashboard view
 * (Quick Warehouse Summary Stat Ribbon & 3 Primary Navigation Cards).
 */
public class InventoryDashboardController {

    @FXML private VBox centralContainer;
    @FXML private StackPane statRibbon;
    @FXML private StackPane statRibbonInner;
    @FXML private Pane ribbonGlowLayer;
    @FXML private Circle ribbonGlowRight;
    @FXML private Ellipse ribbonGlowCenter;

    @FXML private Label activeBatchesValueLabel;
    @FXML private Label lowCountValueLabel;
    @FXML private StackPane needsPoBadge;
    @FXML private Label expiredBatchesValueLabel;

    @FXML private StackPane grnCard;
    @FXML private StackPane grnCardInner;
    @FXML private Pane grnGlowLayer;
    @FXML private Circle grnGlowCircle;
    @FXML private Button openGrnBtn;

    @FXML private StackPane disposalCard;
    @FXML private StackPane disposalCardInner;
    @FXML private Button openDisposalBtn;

    @FXML private StackPane lowCountCard;
    @FXML private StackPane lowCountCardInner;
    @FXML private Button reviewShortagesBtn;

    private InventorySessionContext sessionContext;
    private InventoryDashboardService dashboardService;

    @FXML
    private void initialize() {
        configureVisualEffectsAndClips();
        playEntranceAnimation(centralContainer);
    }

    public void init(InventorySessionContext sessionContext, InventoryDashboardService dashboardService) {
        this.sessionContext = sessionContext;
        this.dashboardService = dashboardService;

        this.sessionContext.activeBatchesCountProperty().addListener((obs, oldV, newV) ->
                updateStatsLabels());
        this.sessionContext.lowCountAlertsProperty().addListener((obs, oldV, newV) ->
                updateStatsLabels());
        this.sessionContext.expiredBatchesCountProperty().addListener((obs, oldV, newV) ->
                updateStatsLabels());

        this.dashboardService.loadWarehouseSummary(this.sessionContext);
        updateStatsLabels();
    }

    private void updateStatsLabels() {
        if (sessionContext == null) {
            return;
        }
        if (activeBatchesValueLabel != null) {
            activeBatchesValueLabel.setText(
                    InventoryDashboardService.formatCount(sessionContext.getActiveBatchesCount()));
        }
        if (lowCountValueLabel != null) {
            int low = sessionContext.getLowCountAlerts();
            lowCountValueLabel.setText(InventoryDashboardService.formatCount(low));
            if (needsPoBadge != null) {
                needsPoBadge.setVisible(low > 0);
                needsPoBadge.setManaged(low > 0);
            }
        }
        if (expiredBatchesValueLabel != null) {
            expiredBatchesValueLabel.setText(
                    InventoryDashboardService.formatCount(sessionContext.getExpiredBatchesCount()));
        }
    }

    private void configureVisualEffectsAndClips() {
        if (statRibbonInner != null) {
            clipRounded(statRibbonInner, 16.0);
        }
        if (grnCardInner != null) {
            clipRounded(grnCardInner, 16.0);
        }
        if (disposalCardInner != null) {
            clipRounded(disposalCardInner, 16.0);
        }
        if (lowCountCardInner != null) {
            clipRounded(lowCountCardInner, 16.0);
        }

        GaussianBlur ribbonBlur = new GaussianBlur(63.0);
        ribbonBlur.setInput(new GaussianBlur(40.0));
        if (ribbonGlowRight != null) {
            ribbonGlowRight.setEffect(ribbonBlur);
        }
        if (ribbonGlowCenter != null) {
            ribbonGlowCenter.setEffect(ribbonBlur);
        }
        if (ribbonGlowLayer != null) {
            ribbonGlowLayer.layoutBoundsProperty().addListener((obs, oldB, b) -> {
                double w = b.getWidth();
                double h = b.getHeight();
                if (ribbonGlowRight != null) {
                    ribbonGlowRight.setCenterX(w - 64.0);
                    ribbonGlowRight.setCenterY(64.0);
                }
                if (ribbonGlowCenter != null) {
                    double ew = w * 0.21505;
                    ribbonGlowCenter.setRadiusX(ew / 2.0);
                    ribbonGlowCenter.setCenterX(w / 3.0 + ew / 2.0);
                    ribbonGlowCenter.setCenterY(h + 80.0 - 160.0);
                }
            });
        }

        if (grnGlowCircle != null) {
            grnGlowCircle.setEffect(new GaussianBlur(40.0));
        }
        if (grnGlowLayer != null && grnGlowCircle != null) {
            grnGlowLayer.layoutBoundsProperty().addListener((obs, oldB, b) -> {
                grnGlowCircle.setCenterX(b.getWidth() - 72.0);
                grnGlowCircle.setCenterY(72.0);
            });
        }

        if (grnCard != null && openGrnBtn != null) {
            grnCard.setOnMouseClicked(e -> openGrnBtn.fire());
        }
        if (disposalCard != null && openDisposalBtn != null) {
            disposalCard.setOnMouseClicked(e -> openDisposalBtn.fire());
        }
        if (lowCountCard != null && reviewShortagesBtn != null) {
            lowCountCard.setOnMouseClicked(e -> reviewShortagesBtn.fire());
        }
    }

    private static void clipRounded(Region region, double radius) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(radius * 2.0);
        clip.setArcHeight(radius * 2.0);
        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());
        region.setClip(clip);
    }

    private static void playEntranceAnimation(Node node) {
        if (node == null) {
            return;
        }
        node.setOpacity(0.0);
        node.setTranslateY(16.0);
        FadeTransition fade = new FadeTransition(Duration.millis(450), node);
        fade.setToValue(1.0);
        TranslateTransition slide = new TranslateTransition(Duration.millis(450), node);
        slide.setToY(0.0);
        new ParallelTransition(fade, slide).play();
    }

    @FXML
    private void onOpenGrnEntry() {
        if (dashboardService != null && sessionContext != null) {
            dashboardService.selectAction(sessionContext, InventoryAction.ADD_GRN);
        }
    }

    @FXML
    private void onOpenDisposalLogs() {
        if (dashboardService != null && sessionContext != null) {
            dashboardService.selectAction(sessionContext, InventoryAction.DISPOSAL);
        }
    }

    @FXML
    private void onReviewShortages() {
        if (dashboardService != null && sessionContext != null) {
            dashboardService.selectAction(sessionContext, InventoryAction.LOW_COUNT);
        }
    }
}
