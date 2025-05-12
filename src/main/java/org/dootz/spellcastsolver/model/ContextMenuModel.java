package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.scene.Node;

public class ContextMenuModel {
    private final IntegerProperty letterMultiplier;
    private final IntegerProperty wordMultiplier;
    private final BooleanProperty hasGem;
    private final BooleanProperty isFrozen;
    private final ObjectProperty<TileModel> tileModel;
    private final DoubleProperty screenX;
    private final DoubleProperty screenY;
    private final BooleanProperty isShown;
    private final ObjectProperty<Node> owner;

    public ContextMenuModel() {
        letterMultiplier = new SimpleIntegerProperty(1);
        wordMultiplier = new SimpleIntegerProperty(1);
        hasGem = new SimpleBooleanProperty(false);
        isFrozen = new SimpleBooleanProperty(false);
        tileModel = new SimpleObjectProperty<>();
        screenX = new SimpleDoubleProperty(0);
        screenY = new SimpleDoubleProperty(0);
        isShown = new SimpleBooleanProperty(false);
        owner = new SimpleObjectProperty<>(null);
    }

    public IntegerProperty letterMultiplierProperty() {
        return letterMultiplier;
    }

    public int getLetterMultiplier() {
        return letterMultiplier.get();
    }

    public void setLetterMultiplier(int letterMultiplier) {
        this.letterMultiplier.set(letterMultiplier);
    }

    public IntegerProperty wordMultiplierProperty() {
        return wordMultiplier;
    }

    public int getWordMultiplier() {
        return wordMultiplier.get();
    }

    public void setWordMultiplier(int wordMultiplier) {
        this.wordMultiplier.set(wordMultiplier);
    }

    public BooleanProperty hasGemProperty() {
        return hasGem;
    }

    public boolean hasGem() {
        return hasGem.get();
    }

    public void setHasGem(boolean hasGem) {
        this.hasGem.set(hasGem);
    }

    public BooleanProperty isFrozenProperty() {
        return isFrozen;
    }

    public boolean isFrozen() {
        return isFrozen.get();
    }

    public void setIsFrozen(boolean isFrozen) {
        this.isFrozen.set(isFrozen);
    }

    public TileModel getTileModel() {
        return tileModel.get();
    }

    public ObjectProperty<TileModel> tileModelProperty() {
        return tileModel;
    }

    public void setTileModel(TileModel tileModel) {
        this.tileModel.set(tileModel);
    }

    public DoubleProperty screenXProperty() {
        return screenX;
    }

    public double getScreenX() {
        return screenX.get();
    }

    public void setScreenX(double screenX) {
        this.screenX.set(screenX);
    }

    public DoubleProperty screenYProperty() {
        return screenY;
    }

    public double getScreenY() {
        return screenY.get();
    }

    public void setScreenY(double screenY) {
        this.screenY.set(screenY);
    }

    public BooleanProperty shownProperty() {
        return isShown;
    }

    public boolean isShown() {
        return isShown.get();
    }

    public BooleanProperty isShownProperty() {
        return isShown;
    }

    public void setIsShown(boolean isShown) {
        this.isShown.set(isShown);
    }

    public ObjectProperty<Node> ownerProperty() {
        return owner;
    }

    @Override
    public String toString() {
        return "ContextMenuModel{" +
                "letterMultiplier=" + letterMultiplier.get() +
                ", wordMultiplier=" + wordMultiplier.get() +
                ", hasGem=" + hasGem.get() +
                ", isFrozen=" + isFrozen.get() +
                ", tileModel=" + tileModel.get() +
                ", screenX=" + screenX.get() +
                ", screenY=" + screenY.get() +
                ", isShown=" + isShown.get() +
                '}';
    }
}