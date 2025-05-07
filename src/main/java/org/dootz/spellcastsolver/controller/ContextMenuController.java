package org.dootz.spellcastsolver.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import org.controlsfx.control.ToggleSwitch;
import org.dootz.spellcastsolver.model.ContextMenuModel;
import org.dootz.spellcastsolver.model.DataModel;
import org.dootz.spellcastsolver.model.TileModel;
import org.dootz.spellcastsolver.utils.SliderUtils;
import org.dootz.spellcastsolver.utils.TileModifier;

public class ContextMenuController {
    private DataModel model;
    private Popup contextMenu;
    @FXML
    private Label tileIndex;
    @FXML
    private Label letterMultiplierLabel;
    @FXML
    private Label wordMultiplierLabel;
    @FXML
    private Slider letterMultiplierSlider;
    @FXML
    private Slider wordMultiplierSlider;
    @FXML
    private ToggleSwitch gemToggle;
    @FXML
    private ToggleSwitch frozenToggle;

    public void initialize() {
        SliderUtils.applySliderProgressGradient(letterMultiplierSlider, "-letter-multiplier-track-color", Color.rgb(155, 7, 255), Color.rgb(40, 40, 40));
        SliderUtils.applySliderProgressGradient(wordMultiplierSlider, "-word-multiplier-track-color", Color.rgb(155, 7, 255), Color.rgb(40, 40, 40));
    }
    public void setPopup(Popup contextMenu) {
        this.contextMenu = contextMenu;
    }
    public void initModel(DataModel model) {
        if (this.model != null) throw new IllegalStateException("Model can only be initialized once");
        this.model = model;

        ContextMenuModel contextMenuModel = model.getContextMenuModel();

        letterMultiplierSlider.valueProperty().bindBidirectional(contextMenuModel.letterMultiplierProperty());
        wordMultiplierSlider.valueProperty().bindBidirectional(contextMenuModel.wordMultiplierProperty());
        gemToggle.selectedProperty().bindBidirectional(contextMenuModel.hasGemProperty());
        frozenToggle.selectedProperty().bindBidirectional(contextMenuModel.isFrozenProperty());
        letterMultiplierLabel.textProperty().bind(contextMenuModel.letterMultiplierProperty().asString().concat("x"));
        wordMultiplierLabel.textProperty().bind(contextMenuModel.wordMultiplierProperty().asString().concat("x"));

        contextMenu.setOnHidden(e -> contextMenuModel.shownProperty().set(false));
        contextMenuModel.shownProperty().addListener((obs, wasShown, nowShown) -> {
            if (nowShown) {
                contextMenu.show(contextMenuModel.ownerProperty().get(),
                        contextMenuModel.screenXProperty().get(),
                        contextMenuModel.screenYProperty().get());
            }
        });

        // Bind changes to modifiers
        contextMenuModel.letterMultiplierProperty().addListener((obs, oldVal, newVal) -> {
            TileModel tile = contextMenuModel.getTileModel();
            ObservableSet<TileModifier> mods = tile.getModifiers();
            mods.remove(TileModifier.DOUBLE_LETTER);
            mods.remove(TileModifier.TRIPLE_LETTER);
            if (newVal.intValue() == 2) mods.add(TileModifier.DOUBLE_LETTER);
            else if (newVal.intValue() == 3) mods.add(TileModifier.TRIPLE_LETTER);
        });

        contextMenuModel.wordMultiplierProperty().addListener((obs, oldVal, newVal) -> {
            TileModel tile = contextMenuModel.getTileModel();
            ObservableSet<TileModifier> mods = tile.getModifiers();
            mods.remove(TileModifier.DOUBLE_WORD);
            mods.remove(TileModifier.TRIPLE_WORD);
            if (newVal.intValue() == 2) mods.add(TileModifier.DOUBLE_WORD);
            else if (newVal.intValue() == 3) mods.add(TileModifier.TRIPLE_WORD);
        });

        contextMenuModel.hasGemProperty().addListener((obs, oldVal, newVal) -> {
            TileModel tile = contextMenuModel.getTileModel();
            if (newVal) tile.getModifiers().add(TileModifier.GEM);
            else tile.getModifiers().remove(TileModifier.GEM);
        });

        contextMenuModel.isFrozenProperty().addListener((obs, oldVal, newVal) -> {
            TileModel tile = contextMenuModel.getTileModel();
            if (newVal) tile.getModifiers().add(TileModifier.FROZEN);
            else tile.getModifiers().remove(TileModifier.FROZEN);
        });

        tileIndex.textProperty().bind(Bindings.createStringBinding(() -> {
            TileModel tile = contextMenuModel.getTileModel();
            return tile != null ? String.format("Tile (%d, %d)", tile.getRow(), tile.getColumn()) : "Tile (unknown)";
        }, contextMenuModel.tileModelProperty()));
    }
}
