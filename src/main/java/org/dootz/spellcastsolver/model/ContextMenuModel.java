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
    public IntegerProperty wordMultiplierProperty() {
        return wordMultiplier;
    }
    public BooleanProperty hasGemProperty() {
        return hasGem;
    }

    public BooleanProperty isFrozenProperty() {
        return isFrozen;
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
    public DoubleProperty screenYProperty() {
        return screenY;
    }

    public BooleanProperty shownProperty() {
        return isShown;
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