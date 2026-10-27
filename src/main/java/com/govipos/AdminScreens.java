package com.govipos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Reference-driven admin screens. Form state is local and never submitted or stored. */
public final class AdminScreens {
    private AdminScreens() {}

    public static Scene createBranch(Stage stage, Runnable back) {
        // Keep the sample values from the design so the form looks populated in the demo.
        VBox basic = section("01", "Basic Branch Information");
        basic.getChildren().add(field("BRANCH NAME", input("Polonnaruwa Central AgroHub"), true));
        TextField code = input("AGRO-HUB-109"); code.getStyleClass().add("branch-code");
        VBox identifier = field("BRANCH CODE / IDENTIFIER", code, true);
        Label auto = text("⟳ Auto-generate", "auto-label");
        // Auto-generate is just a label in this prototype; the branch code stays editable.
        Node identifierCaption = identifier.getChildren().remove(0);
        StackPane codeCaption = new StackPane(identifierCaption, auto);
        StackPane.setAlignment(identifierCaption, Pos.CENTER_LEFT);
        StackPane.setAlignment(auto, Pos.CENTER_RIGHT);
        identifier.getChildren().add(0, codeCaption);
        basic.getChildren().add(half(identifier));

        VBox location = section("02", "Location & Regional Jurisdiction");
        location.getChildren().add(half(field("PROVINCE", choice("North Central Province"), true)));
        TextArea address = new TextArea("No. 45, Commercial Agri-Industrial Zone, New Town, Polonnaruwa");
        address.setWrapText(true); address.setPrefRowCount(2);
        location.getChildren().add(field("PHYSICAL STREET ADDRESS", address, true));
        location.getChildren().add(half(field("BRANCH DIRECT PHONE", input("+94 27 222 4910"), true)));

        VBox operations = section("03", "Operations & Infrastructure Provisioning");
        operations.getChildren().add(pair(
                field("ASSIGNED INITIAL BRANCH MANAGER", choice("Kavinda Perera (ID: STF-4029) - Certified"), true),
                field("CONCURRENT POS LANES / TERMINALS", choice("4"), true)));
        Button register = action("♧  Register Branch", "solid-action");
        // Registration is intentionally unwired: this screen does not save branch data.
        HBox footer = new HBox(register); footer.setAlignment(Pos.CENTER_RIGHT);
        VBox form = new VBox(28, basic, location, operations, footer);
        form.getStyleClass().add("branch-form"); form.setMaxWidth(1100);
        StackPane formWrap = new StackPane(form); formWrap.setAlignment(Pos.TOP_CENTER);
        VBox content = new VBox(22, text("Register New Branch", "screen-title"), formWrap);
        content.getStyleClass().add("branch-content");
        return shell(stage, content, back, "Create Branch");
    }

    public static Scene removeBranch(Stage stage, Runnable back) {
        // These counts and audit rows come from the reference, not a live branch list.
        GridPane summaries = grid(4, 16);
        summaries.add(summary("▤", "Registered Hubs", "32 Depots", false), 0, 0);
        summaries.add(summary("✓", "Active & Balanced", "31 Nodes", false), 1, 0);
        summaries.add(summary("▣", "Awaiting Permanent\nPurge", "1 Branch", true), 2, 0);
        summaries.add(summary("⚿", "Revoked Terminals", "3 Keys", false), 3, 0);
        TextField search = new TextField(); search.setPromptText("⌕  Search branch name, code or district...");
        search.getStyleClass().add("search-field");
        VBox audit = whitePanel(text("Facility Nodes Audit", "panel-title"), search,
                branchTable());
        audit.setId("branch-audit");

        Label confirm = text("Type branch code AGRO-HUB-107 to confirm deletion:", "small-label");
        TextField branchCode = new TextField(); branchCode.setPromptText("AGRO-HUB-107");
        PasswordField password = new PasswordField(); password.setPromptText("••••••••••••••••");
        Button backButton = action("← Back to\nDashboard", "muted-action"); backButton.setOnAction(event -> back.run());
        Button remove = action("▣   Permanently\nRemove Branch", "disabled-danger");
        // Keep the disabled state shown in the design, even when the fields are filled in.
        remove.setDisable(true);
        VBox confirmation = whitePanel(new VBox(8, confirm, branchCode),
                new VBox(10, text("Admin Master Password", "small-label"), password), pair(backButton, remove));
        confirmation.getStyleClass().add("removal-confirmation");
        GridPane main = split(audit, confirmation, 59, 41);
        VBox content = new VBox(16, text("Remove Branch Interface", "screen-title"), summaries, main);
        content.getStyleClass().add("management-content");
        return shell(stage, content, back, "Remove Branch");
    }

    public static Scene assignBranch(Stage stage, Runnable back) {
        Button addAccount = action("♧  + Add New Staff Account", "teal-action");
        addAccount.setId("open-create-account");
        // Returning from account creation rebuilds this screen and clears unsaved selections.
        addAccount.setOnAction(event -> stage.setScene(createAccount(stage, () -> stage.setScene(assignBranch(stage, back)))));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox heading = new HBox(18, text("Staff Role & Access Management (RBAC)", "screen-title"), spacer, addAccount);
        heading.setAlignment(Pos.CENTER_LEFT);
        TextField search = new TextField(); search.setPromptText("⌕  Search staff by name, Employee ID, or branch...");
        search.getStyleClass().add("search-field");
        VBox searchPanel = whitePanel(search); searchPanel.getStyleClass().add("search-panel");
        VBox directory = new VBox(text("♙  ACTIVE STAFF PERSONNEL DIRECTORY", "directory-caption"), staffTable());
        directory.getStyleClass().add("directory-panel");
        VBox left = new VBox(16, searchPanel, directory);

        Label guard = text("Live Guard", "guard-badge");
        Region gap = new Region(); HBox.setHgrow(gap, Priority.ALWAYS);
        HBox quickHeading = new HBox(10, text("▣", "quick-icon"), text("Quick Assign", "panel-title"), gap, guard);
        quickHeading.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> staff = choice(null); staff.setPromptText("name / ID");
        staff.getItems().addAll("Priyantha Kumara", "Roshan Jayawardena", "Kavinda Perera");
        ComboBox<String> roles = choice("Cashier"); roles.getItems().addAll("Inventory Officer", "Branch Manager", "Admin");
        ComboBox<String> branch = choice(null); branch.getItems().add("Downtown AgroHub #102");
        Button save = action("◉  Save & Enforce Role Assignment", "solid-action");
        // Role selections are local to this form; Save does not change the directory rows.
        VBox quick = whitePanel(quickHeading, formField("Select Staff Member", staff), formField("Assign New Role", roles),
                formField("Target Branch Location", branch), save);
        quick.getStyleClass().add("quick-panel");
        // Selecting a directory action only changes the local form selection.
        left.lookupAll(".staff-edit").forEach(node -> ((Button) node).setOnAction(event -> staff.setValue((String) node.getUserData())));
        GridPane body = split(left, quick, 68, 32);
        VBox content = new VBox(20, heading, body); content.getStyleClass().add("management-content");
        content.getStyleClass().add("staff-content");
        return shell(stage, content, back, "Assign Branch");
    }

    public static Scene createAccount(Stage stage, Runnable back) {
        stage.setMinHeight(600); stage.setTitle("GoviPOS — Create Account");
        StackPane brand = new StackPane(text("Create a new account", "account-headline"));
        brand.getStyleClass().add("account-brand"); brand.setPadding(new Insets(45));
        ComboBox<String> role = choice(null); role.setPromptText("Select role");
        role.getItems().addAll("Admin", "Cashier", "Branch Manager", "Inventory Officer");
        TextField name = new TextField(); name.setPromptText("silhas wellihinda");
        TextField email = new TextField(); email.setPromptText("user@gmail.com");
        PasswordField password = new PasswordField(); password.setPromptText("Create strong terminal password");
        Button create = action("Create a new account  →", "account-submit");
        VBox form = new VBox(25, text("Create Account", "account-title"),
                field("SELECT ROLE TYPE", role, false),
                pair(field("FULL NAME", name, false), field("WORK EMAIL", email, false)),
                field("ASSIGNED STORE BRANCH", input("Piliyandala #01"), false), field("PASSWORD", password, false), create);
        form.setMaxWidth(370); form.getStyleClass().add("account-form");
        form.setMaxHeight(Region.USE_PREF_SIZE);
        // Prevent the VBox from stretching, so the whole form stays vertically centred.
        StackPane right = new StackPane(form); right.setPadding(new Insets(45)); right.getStyleClass().add("account-white");
        GridPane root = grid(2, 0); root.getStyleClass().add("account-screen");
        root.add(brand, 0, 0); root.add(right, 1, 0);
        RowConstraints row = new RowConstraints(); row.setPercentHeight(100); root.getRowConstraints().add(row);
        // The reference has no Back button. Escape returns to the staff screen without saving.
        Scene scene = styled(root, 1280, 720);
        scene.setOnKeyPressed(event -> { if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) back.run(); });
        return scene;
    }

    private static Scene shell(Stage stage, VBox content, Runnable back, String title) {
        stage.setMinHeight(600); stage.setTitle("GoviPOS — " + title);
        Button backButton = action("←  Back to Inventory Hub", "back-button");
        backButton.setId("back-to-hub"); backButton.setOnAction(event -> back.run());
        HBox top = new HBox(backButton); top.getStyleClass().add("admin-topbar");
        ScrollPane scroll = new ScrollPane(content); scroll.setFitToWidth(true); scroll.setPannable(false);
        // The header stays visible while long forms scroll underneath it.
        scroll.getStyleClass().add("admin-scroll"); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setMinHeight(0); content.setMaxWidth(Double.MAX_VALUE);
        BorderPane root = new BorderPane(scroll, top, null, null, null); root.getStyleClass().add("admin-screen");
        return styled(root, 1280, title.equals("Create Branch") ? 1100 : 720);
    }

    private static Scene styled(javafx.scene.Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(AdminScreens.class.getResource("admin-screens.css").toExternalForm());
        return scene;
    }

    private static VBox section(String number, String title) {
        Label badge = text(number, "section-number");
        HBox heading = new HBox(12, badge, text(title, "form-section-title")); heading.setAlignment(Pos.CENTER_LEFT);
        VBox section = new VBox(20, heading); section.getStyleClass().add("form-section"); return section;
    }

    private static TextField input(String value) { return new TextField(value); }
    private static ComboBox<String> choice(String value) {
        ComboBox<String> box = new ComboBox<>(); box.setMinWidth(0); box.setMaxWidth(Double.MAX_VALUE);
        if (value != null) { box.getItems().add(value); box.setValue(value); } return box;
    }

    private static VBox field(String title, Node control, boolean required) {
        // The asterisk matches the artwork; it does not enable required-field validation.
        Label label = text(title, "input-caption");
        HBox caption = new HBox(3, label);
        if (required) caption.getChildren().add(text("*", "required-star"));
        VBox field = new VBox(8, caption, control); field.setMinWidth(0); return field;
    }
    private static VBox formField(String title, Node control) { return new VBox(8, text(title, "small-label"), control); }
    private static GridPane pair(Node first, Node second) {
        GridPane grid = grid(2, 18); grid.add(first, 0, 0); grid.add(second, 1, 0);
        GridPane.setHgrow(first, Priority.ALWAYS); GridPane.setHgrow(second, Priority.ALWAYS); return grid;
    }
    // Reserve the second column to keep short fields at half width, as in the branch form.
    private static GridPane half(Node field) { return pair(field, new Region()); }
    private static GridPane grid(int count, double gap) {
        GridPane grid = new GridPane(); grid.setHgap(gap); grid.setVgap(16);
        for (int i = 0; i < count; i++) {
            ColumnConstraints column = new ColumnConstraints(); column.setPercentWidth(100.0 / count); column.setMinWidth(0);
            grid.getColumnConstraints().add(column);
        }
        return grid;
    }

    private static GridPane split(Node left, Node right, double leftPercent, double rightPercent) {
        GridPane grid = grid(2, 28); grid.getColumnConstraints().get(0).setPercentWidth(leftPercent);
        grid.getColumnConstraints().get(1).setPercentWidth(rightPercent);
        grid.add(left, 0, 0); grid.add(right, 1, 0); GridPane.setValignment(right, javafx.geometry.VPos.TOP);
        GridPane.setFillHeight(right, false);
        // Stack the side panel on smaller windows, and restore both columns when widened.
        grid.widthProperty().addListener((observable, before, width) -> {
            boolean stacked = width.doubleValue() < 850;
            GridPane.setRowIndex(right, stacked ? 1 : 0); GridPane.setColumnIndex(right, stacked ? 0 : 1);
            GridPane.setColumnSpan(left, stacked ? 2 : 1); GridPane.setColumnSpan(right, stacked ? 2 : 1);
        });
        return grid;
    }

    private static VBox whitePanel(Node... children) {
        VBox panel = new VBox(22, children); panel.setMinWidth(0); panel.getStyleClass().add("white-panel"); return panel;
    }
    private static HBox summary(String icon, String title, String value, boolean danger) {
        Label symbol = text(icon, danger ? "summary-icon-danger" : "summary-icon");
        VBox words = new VBox(4, text(title, "summary-caption"), text(value, danger ? "summary-value-danger" : "summary-value"));
        HBox summary = new HBox(14, symbol, words); summary.setAlignment(Pos.CENTER_LEFT); summary.getStyleClass().add("summary-card"); return summary;
    }

    private static GridPane branchTable() {
        GridPane table = table(new String[]{"BRANCH & CODE", "DISTRICT", "POS TERMINALS", "INVENTORY", "STAFF", "STATUS"}, new double[]{20, 19, 21, 17, 16, 17});
        // Some values are cut off in the image; keep the agreed ellipses rather than guessing.
        String[][] rows = {
            {"Downtown\nAgroHub #102\nAGRO-HUB-102", "Colombo Dist", "● 4 Online", "Rs.xxx,xx", "8 Members", "Active / Heal…"},
            {"Anuradhapura\nNorth Depot\n#105\nAGRO-HUB-105", "Anuradhapura Dist", "● 3 Online", "Rs.XXXX.00", "5 Members", "Active"},
            {"Kandy\nHighlands\nPost #107\nAGRO-HUB-107", "Kandy Dist", "○ 0 POS (Offline)", "0.00 (Zeroed)", "0 (Reassigned)", "Ready for Pe…"}
        };
        for (int row = 0; row < rows.length; row++) for (int col = 0; col < 6; col++) {
            Label cell = text(rows[row][col], col == 0 ? "branch-table-name" : "table-text");
            if (row == 2 && (col == 0 || col == 5)) cell.getStyleClass().add("red-text");
            if (col == 2 || col == 3) cell.getStyleClass().add("green-text");
            cell.setPadding(new Insets(12, 8, 12, 8)); cell.setMinHeight(row == 1 ? 100 : 76);
            table.add(cell, col, row + 1);
        }
        return table;
    }

    private static GridPane staffTable() {
        GridPane table = table(new String[]{"STAFF MEMBER", "ASSIGNED BRANCH", "ROLE ASSIGNMENT", "LAST ACTIVITY", "ACTIONS"}, new double[]{26, 20, 21, 20, 13});
        String[][] rows = {
            {"PK", "Priyantha Kumara", "EMP-2024-081", "Cashier", "14:20"},
            {"RJ", "Roshan\nJayawardena", "EMP-2023-114", "Inventory Officer", "14:02"},
            {"KP", "Kavinda Perera  ⚿", "EMP-2022-045", "Branch Manager", "14:32"}
        };
        for (int i = 0; i < rows.length; i++) {
            Label avatar = text(rows[i][0], "staff-avatar"); avatar.getStyleClass().add("staff-avatar-" + i);
            HBox identity = new HBox(10, avatar, new VBox(3, text(rows[i][1], "staff-name"), text(rows[i][2], "employee-id")));
            identity.setAlignment(Pos.CENTER_LEFT); identity.setPadding(new Insets(13));
            Label branch = text("Downtown AgroHub\n#102", "table-text");
            Label role = text(rows[i][3], "role-badge"); role.getStyleClass().add("role-" + i);
            Label activity = text("Active today\n" + rows[i][4], "activity-text");
            Button edit = action("≔", "table-action"); edit.getStyleClass().add("staff-edit");
            edit.setAccessibleText("Assign role to " + rows[i][1].replace('\n', ' '));
            // Store the plain name on the button so Quick Assign can select the same person.
            edit.setUserData(rows[i][1].replace("  ⚿", "").replace('\n', ' '));
            Button deactivate = action("⊘", "table-action"); deactivate.setAccessibleText("Deactivate " + rows[i][1].replace('\n', ' '));
            HBox actions = new HBox(8, edit, deactivate); actions.setAlignment(Pos.CENTER_LEFT);
            table.add(identity, 0, i + 1); table.add(branch, 1, i + 1); table.add(role, 2, i + 1);
            table.add(activity, 3, i + 1); table.add(actions, 4, i + 1);
        }
        return table;
    }

    private static GridPane table(String[] headings, double[] percentages) {
        GridPane table = new GridPane(); table.getStyleClass().add("reference-table");
        double total = java.util.Arrays.stream(percentages).sum();
        // Treat the supplied widths as proportions, even if they do not add up to 100.
        for (int i = 0; i < headings.length; i++) {
            ColumnConstraints column = new ColumnConstraints(); column.setPercentWidth(percentages[i] / total * 100); column.setMinWidth(0);
            table.getColumnConstraints().add(column);
            Label heading = text(headings[i], "table-heading"); heading.setMaxWidth(Double.MAX_VALUE); heading.setMinHeight(44);
            table.add(heading, i, 0);
        }
        return table;
    }
    private static Button action(String value, String style) {
        Button button = new Button(value); button.getStyleClass().add(style); button.setWrapText(true); button.setMaxWidth(Double.MAX_VALUE); return button;
    }
    private static Label text(String value, String style) {
        Label label = new Label(value); label.getStyleClass().add(style); label.setWrapText(true); label.setMinWidth(0); return label;
    }
}
