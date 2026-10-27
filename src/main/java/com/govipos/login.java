package com.govipos;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Frontend-only navigation; fields never authenticate or persist credentials. */
public final class login extends Application {
    private final ComboBox<String> roles = new ComboBox<>();
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final TextField visiblePassword = new TextField();
    private final Label headline = new Label("Log in to the account");
    private boolean roleLogin;
    private boolean startupRouteConsumed;

    @Override public void start(Stage stage) {
        // Rebuilding the login on sign-out resets all transient presentation state.
        roleLogin = false;
        headline.setText("Log in to the account");
        roles.getItems().clear();
        username.clear(); password.clear(); roles.setValue(null);
        roles.getItems().addAll("Admin", "Cashier", "Branch Manager", "Inventory Officer");
        roles.setPromptText("Select role");
        roles.setMaxWidth(Double.MAX_VALUE);
        username.setPromptText("Enter email");
        password.setPromptText("••••••••");
        visiblePassword.textProperty().bindBidirectional(password.textProperty());
        visiblePassword.setVisible(false);
        visiblePassword.setManaged(false);
        Button eye = new Button("◉");
        eye.getStyleClass().add("eye");
        eye.setAccessibleText("Show password");
        eye.setOnAction(event -> {
            boolean show = !visiblePassword.isVisible();
            visiblePassword.setVisible(show); visiblePassword.setManaged(show);
            password.setVisible(!show); password.setManaged(!show);
            eye.setAccessibleText(show ? "Hide password" : "Show password");
        });
        StackPane secret = new StackPane(password, visiblePassword);
        HBox.setHgrow(secret, Priority.ALWAYS);
        HBox passwordBox = new HBox(secret, eye);
        passwordBox.getStyleClass().add("password-box");

        Label welcome = new Label("Welcome back");
        welcome.getStyleClass().add("welcome");
        VBox roleField = field("ACCESS LEVEL / ROLE", roles);
        Label userLabel = new Label("EMAIL / USERNAME");
        userLabel.getStyleClass().add("field-label");
        VBox userField = new VBox(6, userLabel, username);
        CheckBox remember = new CheckBox("Remember this workstation");
        remember.setSelected(true);
        Button signIn = new Button("Login  ›");
        signIn.getStyleClass().add("sign-in");
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setDefaultButton(true);
        VBox form = new VBox(14, welcome, roleField, userField, field("PASSWORD", passwordBox), remember, signIn);
        form.setMaxWidth(380);
        form.setAlignment(Pos.CENTER_LEFT);
        headline.getStyleClass().add("headline");
        headline.setWrapText(true);
        headline.setMaxWidth(470);
        StackPane left = new StackPane(headline);
        left.setPadding(new Insets(60));
        left.getStyleClass().add("brand-panel");
        StackPane right = new StackPane(form);
        right.setPadding(new Insets(45));
        right.getStyleClass().add("form-panel");
        GridPane root = new GridPane();
        ColumnConstraints half = new ColumnConstraints(); half.setPercentWidth(50);
        ColumnConstraints otherHalf = new ColumnConstraints(); otherHalf.setPercentWidth(50);
        root.getColumnConstraints().addAll(half, otherHalf);
        RowConstraints row = new RowConstraints(); row.setPercentHeight(100);
        root.getRowConstraints().add(row);
        root.add(left, 0, 0); root.add(right, 1, 0);
        signIn.setOnAction(event -> {
            // Demo routing only: Admin opens immediately without checking credentials.
            if ("Admin".equals(roles.getValue())) {
                stage.setScene(AdminDashboard.create(stage, () -> start(stage)));
                stage.setTitle("GoviPOS — Admin Dashboard");
            } else if (!roleLogin && roles.getValue() != null) {
                roleLogin = true;
                headline.setText("Navigate to " + roles.getValue() + " Dashboard");
                welcome.setVisible(false); welcome.setManaged(false);
                roleField.setVisible(false); roleField.setManaged(false);
                remember.setVisible(false); remember.setManaged(false);
                userLabel.setText("USERNAME");
            }
        });
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("login.css").toExternalForm());
        stage.setTitle("GoviPOS"); stage.setMinWidth(800); stage.setMinHeight(520);
        stage.setScene(scene); stage.show();
        // Optional startup route supports reviewing the real JavaFX dashboard directly.
        if (!startupRouteConsumed && getParameters().getRaw().contains("--dashboard")) {
            stage.setScene(AdminDashboard.create(stage, () -> start(stage)));
        }
        startupRouteConsumed = true;
        String preview = System.getProperty("govipos.preview");
        if (preview != null) {
            javafx.application.Platform.runLater(() -> {
                javafx.scene.Parent previewRoot = stage.getScene().getRoot();
                previewRoot.applyCss(); previewRoot.layout();
                javafx.scene.image.WritableImage image = previewRoot.snapshot(null, null);
                java.awt.image.BufferedImage png = new java.awt.image.BufferedImage(
                        (int) image.getWidth(), (int) image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < png.getHeight(); y++) {
                    for (int x = 0; x < png.getWidth(); x++) {
                        png.setRGB(x, y, image.getPixelReader().getArgb(x, y));
                    }
                }
                try { javax.imageio.ImageIO.write(png, "png", new java.io.File(preview)); }
                catch (java.io.IOException exception) { throw new java.io.UncheckedIOException(exception); }
            });
        }
        scene.widthProperty().addListener((observable, before, width) -> {
            double padding = width.doubleValue() < 1000 ? 30 : 60;
            left.setPadding(new Insets(padding)); right.setPadding(new Insets(padding * .75));
        });
    }

    private VBox field(String text, javafx.scene.Node control) {
        Label label = new Label(text); label.getStyleClass().add("field-label");
        return new VBox(6, label, control);
    }
    public static void main(String[] args) { launch(args); }
}

