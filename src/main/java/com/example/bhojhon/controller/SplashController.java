package com.example.bhojhon.controller;

import com.example.bhojhon.util.BaseController;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class SplashController extends BaseController {

    @FXML
    private VBox mainCard;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle blob1;
    @FXML
    private Circle blob2;

    @Override
    public void initialize() {
        // 1. Initial State
        mainCard.setOpacity(0);
        mainCard.setScaleX(0.9);
        mainCard.setScaleY(0.9);
        mainCard.setTranslateY(30);

        // 2. Card Animation (Fade in & Scale up)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainCard);
        fadeIn.setToValue(1);

        TranslateTransition moveUp = new TranslateTransition(Duration.millis(800), mainCard);
        moveUp.setToY(0);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(800), mainCard);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        ParallelTransition cardEntrance = new ParallelTransition(fadeIn, moveUp, scaleUp);
        cardEntrance.setDelay(Duration.millis(200));

        // 3. Blob Floating Animations
        animateBlob(blob1, 20, 15, 4);
        animateBlob(blob2, -15, 20, 5);

        // 4. Progress Bar Animation
        Timeline progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1)));

        // 5. Sequence and Navigation
        cardEntrance.play();
        progressTimeline.play();

        PauseTransition navigationDelay = new PauseTransition(Duration.seconds(3.5));
        navigationDelay.setOnFinished(event -> navigateTo("/com/example/bhojhon/main-menu-view.fxml"));
        navigationDelay.play();
    }

    private void animateBlob(Node blob, double x, double y, double duration) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(duration), blob);
        tt.setByX(x);
        tt.setByY(y);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }
}
