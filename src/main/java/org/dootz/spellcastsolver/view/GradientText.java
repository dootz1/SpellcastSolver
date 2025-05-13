package org.dootz.spellcastsolver.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GradientText extends Text {
    private final DoubleProperty offset = new SimpleDoubleProperty(0);

    private final Color COLOR_A = Color.web("#e2b7ff"); // Soft neon purple
    private final Color COLOR_B = Color.web("#f7ebff"); // Deeper violet

    public GradientText(String content) {
        super(content);
        bindGradient();
        startAnimation();
    }

    private void bindGradient() {
        offset.addListener((obs, oldVal, newVal) -> setFill(createRainbowGradient(newVal.doubleValue())));
        setFill(createRainbowGradient(0));
    }

    private void startAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(offset, 0)),
                new KeyFrame(Duration.seconds(4), new KeyValue(offset, 1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    private LinearGradient createRainbowGradient(double offset) {
        double shifted = offset % 1; // prevent overflow in case

        return new LinearGradient(
                0, 0, 1, 1,
                true, CycleMethod.REPEAT,
                new Stop(0 + shifted, COLOR_A),
                new Stop(0.5 + shifted, COLOR_B),
                new Stop(1.0 + shifted, COLOR_A)
        );
    }
}
