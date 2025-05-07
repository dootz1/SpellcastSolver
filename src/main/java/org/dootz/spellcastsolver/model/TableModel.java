package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dootz.spellcastsolver.solver.board.Word;

public class TableModel {
    private final IntegerProperty resultWordCount;
    private final LongProperty resultTimeMs;
    private final StringProperty query;
    private final ObservableList<Word> words;
    private final ObjectProperty<Word> selectedWord;

    public TableModel() {
        this.resultWordCount = new SimpleIntegerProperty(0);
        this.resultTimeMs = new SimpleLongProperty(0);
        this.query = new SimpleStringProperty();
        this.words = FXCollections.observableArrayList();
        this.selectedWord = new SimpleObjectProperty<>(new Word());
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

    public ObservableList<Word> getWords() {
        return words;
    }

    public Word getSelectedWord() {
        return selectedWord.get();
    }

    public ObjectProperty<Word> selectedWordProperty() {
        return selectedWord;
    }

    public void setSelectedWord(Word selectedWord) {
        this.selectedWord.set(selectedWord);
    }

    @Override
    public String toString() {
        return "TableModel{" +
                "query=" + query.get() +
                ", words=" + words +
                ", selectedWord=" + selectedWord.get() +
                '}';
    }
}
