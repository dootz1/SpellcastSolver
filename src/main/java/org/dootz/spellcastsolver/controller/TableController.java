package org.dootz.spellcastsolver.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.dootz.spellcastsolver.model.BoardModel;
import org.dootz.spellcastsolver.model.DataModel;
import org.dootz.spellcastsolver.model.TableModel;
import org.dootz.spellcastsolver.solver.board.Tile;
import org.dootz.spellcastsolver.solver.board.Word;
import org.dootz.spellcastsolver.utils.Constants;

public class TableController {
    private DataModel model;
    @FXML
    private Label resultsSummary;
    @FXML
    private TextField wordQuery;
    @FXML
    private TableView<Word> resultsTable;
    @FXML
    private TableColumn<Word, String> wordsColumn;
    @FXML
    private TableColumn<Word, Number> pointsColumn;
    @FXML
    private TableColumn<Word, Number> gemsColumn;
    @FXML
    private TableColumn<Word, Number> swapsColumn;
    @FXML
    private Button clearSelection;

    public void initialize() {
        wordsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().toString()));
        gemsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getTotalGems()));
        pointsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getTotalPoints()));
        swapsColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getTotalSwaps()));

        wordQuery.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
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

        // Setup filtered and sorted view
        ObservableList<Word> allWords = tableModel.getWords();
        FilteredList<Word> filteredWords = new FilteredList<>(allWords, w -> true);

        tableModel.queryProperty().addListener((obs, old, filter) -> {
            String query = filter.toUpperCase();
            filteredWords.setPredicate(w -> query.isEmpty() || w.toString().toUpperCase().contains(query));
        });

        SortedList<Word> sortedWords = new SortedList<>(filteredWords);
        sortedWords.comparatorProperty().bind(resultsTable.comparatorProperty());
        resultsTable.setItems(sortedWords);

        resultsSummary.textProperty().bind(Bindings.createStringBinding(() -> {
            int count = tableModel.getResultWordCount();
            long timeMs = tableModel.getResultTimeMs();
            String result = "";
            if (count > 0) result += count + " Results";
            if (timeMs > 0) result += String.format(" (%.4f seconds)", timeMs / 1000.0);
            return result;
        }, tableModel.resultWordCountProperty(), tableModel.resultTimeMsProperty()));
    }

    private void updateBoardSelection(BoardModel board, Word newWord) {
        board.clearPath();
        board.clearSolvedSelected();
        board.clearSolvedWildcard();

        if (newWord == null) {
            resultsTable.getSelectionModel().clearSelection();
            board.setSolvedVisible(false);
            board.setSelectedWord("");
            board.setSelectedWordPoints(0);
            return;
        }

        resultsTable.getSelectionModel().select(newWord);
        board.setSolvedVisible(true);
        board.setSelectedWord(newWord.toString());
        board.setSelectedWordPoints(newWord.getTotalPoints());

        for (Tile tile : newWord.getTiles()) {
            int index = tile.getRow() * Constants.BOARD_SIZE + tile.getColumn();
            board.addPathNode(index);
            var tileModel = board.getSolvedTileModelByIndex(index);
            tileModel.setSelected(true);
            tileModel.setWildcard(tile.isWildcard());
            tileModel.setWildcardLetter(tile.isWildcard() ? String.valueOf(tile.getWildcardLetter()) : "\0");
        }
    }

    @FXML
    private void handleClearSelection(ActionEvent event) {
        model.getTableModel().setSelectedWord(null);
    }
}
