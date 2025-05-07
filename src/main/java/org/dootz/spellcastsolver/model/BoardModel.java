package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dootz.spellcastsolver.solver.board.Board;
import org.dootz.spellcastsolver.utils.Constants;

import java.util.Random;

public class BoardModel {
    private final StringProperty selectedWord;
    private final IntegerProperty selectedWordPoints;
    private final TileModel[][] inputTiles;
    private final TileModel[][] solvedTiles;
    private final ObservableList<Integer> path;
    private final BooleanProperty solvedVisible;
    private final BooleanProperty inputTilesDisabled;

    public BoardModel() {
        this("", 0, createTileModels(), createTileModels(), FXCollections.observableArrayList(), false, false);
    }
    public BoardModel(String selectedWord, int selectedWordPoints, TileModel[][] inputTiles, TileModel[][] solvedTiles, ObservableList<Integer> path, boolean solvedVisible, boolean inputTilesDisabled) {
        this.selectedWord = new SimpleStringProperty(selectedWord);
        this.selectedWordPoints = new SimpleIntegerProperty(selectedWordPoints);
        this.inputTiles = inputTiles;
        this.solvedTiles = solvedTiles;
        this.path = path;
        this.solvedVisible = new SimpleBooleanProperty(solvedVisible);
        this.inputTilesDisabled = new SimpleBooleanProperty(inputTilesDisabled);
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

    public String getSelectedWord() {
        return selectedWord.get();
    }

    public StringProperty selectedWordProperty() {
        return selectedWord;
    }

    public void setSelectedWord(String selectedWord) {
        this.selectedWord.set(selectedWord);
    }

    public int getSelectedWordPoints() {
        return selectedWordPoints.get();
    }

    public IntegerProperty selectedWordPointsProperty() {
        return selectedWordPoints;
    }

    public void setSelectedWordPoints(int selectedWordPoints) {
        this.selectedWordPoints.set(selectedWordPoints);
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

    public void clearSolvedSelected() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                solvedTiles[i][j].setSelected(false);
            }
        }
    }

    public void clearSolvedWildcard() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                solvedTiles[i][j].setWildcard(false);
                solvedTiles[i][j].setWildcardLetter("\0");
            }
        }
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

    public boolean getInputTilesDisabled() {
        return inputTilesDisabled.get();
    }

    public BooleanProperty inputTilesDisabledProperty() {
        return inputTilesDisabled;
    }

    public void setInputTilesDisabled(boolean disabled) {
        this.inputTilesDisabled.set(disabled);
    }

    public void saveInputTilesIntoSolvedTiles() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                TileModel input = inputTiles[i][j];
                TileModel solved = solvedTiles[i][j];

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
