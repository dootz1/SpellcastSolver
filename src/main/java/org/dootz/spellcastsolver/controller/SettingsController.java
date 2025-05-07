package org.dootz.spellcastsolver.controller;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import org.dootz.spellcastsolver.model.BoardModel;
import org.dootz.spellcastsolver.model.DataModel;
import org.dootz.spellcastsolver.model.SettingsModel;
import org.dootz.spellcastsolver.model.TableModel;
import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.solver.board.Word;
import org.dootz.spellcastsolver.solver.multithreading.SolverTask;
import org.dootz.spellcastsolver.utils.SliderUtils;

import java.util.List;

public class SettingsController {
    private DataModel model;
    private SolverTask solverTask;
    private Solver solver;
    private PauseTransition pause;
    @FXML
    private Button solveButton;
    @FXML
    private ProgressBar solveProgress;
    @FXML
    private ToggleSwitch multithreadingToggle;
    @FXML
    private Label maxSwapsLabel;
    @FXML
    private Slider maxSwapsSlider;
    @FXML
    private Label invalidBoardWarning;

    public void initialize() {
        SliderUtils.applySliderProgressGradient(maxSwapsSlider, "-swaps-track-color", Color.rgb(155, 7, 255), Color.rgb(80, 80, 80));
        pause = new PauseTransition(Duration.seconds(5));
    }

    public void initSolver(Solver solver) {
        this.solver = solver;
    }
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        SettingsModel settings = model.getSettingsModel();

        maxSwapsSlider.valueProperty().bindBidirectional(settings.maxSwapsProperty());
        maxSwapsLabel.textProperty().bind(settings.maxSwapsProperty().asString());
        multithreadingToggle.selectedProperty().bindBidirectional(settings.multithreadingEnabledProperty());
        solveProgress.progressProperty().bind(settings.progressProperty());

        solveButton.textProperty().bind(
                Bindings.createStringBinding(
                        () -> settings.isSolving() ? "CANCEL" : "SOLVE",
                        settings.isSolvingProperty()
                )
        );
        invalidBoardWarning.visibleProperty().bind(settings.showInvalidBoardWarningProperty());
        pause.setOnFinished(e -> settings.setShowInvalidBoardWarning(false));
    }

    public void handleSolve(ActionEvent event) {
        SettingsModel settingsModel = model.getSettingsModel();
        BoardModel boardModel = model.getBoardModel();
        TableModel tableModel = model.getTableModel();

        if (settingsModel.isSolving()) {
            solverTask.cancel();
            return;
        }

        if (!boardModel.isFilledIn()) {
            settingsModel.setShowInvalidBoardWarning(true);
            pause.stop();
            pause.playFromStart();
            return;
        }

        boardModel.saveInputTilesIntoSolvedTiles();
        solver.setBoard(boardModel.toDomainObject());
        tableModel.setSelectedWord(null);

        solverTask = new SolverTask(solver, settingsModel.getMaxSwaps(), settingsModel.isMultithreadingEnabled());
        settingsModel.progressProperty().bind(solverTask.progressProperty());

        solverTask.setOnRunning(e -> {
            settingsModel.setIsSolving(true);
            settingsModel.setTimeStartedSolving(System.currentTimeMillis());
        });

        solverTask.setOnFailed(e -> resetState(settingsModel));
        solverTask.setOnCancelled(e -> resetState(settingsModel));

        solverTask.setOnSucceeded(e -> {
            resetState(settingsModel);
            List<Word> results = solver.getResultAsList();
            tableModel.getWords().setAll(results);
            tableModel.setResultTimeMs(System.currentTimeMillis() - settingsModel.getTimeStartedSolving());
            tableModel.setResultWordCount(results.size());

            solver.clearResult();
        });

        Thread solverThread = new Thread(solverTask);
        solverThread.setDaemon(true);
        solverThread.start();
    }

    private void resetState(SettingsModel settings) {
        settings.setIsSolving(false);
        settings.progressProperty().unbind();
        settings.setProgress(0);
    }
}