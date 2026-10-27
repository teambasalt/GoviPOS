import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class InventoryManagerDashboard {

    public static void main(String[] args) {
        Application.launch(DashboardApp.class, args);
    }

    //  Design tokens (taken from the Figma file)

    private static final double DESIGN_WIDTH = 1366;
    private static final double DESIGN_HEIGHT = 768;
    private static final double HEADER_HEIGHT = 80;
    private static final double CONTAINER_WIDTH = 1046;   // central panel width at 1366px
    private static final double DESIGN_TOP_GAP = 70;      // gap between header and panel
    private static final double DESIGN_FREE_SPACE = 178;  // total vertical free space at 768px

    // Font keys -> resource files
    private static final String INTER_BOLD = "Inter-Bold";
    private static final String MANROPE_MEDIUM = "Manrope-Medium";
    private static final String MANROPE_SEMIBOLD = "Manrope-SemiBold";
    private static final String MANROPE_BOLD = "Manrope-Bold";
    private static final String GROTESK_MEDIUM = "SpaceGrotesk-Medium";
    private static final String GROTESK_BOLD = "SpaceGrotesk-Bold";

    private static final Map<String, String> FONT_NAMES = new HashMap<>();

    // Material icon paths (24x24 viewBox)
    private static final String ICON_ARROW =
            "M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z";
    private static final String ICON_STORE =
            "M21.9 8.89l-1.05-4.37c-.22-.9-1-1.52-1.91-1.52H5.05c-.9 0-1.69.63-1.9 1.52L2.1 8.89"
            + "c-.24 1.02-.02 2.06.62 2.88.08.11.19.19.28.29V19c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2v-6.94"
            + "c.09-.09.2-.18.28-.28.64-.82.87-1.87.62-2.89zm-2.99-3.9l1.05 4.37c.1.42.01.84-.25 1.17"
            + "-.14.18-.44.47-.94.47-.61 0-1.14-.49-1.21-1.14L16.98 5l1.93-.01zM13 5h1.96l.54 4.52"
            + "c.05.39-.07.78-.33 1.07-.22.26-.54.41-.95.41-.67 0-1.22-.59-1.22-1.31V5zM8.49 9.52"
            + "L9.04 5H11v4.69c0 .72-.55 1.31-1.29 1.31-.34 0-.65-.15-.89-.41-.25-.29-.37-.68-.33-1.07z"
            + "m-4.45-.16L5.05 5h1.97l-.58 4.86c-.08.65-.6 1.14-1.21 1.14-.49 0-.8-.29-.93-.47"
            + "-.27-.32-.36-.75-.26-1.17zM5 19v-6.03c.08.01.15.03.23.03.87 0 1.66-.36 2.24-.95"
            + ".6.6 1.4.95 2.31.95.87 0 1.65-.36 2.23-.93.59.57 1.39.93 2.29.93.84 0 1.64-.35 2.24-.95"
            + ".58.59 1.37.95 2.24.95.08 0 .15-.02.23-.03V19H5z";
    private static final String ICON_PERSON =
            "M12 5.9c1.16 0 2.1.94 2.1 2.1s-.94 2.1-2.1 2.1S9.9 9.16 9.9 8s.94-2.1 2.1-2.1m0 9"
            + "c2.97 0 6.1 1.46 6.1 2.1v1.1H5.9V17c0-.64 3.13-2.1 6.1-2.1M12 4C9.79 4 8 5.79 8 8"
            + "s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm0 9c-2.67 0-8 1.34-8 4v3h16v-3c0-2.66-5.33-4-8-4z";
    // Sprout-in-bowl brand mark (stroked)
    private static final String ICON_LOGO =
            "M12 13V8.5 M12 9.5C12 6.5 10 4.5 6.5 4.5C6.5 7.5 8.5 9.5 12 9.5Z "
            + "M12 8.5C12 6 13.8 4 17.5 4C17.5 6.8 15.5 8.5 12 8.5Z "
            + "M5 13H19 M6 13C6 17 8.6 20 12 20C15.4 20 18 17 18 13";

    //  Stylesheet (embedded so the dashboard is a single self-contained file)

    private static final String CSS = """
            .root-pane {
                -fx-background-color: linear-gradient(from 0% 0% to 100% 100%,
                    #0f5132 0%, #108b5e 40.87%, #10b981 100%);
            }
            .scroll-pane, .scroll-pane > .viewport, .scroll-pane .corner {
                -fx-background-color: transparent;
                -fx-background-insets: 0;
                -fx-padding: 0;
            }
            .scroll-pane .scroll-bar { -fx-opacity: 0.6; }

            /* ---------- Header ---------- */
            .header {
                -fx-background-color: #047857;
                -fx-border-color: transparent transparent rgba(255,255,255,0.85) transparent;
                -fx-border-width: 0 0 1 0;
                -fx-effect: dropshadow(gaussian, rgba(15,81,50,0.06), 8, 0, 0, 1);
            }
            .logo-box {
                -fx-background-color: #ffffff;
                -fx-background-radius: 8;
                -fx-effect: dropshadow(gaussian, rgba(15,81,50,0.25), 8, 0, 0, 2);
            }
            .divider { -fx-background-color: #e4e9e6; }
            .branch-pill {
                -fx-background-color: #eaefec;
                -fx-background-radius: 9999;
                -fx-padding: 6 12 6 12;
                -fx-cursor: hand;
            }
            .branch-pill:hover { -fx-background-color: #ffffff; }
            .change-role {
                -fx-background-color: #10b981;
                -fx-background-radius: 8;
                -fx-background-insets: 0;
                -fx-border-color: #e4e9e6;
                -fx-border-radius: 8;
                -fx-border-width: 1;
                -fx-text-fill: #ffffff;
                -fx-padding: 0 16 0 16;
                -fx-cursor: hand;
            }
            .change-role:hover   { -fx-background-color: #19c993; }
            .change-role:pressed { -fx-background-color: #0ea372; }
            .online-pill {
                -fx-background-color: #f0f5f2;
                -fx-background-radius: 9999;
                -fx-padding: 6 12 6 12;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 0, 1);
            }
            .avatar {
                -fx-background-color: #003820;
                -fx-background-radius: 9999;
                -fx-cursor: hand;
            }

            /* ---------- Central panel ---------- */
            .panel {
                -fx-background-color: linear-gradient(from 0% 0% to 100% 60%, #10b981 0%, #0f5132 100%);
                -fx-padding: 32 56 32 56;
            }
            .ribbon {
                -fx-background-color: linear-gradient(to right, #003820 0%, #0f5132 50%, #003825 100%);
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,1), 20, 0, 5, 8);
            }
            .stat-card {
                -fx-background-color: rgba(255,255,255,0.05);
                -fx-background-radius: 12;
                -fx-padding: 16;
            }
            .stat-card:hover { -fx-background-color: rgba(255,255,255,0.09); }
            .stat-card.alert { -fx-background-color: rgba(186,26,26,0.15); }
            .stat-card.alert:hover { -fx-background-color: rgba(186,26,26,0.22); }
            .badge {
                -fx-background-color: rgba(186,26,26,0.30);
                -fx-background-radius: 4;
                -fx-padding: 2 6 2 6;
            }

            .action-card {
                -fx-background-color: #047857;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.40), 20, 0.1, 2, 5);
            }
            .action-card:hover {
                -fx-background-color: #06875f;
                -fx-translate-y: -2;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.48), 24, 0.1, 2, 8);
            }
            .action-btn {
                -fx-background-radius: 12;
                -fx-background-insets: 0;
                -fx-padding: 10 16 10 16;
                -fx-text-fill: #ffffff;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);
            }
            .action-btn.grn      { -fx-background-color: #003820; }
            .action-btn.disposal { -fx-background-color: #003825; }
            .action-btn.low      { -fx-background-color: #0f5132; }
            .action-btn:hover    { -fx-background-color: #00553a; }
            .action-btn:pressed  { -fx-background-color: #002a18; }
            """;

   // ------------------------------------------------------------------------------------
    //  JavaFX Application
    // --------------------------------------------------------------------------------
    public static class DashboardApp extends Application {

        @Override
        public void start(Stage stage) {
            loadFonts();

            BorderPane root = new BorderPane();
            root.getStyleClass().add("root-pane");
            root.setTop(buildHeader());

            VBox panel = buildPanel();
            ResponsiveHolder holder = new ResponsiveHolder(panel);

            ScrollPane scroll = new ScrollPane(holder);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            root.setCenter(scroll);

            Scene scene = new Scene(root, DESIGN_WIDTH, DESIGN_HEIGHT);
            scene.getStylesheets().add("data:text/css;base64,"
                    + Base64.getEncoder().encodeToString(CSS.getBytes(StandardCharsets.UTF_8)));

            stage.setTitle("GoviPOS \u2013 Inventory Manager Dashboard");
            stage.setScene(scene);
            stage.setMinWidth(1024);
            stage.setMinHeight(640);
            stage.setMaximized(true);
            stage.show();

            playEntrance(panel);
        }
    }

    // ------------------------------------------------------------------------------------
    //  Header
    // ------------------------------------------------------------------------------------
    private static Node buildHeader() {
        // Brand: logo + name
        StackPane logo = new StackPane(strokedIcon(ICON_LOGO, 24, 1.15, Color.BLACK, 1.6));
        logo.getStyleClass().add("logo-box");
        fixSize(logo, 40, 40);

        Label brand = label("GoviPOS", GROTESK_BOLD, 18, Color.WHITE);
        Label suite = label("AGROHUB SUITE", MANROPE_SEMIBOLD, 12, Color.WHITE);
        VBox brandText = new VBox(0, brand, suite);
        brandText.setAlignment(Pos.CENTER_LEFT);

        HBox brandBox = new HBox(12, logo, brandText);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Region divider = new Region();
        divider.getStyleClass().add("divider");
        fixSize(divider, 1, 32);

        HBox branchPill = new HBox(8,
                icon(ICON_STORE, 18, 0.75, Color.web("#006c49")),
                label("Piliyandala #01", MANROPE_SEMIBOLD, 12, Color.web("#171d1b")));
        branchPill.setAlignment(Pos.CENTER_LEFT);
        branchPill.getStyleClass().add("branch-pill");
        branchPill.setMaxHeight(Region.USE_PREF_SIZE);

        HBox left = new HBox(24, brandBox, divider, branchPill);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(0, 24, 0, 0));

        // Right side: change role, online status, user
        Button changeRole = new Button("Change role");
        changeRole.getStyleClass().add("change-role");
        changeRole.setFont(font(GROTESK_MEDIUM, 12));
        fixSize(changeRole, 113, 30);
        changeRole.setOnAction(e -> System.out.println("Change role clicked"));

        Circle dot = new Circle(5, Color.web("#006c49"));
        HBox online = new HBox(8, dot, label("Online", MANROPE_SEMIBOLD, 12, Color.web("#003820")));
        online.setAlignment(Pos.CENTER_LEFT);
        online.getStyleClass().add("online-pill");
        online.setMaxHeight(Region.USE_PREF_SIZE);

        Label userName = label("Roshan Jayawardena", MANROPE_BOLD, 12, Color.WHITE);
        Label userRole = label("Inventory Officer", MANROPE_MEDIUM, 11, Color.WHITE);
        VBox userText = new VBox(0, userName, userRole);
        userText.setAlignment(Pos.CENTER_RIGHT);

        StackPane avatar = new StackPane(icon(ICON_PERSON, 18, 0.75, Color.WHITE));
        avatar.getStyleClass().add("avatar");
        fixSize(avatar, 32, 32);

        HBox user = new HBox(12, userText, avatar);
        user.setAlignment(Pos.CENTER_LEFT);
        user.setPadding(new Insets(0, 0, 0, 8));

        HBox right = new HBox(16, changeRole, online, user);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(left, spacer, right);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 32, 0, 32));
        header.setMinHeight(HEADER_HEIGHT);
        header.setPrefHeight(HEADER_HEIGHT);
        header.setMaxHeight(HEADER_HEIGHT);
        return header;
    }

    // ------------------------------------------------------------------------------------
    //  Central panel
    // ------------------------------------------------------------------------------------
    private static VBox buildPanel() {
        Label title = label("Central Inventory Operations", INTER_BOLD, 30, Color.WHITE);
        title.setMinHeight(40);
        VBox titleStrip = new VBox(title);
        titleStrip.setPadding(new Insets(0, 0, 8, 0));

        VBox panel = new VBox(32, titleStrip, buildStatRibbon(), buildActionCards());
        panel.getStyleClass().add("panel");
        panel.setFillWidth(true);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        return panel;
    }

    /** "Quick Warehouse Summary" stat ribbon with soft glow overlays. */
    private static Node buildStatRibbon() {
        StackPane active = statCard("ACTIVE BATCHES", Color.web("#ffffff", 0.7), "1,420", null, false, false);
        StackPane low = statCard("LOW COUNT ALERTS", Color.web("#ffdad6"), "3", "Needs PO", false, true);
        StackPane expired = statCard("BATCHES EXPIRED", Color.web("#ffffff", 0.7), "2", null, true, false);

        HBox stats = new HBox(16, active, low, expired);
        stats.setAlignment(Pos.TOP_CENTER);
        for (Node n : stats.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
            ((Region) n).setMaxWidth(Double.MAX_VALUE);
            ((Region) n).setMinWidth(0);
            ((Region) n).setPrefWidth(0);
        }
        stats.setPadding(new Insets(28, 32, 28, 32));

        // Decorative blurred glows (Figma "Overlay+Blur" layers)
        Circle glowRight = new Circle(128, Color.web("#006c49", 0.15));
        Ellipse glowCenter = new Ellipse(100.43, 160);
        glowCenter.setFill(Color.web("#6ffbbe", 0.10));
        glowRight.setEffect(strongBlur());
        glowCenter.setEffect(strongBlur());

        Pane glowLayer = glowLayer(glowRight, glowCenter);
        glowLayer.layoutBoundsProperty().addListener((o, ov, b) -> {
            double w = b.getWidth(), h = b.getHeight();
            // right-anchored circle: x = w - 192, y = -64 (256 x 256)
            glowRight.setCenterX(w - 64);
            glowRight.setCenterY(64);
            // scaled ellipse: x = 1/3 w, width = 21.5% w, bottom-anchored (bottom = h + 80)
            double ew = w * 0.21505;
            glowCenter.setRadiusX(ew / 2);
            glowCenter.setCenterX(w / 3.0 + ew / 2);
            glowCenter.setCenterY(h + 80 - 160);
        });

        StackPane inner = new StackPane(glowLayer, stats);
        clipRounded(inner, 16);

        StackPane ribbon = new StackPane(inner);
        ribbon.getStyleClass().add("ribbon");
        return ribbon;
    }

    private static StackPane statCard(String caption, Color captionColor, String value,
                                      String badgeText, boolean centerValue, boolean alert) {
        Label cap = label(caption, MANROPE_SEMIBOLD, 12, captionColor);
        cap.setMinHeight(16);

        Label val = label(value, GROTESK_BOLD, 36, Color.WHITE);
        val.setMinHeight(40);

        HBox valueRow = new HBox(8, val);
        valueRow.setAlignment(centerValue ? Pos.BASELINE_CENTER : Pos.BASELINE_LEFT);
        valueRow.setMinHeight(40);
        if (badgeText != null) {
            Label badgeLbl = label(badgeText, MANROPE_BOLD, 12, Color.web("#ffdad6"));
            StackPane badge = new StackPane(badgeLbl);
            badge.getStyleClass().add("badge");
            badge.setMaxHeight(Region.USE_PREF_SIZE);
            valueRow.getChildren().add(badge);
        }
        VBox.setMargin(valueRow, new Insets(8, 0, 0, 0));

        VBox content = new VBox(0, cap, valueRow);
        StackPane card = new StackPane(content);
        card.getStyleClass().add("stat-card");
        if (alert) card.getStyleClass().add("alert");
        return card;
    }

    /** "Main Navigation Architecture" - three large clickable cards. */
    private static Node buildActionCards() {
        Node grn = actionCard("Add GRNs", "Open GRN Entry", "grn", 10,
                Color.web("#6ffbbe", 0.20), true);
        Node disposal = actionCard("Disposal", "Open Disposal Logs", "disposal", 0,
                Color.web("#68fcbf", 0.20), false);
        Node low = actionCard("Low Count", "Review Shortages", "low", 0,
                Color.web("#ffdad6", 0.30), false);

        HBox row = new HBox(24, grn, disposal, low);
        row.setAlignment(Pos.TOP_CENTER);
        for (Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
            ((Region) n).setMaxWidth(Double.MAX_VALUE);
            ((Region) n).setMinWidth(0);
            ((Region) n).setPrefWidth(0);
        }
        return row;
    }

    private static Node actionCard(String title, String action, String variant, double gap,
                                   Color glowColor, boolean glowVisible) {
        Label heading = label(title, INTER_BOLD, 24, Color.WHITE);
        heading.setMinHeight(32);

        Button btn = new Button(action);
        btn.getStyleClass().addAll("action-btn", variant);
        btn.setFont(font(GROTESK_BOLD, 12));
        btn.setGraphic(icon(ICON_ARROW, 16, 2.0 / 3.0, Color.WHITE));
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setGraphicTextGap(6);
        btn.setMinWidth(Region.USE_PREF_SIZE);
        btn.setOnAction(e -> System.out.println(action + " clicked"));

        // Footer margin (24) + footer padding (24) as in the Figma auto-layout
        VBox footer = new VBox(btn);
        footer.setPadding(new Insets(48, 0, 0, 0));

        VBox content = new VBox(gap, heading, footer);
        content.setPadding(new Insets(28));
        content.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane inner = new StackPane();
        if (glowVisible) {
            // "Glow Pill on Top Right": 144 x 144, right-anchored, top 0, blur 40
            Circle glow = new Circle(72, glowColor);
            glow.setEffect(new GaussianBlur(40));
            Pane layer = glowLayer(glow);
            layer.layoutBoundsProperty().addListener((o, ov, b) -> {
                glow.setCenterX(b.getWidth() - 72);
                glow.setCenterY(72);
            });
            inner.getChildren().add(layer);
        }
        inner.getChildren().add(content);
        StackPane.setAlignment(content, Pos.TOP_LEFT);
        clipRounded(inner, 16);

        StackPane card = new StackPane(inner);
        card.getStyleClass().add("action-card");
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setOnMouseClicked(e -> btn.fire());
        return card;
    }

    // ------------------------------------------------------------------------------------
    //  Responsive holder: centres the panel exactly like the 1366x768 frame and adapts
    //  width / vertical offset to the window size.
    // ------------------------------------------------------------------------------------
    private static final class ResponsiveHolder extends Pane {
        private static final double MIN_SIDE = 32;
        private static final double MIN_V = 24;
        private final Region content;

        ResponsiveHolder(Region content) {
            this.content = content;
            getChildren().add(content);
        }

        private double panelWidth(double w) {
            // 1046px at 1366 wide; grows proportionally on wider screens,
            // shrinks down to the window width minus margins on narrower ones.
            double proportional = w * CONTAINER_WIDTH / DESIGN_WIDTH;
            double fixed = Math.min(w - 2 * MIN_SIDE, CONTAINER_WIDTH);
            return Math.max(0, Math.max(proportional, fixed));
        }

        @Override
        protected void layoutChildren() {
            double w = getWidth(), h = getHeight();
            double cw = panelWidth(w);
            double ch = content.prefHeight(cw);
            double free = Math.max(0, h - ch);
            double top = Math.max(free * DESIGN_TOP_GAP / DESIGN_FREE_SPACE, Math.min(MIN_V, free / 2));
            content.resizeRelocate(Math.round((w - cw) / 2), Math.round(top), cw, ch);
        }

        @Override
        protected double computePrefHeight(double width) {
            double w = width > 0 ? width : DESIGN_WIDTH;
            return content.prefHeight(panelWidth(w)) + 2 * MIN_V;
        }

        @Override
        protected double computeMinHeight(double width) {
            return computePrefHeight(width);
        }

        @Override
        protected double computePrefWidth(double height) {
            return DESIGN_WIDTH;
        }
    }

    // ------------------------------------------------------------------------------------
    //  Helpers
    // ------------------------------------------------------------------------------------
    private static void loadFonts() {
        String[] keys = {INTER_BOLD, MANROPE_MEDIUM, MANROPE_SEMIBOLD, MANROPE_BOLD, GROTESK_MEDIUM, GROTESK_BOLD};
        for (String key : keys) {
            try (InputStream in = InventoryManagerDashboard.class.getResourceAsStream("/fonts/" + key + ".ttf")) {
                if (in == null) continue;
                Font f = Font.loadFont(in, 12);
                if (f != null) FONT_NAMES.put(key, f.getName());
            } catch (Exception ex) {
                System.err.println("Could not load font " + key + ": " + ex.getMessage());
            }
        }
    }

    private static Font font(String key, double size) {
        String name = FONT_NAMES.get(key);
        if (name != null) return new Font(name, size);
        // Fallback when the bundled font files are not on the classpath
        FontWeight weight = key.endsWith("Bold") && !key.endsWith("SemiBold") ? FontWeight.BOLD
                : key.endsWith("SemiBold") ? FontWeight.SEMI_BOLD : FontWeight.MEDIUM;
        return Font.font("Segoe UI", weight, size);
    }

    private static Label label(String text, String fontKey, double size, Color color) {
        Label l = new Label(text);
        l.setFont(font(fontKey, size));
        l.setTextFill(color);
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    private static StackPane icon(String path, double box, double scale, Color fill) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.setFill(fill);
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        StackPane sp = new StackPane(new Group(svg));
        fixSize(sp, box, box);
        sp.setMouseTransparent(true);
        return sp;
    }

    private static StackPane strokedIcon(String path, double box, double scale, Color stroke, double width) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(stroke);
        svg.setStrokeWidth(width);
        svg.setStrokeLineCap(StrokeLineCap.ROUND);
        svg.setStrokeLineJoin(StrokeLineJoin.ROUND);
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        StackPane sp = new StackPane(new Group(svg));
        fixSize(sp, box, box);
        return sp;
    }

    /** A zero-pref-size pane that fills its StackPane parent and hosts unmanaged glow shapes. */
    private static Pane glowLayer(Node... glows) {
        Pane layer = new Pane();
        layer.setMouseTransparent(true);
        layer.setMinSize(0, 0);
        layer.setPrefSize(0, 0);
        layer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        for (Node g : glows) {
            g.setManaged(false);
            layer.getChildren().add(g);
        }
        return layer;
    }

    private static GaussianBlur strongBlur() {
        // Figma layer blur 64 ~ two chained JavaFX gaussian passes
        GaussianBlur outer = new GaussianBlur(63);
        outer.setInput(new GaussianBlur(40));
        return outer;
    }

    private static void clipRounded(Region region, double radius) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());
        region.setClip(clip);
    }

    private static void fixSize(Region r, double w, double h) {
        r.setMinSize(w, h);
        r.setPrefSize(w, h);
        r.setMaxSize(w, h);
    }

    private static void playEntrance(Node node) {
        node.setOpacity(0);
        node.setTranslateY(16);
        FadeTransition fade = new FadeTransition(Duration.millis(450), node);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(450), node);
        slide.setToY(0);
        new ParallelTransition(fade, slide).play();
    }
}
