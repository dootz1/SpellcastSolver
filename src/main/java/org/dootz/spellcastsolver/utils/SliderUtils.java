package org.dootz.spellcastsolver.utils;

import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class SliderUtils {
    private SliderUtils() {}
    public static void applySliderProgressGradient(Slider slider, String trackColorProperty, Color progressColor, Color unfilledColor) {
        slider.valueProperty().addListener((observable, oldVal, newVal) -> {
            double percentage = 100.0 * (newVal.doubleValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
            String style = String.format("%s: linear-gradient(to right, %s %f%%, %s %f%%)",
                    trackColorProperty,
                    colorToRgbCss(progressColor), percentage,
                    colorToRgbCss(unfilledColor), percentage);
            slider.setStyle(style);
        });
    }

    private static String colorToRgbCss(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }
}
