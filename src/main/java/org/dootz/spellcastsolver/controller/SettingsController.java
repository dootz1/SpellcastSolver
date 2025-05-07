package org.dootz.spellcastsolver.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.ToggleSwitch;
import org.dootz.spellcastsolver.game.board.Move;
import org.dootz.spellcastsolver.model.*;
import org.dootz.spellcastsolver.solver.Evaluator;
import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.solver.multithreading.SolverTask;
import org.dootz.spellcastsolver.utils.Constants;

import java.util.List;
import java.util.function.UnaryOperator;

public class SettingsController {
    private DataModel model;
    private SolverTask solverTask;
    private Dictionary dictionary;
    private PauseTransition pause;
    @FXML
    private Button solveButton;
    @FXML
    private ProgressBar solveProgress;
    @FXML
    private ToggleSwitch multithreadingToggle;
    @FXML
    private Spinner<Integer> playerGems;
    @FXML
    private Spinner<Integer> gameRound;
    @FXML
    private Label invalidBoardWarning;

    private ObjectProperty<Integer> gemObject; // need to do this for some reason related to gc
    private ObjectProperty<Integer> roundObject;

    public void initialize() {
//        SliderUtils.applySliderProgressGradient(maxSwapsSlider, "-swaps-track-color", Color.rgb(155, 7, 255), Color.rgb(80, 80, 80));
        playerGems.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        gameRound.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5));
        setIntegerRangeFormatter(playerGems, 1, 10);
        setIntegerRangeFormatter(gameRound, 1, 5);
        pause = new PauseTransition(Duration.seconds(5));
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    private void setIntegerRangeFormatter(Spinner<Integer> spinner, int min, int max) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // allow only digits
                return change;
            }
            return null;
        };

        TextFormatter<Integer> formatter = new TextFormatter<>(
                new IntegerStringConverter(), 1, filter
        );

        spinner.getEditor().setTextFormatter(formatter);

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();
        int lastValidValue = spinner.getValue();
        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String text = spinner.getEditor().getText();
                if (text.isEmpty()) {
                    valueFactory.setValue(lastValidValue);
                    spinner.getEditor().setText(String.valueOf(lastValidValue));
                }
            }
        });
    }
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        SettingsModel settings = model.getSettingsModel();

//        Timeline timeline = new Timeline(
//                new KeyFrame(Duration.seconds(1), event -> {
//                    System.out.println(settings);
//                })
//        );
//        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat forever
//        timeline.play();

        gemObject = settings.playerGemsProperty().asObject();
        roundObject = settings.gameRoundProperty().asObject();
        playerGems.getValueFactory().valueProperty().bindBidirectional(gemObject);
        gameRound.getValueFactory().valueProperty().bindBidirectional(roundObject);
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
        int gems = settingsModel.getPlayerGems();
        int round = settingsModel.getGameRound();

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

        saveInputTilesIntoSolvedTiles(boardModel);
        tableModel.setSelectedWord(null);

        Solver solver = new Solver(dictionary, boardModel.toDomainObject());
        solverTask = new SolverTask(solver, gems, settingsModel.isMultithreadingEnabled());

        settingsModel.progressProperty().bind(solverTask.progressProperty());

        solverTask.setOnRunning(e -> {
            settingsModel.setIsSolving(true);
            settingsModel.setTimeStartedSolving(System.currentTimeMillis());
        });

        solverTask.setOnFailed(e -> resetState(settingsModel));
        solverTask.setOnCancelled(e -> resetState(settingsModel));

        solverTask.setOnSucceeded(e -> {
            resetState(settingsModel);
            List<Move> moves = solver.getMovesAsList();
            Evaluator evaluator = new Evaluator();
            List<Evaluator.EvaluatedMove> evaluatedMoves = evaluator.evaluateMoves(moves, round, gems);
            tableModel.setResultTimeMs(System.currentTimeMillis() - settingsModel.getTimeStartedSolving()); // algorithm ends here
            tableModel.setResultWordCount(evaluatedMoves.size());

            tableModel.getWords().setAll(evaluatedMoves);
            solver.clearResult();
        });

        Thread solverThread = new Thread(solverTask);
        solverThread.setDaemon(true);
        solverThread.start();
    }

    private void saveInputTilesIntoSolvedTiles(BoardModel board) {
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            TileModel input = board.getInputTileModelByIndex(i);
            TileModel solved = board.getSolvedTileModelByIndex(i);

            solved.setLetter(input.getLetter());
            solved.getModifiers().clear();
            solved.getModifiers().addAll(input.getModifiers());
            solved.setWildcard(input.isWildcard());
            solved.setWildcardLetter(input.getWildcardLetter());
            solved.setSelected(input.isSelected());
            solved.setRow(input.getRow());
            solved.setColumn(input.getColumn());
        }
    }

    private void resetState(SettingsModel settings) {
        settings.setIsSolving(false);
        settings.progressProperty().unbind();
        settings.setProgress(0);
    }
}