package org.dootz.spellcastsolver.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class TopBarController {
    @FXML
    private Circle closeApp;

    @FXML
    private Circle minimizeApp;

    @FXML
    public void initialize() {
        closeApp.setOnMouseClicked(e -> Platform.exit());
        minimizeApp.setOnMouseClicked(e -> {
            Stage stage = (Stage) minimizeApp.getScene().getWindow();
            stage.setIconified(true);
        });
    }
}

