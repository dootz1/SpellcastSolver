package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dootz.spellcastsolver.solver.Evaluator;

public class TableModel {
    private final IntegerProperty resultWordCount;
    private final LongProperty resultTimeMs;
    private final StringProperty query;
    private final ObservableList<Evaluator.EvaluatedMove> moves;
    private final ObjectProperty<Evaluator.EvaluatedMove> selectedWord;
    private final BooleanProperty shuffleRecommendedVisible;

    public TableModel() {
        this.resultWordCount = new SimpleIntegerProperty(0);
        this.resultTimeMs = new SimpleLongProperty(0);
        this.query = new SimpleStringProperty();
        this.moves = FXCollections.observableArrayList();
        this.selectedWord = new SimpleObjectProperty<>();
        this.shuffleRecommendedVisible = new SimpleBooleanProperty(false);
    }

    public int getResultWordCount() {
        return resultWordCount.get();
    }

    public IntegerProperty resultWordCountProperty() {
        return resultWordCount;
    }

    public void setResultWordCount(int resultWordCount) {
        this.resultWordCount.set(resultWordCount);
    }

    public long getResultTimeMs() {
        return resultTimeMs.get();
    }

    public LongProperty resultTimeMsProperty() {
        return resultTimeMs;
    }

    public void setResultTimeMs(long resultTimeMs) {
        this.resultTimeMs.set(resultTimeMs);
    }

    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public void setQuery(String query) {
        this.query.set(query);
    }

    public ObservableList<Evaluator.EvaluatedMove> getWords() {
        return moves;
    }

    public Evaluator.EvaluatedMove getSelectedWord() {
        return selectedWord.get();
    }

    public ObjectProperty<Evaluator.EvaluatedMove> selectedWordProperty() {
        return selectedWord;
    }

    public void setSelectedWord(Evaluator.EvaluatedMove selectedMove) {
        this.selectedWord.set(selectedMove);
    }

    public boolean isShuffleRecommendedVisible() {
        return shuffleRecommendedVisible.get();
    }

    public BooleanProperty shuffleRecommendedVisibleProperty() {
        return shuffleRecommendedVisible;
    }

    public void setShuffleRecommendedVisible(boolean shuffleRecommendedVisible) {
        this.shuffleRecommendedVisible.set(shuffleRecommendedVisible);
    }

    @Override
    public String toString() {
        return "TableModel{" +
                "query=" + query.get() +
                ", words=" + moves +
                ", selectedWord=" + selectedWord.get() +
                '}';
    }
}
