package com.govipos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/** Admin overview. All data and interactions are local presentation state. */
public final class AdminDashboard {
    private AdminDashboard() {}

    public static Scene create(Stage owner, Runnable logout) {
        owner.setMinHeight(680);
        Label brand = label("GoviPOS", "brand-name");
        VBox wordmark = new VBox(0, brand, label("AGROHUB SUITE", "brand-caption"));
        HBox identity = new HBox(10, icon("leaf"), wordmark);
        identity.setAlignment(Pos.CENTER_LEFT);
        identity.getStyleClass().add("identity");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button local = new Button("Change role");
        local.getStyleClass().add("local-badge");
        local.setOnAction(event -> logout.run());
        Label online = label("● Online", "online-badge");
        // Display name is reference artwork text, not a credential or signed-in identity.
        VBox account = new VBox(2, label("Senaka Bandara", "account-name"), label("Admin", "account-caption"));
        account.setAlignment(Pos.CENTER_RIGHT);
        Button profile = new Button("A");
        profile.getStyleClass().add("avatar");
        profile.setAccessibleText("Admin account menu");
        MenuItem signOut = new MenuItem("Sign out");
        signOut.setOnAction(event -> logout.run());
        ContextMenu menu = new ContextMenu(signOut);
        profile.setOnAction(event -> menu.show(profile, javafx.geometry.Side.BOTTOM, 0, 8));
        HBox header = new HBox(16, identity, spacer, local, online, account, profile);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 30, 16, 30));
        header.getStyleClass().add("dashboard-header");

        // Equal-width columns preserve the three-card Figma composition on desktop.
        GridPane statistics = columns();
        statistics.add(metric("ACTIVE REGIONAL", "BRANCHES", "18 Branches", "9 Agricultural", "Provinces", .96), 0, 0);
        statistics.add(metric("LICENSED POS", "TERMINALS", "64 POS\nLanes", "All 18", "Branches", .88), 1, 0);
        statistics.add(metric("SYSTEM USERS & ROLES", "", "142\nAccounts", "Active Staff Allocations", "4 Tiers Defined", .96), 2, 0);
        GridPane.setVgrow(statistics, Priority.ALWAYS);
        Label title = label("Admin Central Management Hub", "dashboard-title");
        Label section = label("Primary Operational Controls", "section-title");
        GridPane controls = columns();
        controls.add(operation(owner, "Add Branch", "Add Branch", "branch", false), 0, 0);
        controls.add(operation(owner, "Remove Branch", "Open Branch Removal Console", "remove", true), 1, 0);
        controls.add(operation(owner, "Assign Roles", "Manage Role Allocations", "roles", false), 2, 0);
        VBox content = new VBox(20, title, statistics, section, controls);
        content.setPadding(new Insets(26, 30, 40, 30));
        VBox.setVgrow(statistics, Priority.ALWAYS); VBox.setVgrow(controls, Priority.ALWAYS);
        BorderPane root = new BorderPane(content, header, null, null, null);
        root.getStyleClass().add("dashboard");
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(AdminDashboard.class.getResource("dashboard.css").toExternalForm());
        return scene;
    }

    private static GridPane columns() {
        GridPane grid = new GridPane(); grid.setHgap(22);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints(); column.setPercentWidth(100.0 / 3);
            column.setMinWidth(0); column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
        RowConstraints row = new RowConstraints(); row.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(row);
        return grid;
    }

    private static VBox metric(String category, String caption, String value, String status, String detail, double progress) {
        Region space = new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        ProgressBar bar = new ProgressBar(progress); bar.setMaxWidth(Double.MAX_VALUE);
        VBox card = new VBox(5, label(category, "metric-category"), label(caption, "metric-caption"),
                label(value, "metric-value"), space, label(status, "metric-caption"), label(detail, "metric-detail"), bar);
        card.getStyleClass().add("metric-card"); card.setMinHeight(210);
        return card;
    }

    private static VBox operation(Stage owner, String title, String action, String symbol, boolean destructive) {
        StackPane tile = new StackPane(icon(symbol)); tile.getStyleClass().add("operation-icon");
        tile.setMaxSize(42, 42); tile.setMinSize(42, 42);
        Region space = new Region(); VBox.setVgrow(space, Priority.ALWAYS);
        Button button = new Button(action + "   →"); button.setMaxWidth(Double.MAX_VALUE);
        button.setWrapText(true);
        button.getStyleClass().add(destructive ? "danger-action" : "primary-action");
        // Dialogs are presentation-only: no validation, persistence, or server calls.
        button.setOnAction(event -> showPanel(owner, title));
        VBox card = new VBox(18, tile, label(title, "operation-title"), space, button);
        card.setMinHeight(218); card.getStyleClass().add("operation-card");
        if (destructive) card.getStyleClass().add("remove-card");
        return card;
    }

    private static void showPanel(Stage owner, String title) {
        Dialog<Void> dialog = new Dialog<>(); dialog.initOwner(owner); dialog.setTitle(title);
        dialog.setHeaderText(title);
        VBox fields = new VBox(12); fields.setPadding(new Insets(12)); fields.setPrefWidth(350);
        if (title.equals("Add Branch")) {
            fields.getChildren().addAll(new Label("Branch name"), new TextField(), new Label("Location"), new TextField());
        } else if (title.equals("Remove Branch")) {
            ComboBox<String> branches = new ComboBox<>(); branches.getItems().addAll("Colombo", "Kandy", "Galle");
            branches.setPromptText("Select branch"); branches.setMaxWidth(Double.MAX_VALUE);
            fields.getChildren().addAll(new Label("Branch"), branches);
        } else {
            TextField user = new TextField(); user.setPromptText("Username");
            ComboBox<String> role = new ComboBox<>(); role.getItems().addAll("Admin", "Cashier", "Branch Manager", "Inventory Officer");
            role.setPromptText("Select role"); role.setMaxWidth(Double.MAX_VALUE);
            fields.getChildren().addAll(new Label("User"), user, new Label("Role"), role);
        }
        dialog.getDialogPane().setContent(fields);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Done", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(AdminDashboard.class.getResource("dashboard.css").toExternalForm());
        dialog.showAndWait();
    }

    private static Label label(String text, String style) {
        Label label = new Label(text); label.getStyleClass().add(style); label.setWrapText(true); return label;
    }

    // Small vector icons stay crisp as the desktop window is resized.
    private static SVGPath icon(String name) {
        SVGPath path = new SVGPath();
        path.setContent(switch (name) {
            case "branch" -> "M3 21 V9 L12 3 L21 9 V21 H3 M9 21 V14 H15 V21 M7 10 H9 M15 10 H17";
            case "remove" -> "M4 7 H20 M9 7 V3 H15 V7 M6 7 L7 21 H17 L18 7 M10 11 V17 M14 11 V17";
            case "roles" -> "M12 3 L21 7 V13 Q20 19 12 22 Q4 19 3 13 V7 Z M8 12 L11 15 L16 9";
            default -> "M3 21 Q6 12 18 6 M6 16 Q0 3 21 3 Q23 23 6 16";
        });
        path.getStyleClass().add("line-icon"); return path;
    }
}
