package org.dootz.spellcastsolver.controller;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Tile;
import org.dootz.spellcastsolver.model.BoardModel;
import org.dootz.spellcastsolver.model.ContextMenuModel;
import org.dootz.spellcastsolver.model.DataModel;
import org.dootz.spellcastsolver.model.TileModel;
import org.dootz.spellcastsolver.utils.BoardUtils;
import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.utils.TileModifier;
import org.dootz.spellcastsolver.utils.TileUtils;

import java.util.Set;

public class BoardController {
    private static final int TILE_MODIFIER_BACKGROUND_INDEX = 0;
    private static final int TILE_INPUT_FIELD_INDEX = 1;
    private static final int TILE_POINTS_LABEL_INDEX = 2;
    private static final int LETTER_MODIFIER_CONTAINER_INDEX = 3;
    private static final int WORD_MODIFIER_CONTAINER_INDEX = 4;
    private static final int GEM_POLYGON_INDEX = 5;
    private static final int FROZEN_RECTANGLE_INDEX = 6;
    private DataModel model;
    @FXML
    private Label currentWordLabel;
    @FXML
    private Label longWordBonusStatusLabel;
    @FXML
    private Label gemsEarnedLabel;
    @FXML
    private Pane canvas;
    @FXML
    private GridPane inputGrid;
    @FXML
    private GridPane solvedGrid;
    @FXML
    private Label modifierInstructions;

    public void initialize() {
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            setupTile(getTileFromGrid(inputGrid, i));
            setupTile(getTileFromGrid(solvedGrid, i));
        }

        Tooltip tooltip = new Tooltip(
                """
                Modifier Hotkeys:
                1    – 💎 Gem
                !    – ❄️ Frozen
                2/3  – 🔠 Double / Triple Letter
                @/#  – 🔤 Double / Triple Word
                0    – ♻️ Clear All Modifiers
                """
        );
        tooltip.setStyle("-fx-font-family: 'monospaced'; -fx-padding: 5;");
        Tooltip.install(modifierInstructions, tooltip);
    }

    private StackPane getTileFromGrid(GridPane grid, int index) {
        return (StackPane) grid.getChildren().get(index);
    }

    private void setupTile(StackPane tileContainer) {
        TextField tileInput = (TextField) tileContainer.getChildren().get(TILE_INPUT_FIELD_INDEX);

        tileInput.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getText();
            if (text.isEmpty()) return change; // Allow deletion

            String upper = text.toUpperCase();
            if (upper.matches("[A-Z]")) {
                change.setText(upper);
                change.setRange(0, change.getControlText().length()); // Replace all text
                return change;
            }

            return null; // Reject invalid input
        }));

        tileInput.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) return;

            GridPane parentGrid = (GridPane) tileContainer.getParent();
            int currentIndex = parentGrid.getChildren().indexOf(tileContainer);
            int nextIndex = (currentIndex + 1) % Constants.BOARD_TILES;

            StackPane nextContainer = (StackPane) parentGrid.getChildren().get(nextIndex);
            TextField nextInput = (TextField) nextContainer.getChildren().get(TILE_INPUT_FIELD_INDEX);
            nextInput.requestFocus();
        });

    }

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;
        BoardModel boardModel = model.getBoardModel();
        ContextMenuModel contextMenuModel = model.getContextMenuModel();

        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            StackPane inputTile = (StackPane) inputGrid.getChildren().get(i);
            StackPane solvedTile = (StackPane) solvedGrid.getChildren().get(i);

            bindTileToView(boardModel.getInputTileModelByIndex(i), inputTile, contextMenuModel);
            bindTileToView(boardModel.getSolvedTileModelByIndex(i), solvedTile, contextMenuModel);
        }

        bindPathToCanvas(boardModel.getPath());
        inputGrid.visibleProperty().bind(boardModel.solvedVisibleProperty().not());
        solvedGrid.visibleProperty().bind(boardModel.solvedVisibleProperty());
        canvas.visibleProperty().bind(boardModel.solvedVisibleProperty());
        currentWordLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            String word = boardModel.getCurrentWord();
            int points = boardModel.getCurrentWordScore();
            return points > 0 ? word + " +" + points : word;
        }, boardModel.currentWordProperty(), boardModel.currentWordScoreProperty()));
        longWordBonusStatusLabel.visibleProperty().bind(boardModel.isLongWordBonusAppliedProperty());
        gemsEarnedLabel.textProperty().bind(Bindings.concat("x", boardModel.currentWordGemsEarnedProperty().asString()));
    }

    @FXML
    private void handleClearTileLetters(ActionEvent event) {
        BoardModel boardModel = model.getBoardModel();
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            boardModel.getInputTileModelByIndex(i).setLetter("");
        }
    }

    @FXML
    private void handleClearTileModifiers(ActionEvent event) {
        BoardModel boardModel = model.getBoardModel();
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            boardModel.getInputTileModelByIndex(i).getModifiers().clear();
        }
    }

    @FXML
    private void handleRandomizeBoard(ActionEvent event) {
        BoardModel boardModel = model.getBoardModel();
        Board randomBoard = BoardUtils.randomBoard(true);
        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            TileModel tileModel = boardModel.getInputTileModelByIndex(i);
            Tile tile = randomBoard.getTile(BoardUtils.indexToRow(i), BoardUtils.indexToColumn(i));

            tileModel.setLetter(String.valueOf(tile.getLetter()));
            tileModel.getModifiers().clear();
            tileModel.getModifiers().addAll(tile.getModifiers());
        }
    }

    private void bindPathToCanvas(ObservableList<Integer> path) {
        path.addListener((ListChangeListener<Integer>) change -> {
            canvas.getChildren().clear();

            for (int i = 0; i < path.size() - 1; i++) {
                Node from = inputGrid.getChildren().get(path.get(i));
                Node to = inputGrid.getChildren().get(path.get(i + 1));

                Bounds fromBounds = from.localToParent(from.getBoundsInLocal());
                Bounds toBounds = to.localToParent(to.getBoundsInLocal());

                double fromX = fromBounds.getMinX() + fromBounds.getWidth() / 2;
                double fromY = fromBounds.getMinY() + fromBounds.getHeight() / 2;
                double toX = toBounds.getMinX() + toBounds.getWidth() / 2;
                double toY = toBounds.getMinY() + toBounds.getHeight() / 2;

                Line line = new Line(fromX, fromY, toX, toY);
                line.setStroke(Color.rgb(93, 248, 253));
                line.setStrokeWidth(8);

                canvas.getChildren().add(line);
            }
        });
    }

    private void bindTileToView(TileModel tileModel, StackPane tileContainer,
                                ContextMenuModel contextMenuModel) {
        TextField tileInput = (TextField) tileContainer.getChildren().get(TILE_INPUT_FIELD_INDEX);
        Label tilePoints = (Label) tileContainer.getChildren().get(TILE_POINTS_LABEL_INDEX);

        if (contextMenuModel != null) {
            setupContextMenu(tileModel, tileInput, contextMenuModel);
        }

        ChangeListener<Object> modelToViewListener = (obs, oldVal, newVal) -> {
            String newText = tileModel.isWildcard() ?
                    tileModel.getWildcardLetter() : tileModel.getLetter();

            // Prevent feedback loop
            if (!tileInput.getText().equals(newText)) {
                tileInput.setText(newText);
            }
        };

        tileModel.letterProperty().addListener(modelToViewListener);
        tileModel.wildcardProperty().addListener(modelToViewListener);
        tileModel.wildcardProperty().addListener((obs, oldVal, newVal) -> {
            tileContainer.getStyleClass().remove("tile-wildcard");
            if (newVal) {
                tileContainer.getStyleClass().add("tile-wildcard");
            }
        });
        tileModel.wildcardLetterProperty().addListener(modelToViewListener);

        // Trigger once at the beginning to set the initial value
        tileInput.setText(tileModel.isWildcard() ?
                tileModel.getWildcardLetter() : tileModel.getLetter());

        tileInput.textProperty().addListener((obs, oldText, newText) -> {
            if (tileModel.isWildcard()) {
                tileModel.setWildcardLetter(newText);
            } else {
                tileModel.setLetter(newText);
            }
        });


        tilePoints.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String letter = tileModel.isWildcard() ? tileModel.getWildcardLetter() : tileModel.getLetter();
                    if (letter == null || letter.isEmpty() || !TileUtils.validLetter(letter.charAt(0))) return "0";
                    int points = TileUtils.letterToPoints(letter.charAt(0));
                    Set<TileModifier> modifiers = tileModel.getModifiers();
                    if (modifiers.contains(TileModifier.DOUBLE_LETTER)) points *= 2;
                    else if (modifiers.contains(TileModifier.TRIPLE_LETTER)) points *= 3;
                    return String.valueOf(points);
                },
                tileModel.letterProperty(),
                tileModel.wildcardLetterProperty(),
                tileModel.wildcardProperty(),
                tileModel.getModifiers()
        ));

        tileInput.setOnKeyTyped(event -> {
            Set<TileModifier> modifiers = tileModel.getModifiers();
            String character = event.getCharacter();

            switch (character) {
                case "1" -> toggle(modifiers, TileModifier.GEM);
                case "!" -> toggle(modifiers, TileModifier.FROZEN);
                case "2" -> {
                    modifiers.remove(TileModifier.TRIPLE_LETTER);
                    toggle(modifiers, TileModifier.DOUBLE_LETTER);
                }
                case "3" -> {
                    modifiers.remove(TileModifier.DOUBLE_LETTER);
                    toggle(modifiers, TileModifier.TRIPLE_LETTER);
                }
                case "@" -> {
                    modifiers.remove(TileModifier.TRIPLE_WORD);
                    toggle(modifiers, TileModifier.DOUBLE_WORD);
                }
                case "#" -> {
                    modifiers.remove(TileModifier.DOUBLE_WORD);
                    toggle(modifiers, TileModifier.TRIPLE_WORD);
                }
                case "0" -> modifiers.clear();
            }
        });

        tileInput.focusedProperty().addListener((obs, wasSelected, isSelected) -> {
            Node parent = tileInput.getParent();
            if (parent != null) {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), parent);
                scaleTransition.setToX(isSelected ? 1.1 : 1.0);
                scaleTransition.setToY(isSelected ? 1.1 : 1.0);
                scaleTransition.play();
            }
        });

        bindTileModifiers(tileModel, tileContainer);
        bindTileSelection(tileModel, tileContainer);
    }

    private void toggle(Set<TileModifier> modifiers, TileModifier modifier) {
        if (modifiers.contains(modifier)) {
            modifiers.remove(modifier);
        } else {
            modifiers.add(modifier);
        }
    }

    private void setupContextMenu(TileModel tileModel, TextField tileInput,
                                  ContextMenuModel contextMenuModel) {
        tileInput.setOnContextMenuRequested(event -> {
            tileInput.requestFocus();

            ObservableSet<TileModifier> modifiers = tileModel.getModifiers();
            contextMenuModel.setTileModel(tileModel);

            contextMenuModel.letterMultiplierProperty().set(
                    modifiers.contains(TileModifier.TRIPLE_LETTER) ? 3 :
                            modifiers.contains(TileModifier.DOUBLE_LETTER) ? 2 : 1
            );
            contextMenuModel.wordMultiplierProperty().set(
                    modifiers.contains(TileModifier.TRIPLE_WORD) ? 3 :
                            modifiers.contains(TileModifier.DOUBLE_WORD) ? 2 : 1
            );
            contextMenuModel.hasGemProperty().set(modifiers.contains(TileModifier.GEM));
            contextMenuModel.isFrozenProperty().set(modifiers.contains(TileModifier.FROZEN));

            if (contextMenuModel.shownProperty().get()) {
                contextMenuModel.shownProperty().set(false);
            }

            contextMenuModel.ownerProperty().set(tileInput);
            contextMenuModel.screenXProperty().set(event.getScreenX());
            contextMenuModel.screenYProperty().set(event.getScreenY());
            contextMenuModel.shownProperty().set(true);
        });
    }

    private void bindTileModifiers(TileModel tileModel, StackPane container) {
        Pane wordModBackground = (Pane) container.getChildren().get(TILE_MODIFIER_BACKGROUND_INDEX);
        StackPane letterModCont = (StackPane) container.getChildren().get(LETTER_MODIFIER_CONTAINER_INDEX);
        StackPane wordModCont = (StackPane) container.getChildren().get(WORD_MODIFIER_CONTAINER_INDEX);
        Polygon gem = (Polygon) container.getChildren().get(GEM_POLYGON_INDEX);
        Rectangle frozen = (Rectangle) container.getChildren().get(FROZEN_RECTANGLE_INDEX);
        Circle letterCircle = (Circle) letterModCont.getChildren().getFirst();
        Circle wordCircle = (Circle) wordModCont.getChildren().getFirst();
        Label letterLabel = (Label) letterModCont.getChildren().get(1);
        Label wordLabel = (Label) wordModCont.getChildren().get(1);

        tileModel.getModifiers().addListener((SetChangeListener<TileModifier>) change -> {
            ObservableSet<TileModifier> modifiers = tileModel.getModifiers();

            updateModifierLabel(letterModCont, letterCircle, letterLabel, modifiers,
                    TileModifier.DOUBLE_LETTER, "double-letter-modifier", "DL",
                    TileModifier.TRIPLE_LETTER, "triple-letter-modifier", "TL");

            updateModifierLabel(wordModCont, wordCircle, wordLabel, modifiers,
                    TileModifier.DOUBLE_WORD, "double-word-modifier", "2X",
                    TileModifier.TRIPLE_WORD, "triple-word-modifier", "3X");

            updateModifierBackground(wordModBackground, modifiers,
                    TileModifier.DOUBLE_WORD, "double-word-modifier",
                    TileModifier.TRIPLE_WORD, "triple-word-modifier");
            gem.setVisible(modifiers.contains(TileModifier.GEM));
            frozen.setVisible(modifiers.contains(TileModifier.FROZEN));
        });
    }

    private void updateModifierBackground(Pane background, ObservableSet<TileModifier> modifiers,
                                          TileModifier mod2x, String style2x,
                                          TileModifier mod3x, String style3x) {
        ObservableList<String> styleClass = background.getStyleClass();
        styleClass.removeAll(style2x, style3x);
        background.setVisible(true);

        if (modifiers.contains(mod2x)) {
            styleClass.add(style2x);
        } else if (modifiers.contains(mod3x)) {
            styleClass.add(style3x);
        } else {
            background.setVisible(false);
        }
    }

    private void updateModifierLabel(StackPane container, Circle circle, Label label,
                                     ObservableSet<TileModifier> modifiers,
                                     TileModifier mod2x, String style2x, String label2x,
                                     TileModifier mod3x, String style3x, String label3x) {
        ObservableList<String> styleClass = circle.getStyleClass();
        styleClass.removeAll(style2x, style3x);
        container.setVisible(true);

        if (modifiers.contains(mod2x)) {
            styleClass.add(style2x);
            label.setText(label2x);
        } else if (modifiers.contains(mod3x)) {
            styleClass.add(style3x);
            label.setText(label3x);
        } else {
            container.setVisible(false);
        }
    }

    private void bindTileSelection(TileModel tileModel, StackPane container) {
        tileModel.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                container.getStyleClass().add("tile-selected");
            } else {
                container.getStyleClass().remove("tile-selected");
            }

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), container);
            scaleTransition.setToX(isSelected ? 0.9 : 1.0);
            scaleTransition.setToY(isSelected ? 0.9 : 1.0);
            scaleTransition.play();
        });
    }
}
