package com.govipos;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Exports snapshots from actual JavaFX controls, not a separate HTML recreation. */
final class ScreenPreviews {
    private ScreenPreviews() {}
    static void export(Stage stage, Runnable hub) {
        Path directory = Path.of("target", "previews");
        try {
            Files.createDirectories(directory);
            // Use a taller capture for the branch form so all three sections appear in one image.
            capture(stage, AdminScreens.createBranch(stage, hub), directory.resolve("create-branch.png"), 1280, 1240);
            capture(stage, AdminScreens.removeBranch(stage, hub), directory.resolve("remove-branch.png"), 1280, 720);
            capture(stage, AdminScreens.assignBranch(stage, hub), directory.resolve("assign-branch.png"), 1280, 720);
            capture(stage, AdminScreens.createAccount(stage, hub), directory.resolve("create-account.png"), 1280, 720);
            hub.run();
        } catch (IOException exception) { throw new UncheckedIOException(exception); }
    }
    private static void capture(Stage stage, Scene scene, Path path, int width, int height) throws IOException {
        stage.setScene(scene);
        var root = scene.getRoot(); root.applyCss(); root.resize(width, height); root.layout();
        // ScrollPane skins need a second layout after their viewport acquires its width.
        root.applyCss(); root.layout();
        var image = root.snapshot(null, null);
        // Copy JavaFX pixels into a PNG without adding a javafx-swing dependency.
        BufferedImage png = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < png.getHeight(); y++) for (int x = 0; x < png.getWidth(); x++) {
            png.setRGB(x, y, image.getPixelReader().getArgb(x, y));
        }
        ImageIO.write(png, "png", path.toFile());
        System.out.println("Preview: " + path.toAbsolutePath());
    }
}
