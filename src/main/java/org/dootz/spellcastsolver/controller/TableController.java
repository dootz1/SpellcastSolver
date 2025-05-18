package org.dootz.spellcastsolver.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.CustomTextField;
import org.dootz.spellcastsolver.game.board.Tile;
import org.dootz.spellcastsolver.model.BoardModel;
import org.dootz.spellcastsolver.model.DataModel;
import org.dootz.spellcastsolver.model.SettingsModel;
import org.dootz.spellcastsolver.model.TableModel;
import org.dootz.spellcastsolver.solver.Evaluator;
import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.view.GradientText;

public class TableController {
    private DataModel model;
    @FXML
    private Label resultsSummary;
    @FXML
    private CustomTextField wordQuery;
    @FXML
    private TableView<Evaluator.EvaluatedMove> resultsTable;
    @FXML
    private TableColumn<Evaluator.EvaluatedMove, String> wordsColumn;
    @FXML
    private TableColumn<Evaluator.EvaluatedMove, Number> pointsColumn;
    @FXML
    private TableColumn<Evaluator.EvaluatedMove, Number> gemsColumn;
    @FXML
    private TableColumn<Evaluator.EvaluatedMove, Number> swapsColumn;
    @FXML
    private TableColumn<Evaluator.EvaluatedMove, Number> evaluationColumn;
    @FXML
    private Button clearSelection;
    @FXML
    private Button playSelection;
    @FXML
    private Label shuffleRecommended;

    public void initialize() {
        wordsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().toString()));
        gemsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getMove().gems()));
        pointsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getMove().points()));
        swapsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getMove().swaps()));
        evaluationColumn.setCellValueFactory(cellData -> new ReadOnlyDoubleWrapper(cellData.getValue().getEvaluationScore()));
        evaluationColumn.setCellFactory(column -> new TableCell<>() {
            private final GradientText gradientText = new GradientText("");
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    gradientText.setText(String.format("%.1f", item.doubleValue()));
                    gradientText.getStyleClass().add("glowing-text");
                    setGraphic(gradientText);
                }
            }
        });

        wordQuery.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        evaluationColumn.setSortType(TableColumn.SortType.DESCENDING);
        resultsTable.getSortOrder().add(evaluationColumn);
        resultsTable.sort(); // sort by evaluation by default
    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        TableModel tableModel = model.getTableModel();
        BoardModel boardModel = model.getBoardModel();

        // Bind query and selection
        tableModel.queryProperty().bindBidirectional(wordQuery.textProperty());
        resultsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) ->
                tableModel.setSelectedWord(selected)
        );

        // React to selected word changes
        tableModel.selectedWordProperty().addListener((obs, oldWord, newWord) -> updateBoardSelection(boardModel, newWord));

        // Setup search bar
        ObservableList<Evaluator.EvaluatedMove> allMoves = tableModel.getWords();
        FilteredList<Evaluator.EvaluatedMove> filteredMoves = new FilteredList<>(allMoves, w -> true);

        tableModel.queryProperty().addListener((obs, old, filter) -> {
            String query = filter.toUpperCase();
            filteredMoves.setPredicate(m -> query.isEmpty() || m.toString().toUpperCase().contains(query));
        });

        SortedList<Evaluator.EvaluatedMove> sortedMoves = new SortedList<>(filteredMoves);
        sortedMoves.comparatorProperty().bind(resultsTable.comparatorProperty());
        resultsTable.setItems(sortedMoves);

        resultsSummary.textProperty().bind(Bindings.createStringBinding(() -> {
            int count = tableModel.getResultWordCount();
            long timeMs = tableModel.getResultTimeMs();
            String result = "";
            if (count > 0) result += count + " Results";
            if (timeMs > 0) result += String.format(" (%.4f seconds)", timeMs / 1000.0);
            return result;
        }, tableModel.resultWordCountProperty(), tableModel.resultTimeMsProperty()));

        clearSelection.disableProperty().bind(tableModel.selectedWordProperty().isNull());
        playSelection.disableProperty().bind(tableModel.selectedWordProperty().isNull());
        shuffleRecommended.visibleProperty().bind(tableModel.shuffleRecommendedVisibleProperty());
    }

    private void updateBoardSelection(BoardModel board, Evaluator.EvaluatedMove newMove) {
        board.clearPath();
        clearPreviousSolveData(board);

        if (newMove == null) {
            resultsTable.getSelectionModel().clearSelection();
            board.setSolvedVisible(false);
            board.setCurrentWord("");
            board.setCurrentWordScore(0);
            board.setIsLongWordBonusApplied(false);
            board.setCurrentWordGemsEarned(0);
            return;
        }

        resultsTable.getSelectionModel().select(newMove);
        board.setSolvedVisible(true);
        board.setCurrentWord(newMove.toString());
        board.setCurrentWordScore(newMove.getMove().points());
        board.setIsLongWordBonusApplied(newMove.getMove().isLong());
        board.setCurrentWordGemsEarned(newMove.getMove().gems());

        for (Tile tile : newMove.getMove().tiles()) {
            int index = tile.getRow() * Constants.BOARD_SIZE + tile.getColumn();
            board.addPathNode(index);
            var tileModel = board.getSolvedTileModelByIndex(index);
            tileModel.setSelected(true);
            tileModel.setWildcard(tile.isWildcard());
            tileModel.setWildcardLetter(tile.isWildcard() ? String.valueOf(tile.getWildcardLetter()) : "\0");
        }
    }

    private void clearPreviousSolveData(BoardModel board) {
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            board.getSolvedTileModelByIndex(i).setSelected(false);
            board.getSolvedTileModelByIndex(i).setWildcard(false);
            board.getSolvedTileModelByIndex(i).setWildcardLetter("\0");
        }
    }

    @FXML
    private void handleClearSelection(ActionEvent event) {
        model.getTableModel().setSelectedWord(null);
    }

    @FXML
    private void handlePlaySelection(ActionEvent event) {
        BoardModel board = model.getBoardModel();
        SettingsModel settings = model.getSettingsModel();
        TableModel table = model.getTableModel();
        Evaluator.EvaluatedMove move = table.getSelectedWord();

        if (move == null) return;

        for (Tile tile : move.getMove().tiles()) {
            int index = tile.getRow() * Constants.BOARD_SIZE + tile.getColumn();
            var tileModel = board.getInputTileModelByIndex(index);
            tileModel.getModifiers().clear();
            tileModel.setLetter("");
        }

        settings.setGameRound(Math.min(5, settings.getGameRound() + 1));
        settings.setPlayerGems(Math.min(10, settings.getPlayerGems() + move.getMove().gemProfit()));

        table.setSelectedWord(null);
        table.getWords().clear();
        table.setShuffleRecommendedVisible(false);
    }
}
