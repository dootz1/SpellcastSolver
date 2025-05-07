package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.utils.Constants;

public class BoardModel {
    private final StringProperty currentWord;
    private final IntegerProperty currentWordScore;
    private final BooleanProperty isLongWordBonusApplied;
    private final IntegerProperty currentWordGemsEarned;
    private final TileModel[][] inputTiles;
    private final TileModel[][] solvedTiles;
    private final ObservableList<Integer> path;
    private final BooleanProperty solvedVisible;

    public BoardModel() {
        this("", 0, false, 0, createTileModels(), createTileModels(), FXCollections.observableArrayList(), false);
    }
    public BoardModel(String currentWord, int currentWordScore, boolean isLongWordBonusApplied, int currentWordGemsEarned, TileModel[][] inputTiles, TileModel[][] solvedTiles, ObservableList<Integer> path, boolean solvedVisible) {
        this.currentWord = new SimpleStringProperty(currentWord);
        this.currentWordScore = new SimpleIntegerProperty(currentWordScore);
        this.isLongWordBonusApplied = new SimpleBooleanProperty(isLongWordBonusApplied);
        this.currentWordGemsEarned = new SimpleIntegerProperty(currentWordGemsEarned);
        this.inputTiles = inputTiles;
        this.solvedTiles = solvedTiles;
        this.path = path;
        this.solvedVisible = new SimpleBooleanProperty(solvedVisible);
    }

    private static TileModel[][] createTileModels() {
        TileModel[][] tiles = new TileModel[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tiles[i][j] = new TileModel(i, j);
            }
        }
        return tiles;
    }

    public String getCurrentWord() {
        return currentWord.get();
    }

    public StringProperty currentWordProperty() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord.set(currentWord);
    }

    public int getCurrentWordScore() {
        return currentWordScore.get();
    }

    public IntegerProperty currentWordScoreProperty() {
        return currentWordScore;
    }

    public void setCurrentWordScore(int currentWordScore) {
        this.currentWordScore.set(currentWordScore);
    }

    public boolean isLongWordBonusApplied() {
        return isLongWordBonusApplied.get();
    }

    public BooleanProperty isLongWordBonusAppliedProperty() {
        return isLongWordBonusApplied;
    }

    public void setIsLongWordBonusApplied(boolean isLongWordBonusApplied) {
        this.isLongWordBonusApplied.set(isLongWordBonusApplied);
    }

    public int getCurrentWordGemsEarned() {
        return currentWordGemsEarned.get();
    }

    public IntegerProperty currentWordGemsEarnedProperty() {
        return currentWordGemsEarned;
    }

    public void setCurrentWordGemsEarned(int currentWordGemsEarned) {
        this.currentWordGemsEarned.set(currentWordGemsEarned);
    }

    public TileModel[][] getInputTiles() {
        return inputTiles;
    }

    public TileModel[][] getSolvedTiles() {
        return solvedTiles;
    }

    public ObservableList<Integer> getPath() {
        return path;
    }
    public void addPathNode(int index) {
        path.add(index);
    }
    public void clearPath() {
        path.clear();
    }

    public boolean isSolvedVisible() {
        return solvedVisible.get();
    }

    public BooleanProperty solvedVisibleProperty() {
        return solvedVisible;
    }

    public void setSolvedVisible(boolean solvedVisible) {
        this.solvedVisible.set(solvedVisible);
    }

    public Board toDomainObject() {
        Board board = new Board();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                board.setTile(i, j, inputTiles[i][j].toDomainObject());
            }
        }
        return board;
    }

    public boolean isFilledIn() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (inputTiles[i][j].getLetter().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public TileModel getTileModelByIndex(TileModel[][] tiles, int index) {
        return tiles[index / 5][index % 5];
    }
    public TileModel getInputTileModelByIndex(int index) {
        return getTileModelByIndex(inputTiles, index);
    }
    public TileModel getSolvedTileModelByIndex(int index) {
        return getTileModelByIndex(solvedTiles, index);
    }

}
