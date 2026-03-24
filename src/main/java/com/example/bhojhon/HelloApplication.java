package com.example.bhojhon;

import com.example.bhojhon.util.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.text.Font;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the new Plus Jakarta Sans font weights
        String[] fontFiles = {
            "PlusJakartaSans-Regular.otf",
            "PlusJakartaSans-Medium.otf",
            "PlusJakartaSans-SemiBold.otf",
            "PlusJakartaSans-Bold.otf",
            "PlusJakartaSans-ExtraBold.otf"
        };
        for (String fontFile : fontFiles) {
            Font.loadFont(getClass().getResourceAsStream("/com/example/bhojhon/plus jakarta sans font/" + fontFile), 12);
        }

        // Bootstrap the app using the central NavigationManager so every screen
        // is styled and navigation works consistently.
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.setPrimaryStage(stage);
        navigationManager.navigateTo(
                "/com/example/bhojhon/splash-view.fxml",
                "Upload Ticket - Train Food Pre-Order");
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch();
    }
}