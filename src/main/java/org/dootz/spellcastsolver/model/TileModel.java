package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.dootz.spellcastsolver.solver.board.Tile;
import org.dootz.spellcastsolver.utils.TileModifier;

import java.util.EnumSet;
import java.util.Set;

public class TileModel {
    private final StringProperty letter;
    private final ObservableSet<TileModifier> modifiers;
    private final BooleanProperty wildcard;
    private final StringProperty wildcardLetter;
    private final BooleanProperty selected;
    private final IntegerProperty row;
    private final IntegerProperty column;

    public TileModel(int row, int column) {
        this("", FXCollections.observableSet(), false, "\0", false, row, column);
    }
    public TileModel(String letter, Set<TileModifier> modifiers, boolean wildcard, String wildcardLetter, boolean selected, int row, int column) {
        this.letter = new SimpleStringProperty(letter);
        this.modifiers = FXCollections.observableSet(modifiers);
        this.wildcard = new SimpleBooleanProperty(wildcard);
        this.wildcardLetter = new SimpleStringProperty(wildcardLetter);
        this.selected = new SimpleBooleanProperty(selected);
        this.row = new SimpleIntegerProperty(row);
        this.column = new SimpleIntegerProperty(column);
    }

    public String getLetter() {
        return letter.get();
    }

    public StringProperty letterProperty() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter.set(letter);
    }

    public ObservableSet<TileModifier> getModifiers() {
        return modifiers;
    }

    public boolean isWildcard() {
        return wildcard.get();
    }

    public BooleanProperty wildcardProperty() {
        return wildcard;
    }

    public void setWildcard(boolean wildcard) {
        this.wildcard.set(wildcard);
    }

    public String getWildcardLetter() {
        return wildcardLetter.get();
    }

    public StringProperty wildcardLetterProperty() {
        return wildcardLetter;
    }

    public void setWildcardLetter(String wildcardLetter) {
        this.wildcardLetter.set(wildcardLetter);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public int getRow() {
        return row.get();
    }

    public IntegerProperty rowProperty() {
        return row;
    }

    public void setRow(int row) {
        this.row.set(row);
    }

    public int getColumn() {
        return column.get();
    }

    public IntegerProperty columnProperty() {
        return column;
    }

    public void setColumn(int column) {
        this.column.set(column);
    }
    public Tile toDomainObject() {
        EnumSet<TileModifier> safeModifiers = modifiers.isEmpty()
                ? EnumSet.noneOf(TileModifier.class)
                : EnumSet.copyOf(modifiers);
        return new Tile(letter.get().charAt(0), safeModifiers, wildcard.get(), wildcardLetter.get().charAt(0), row.get(), column.get());
    }

    @Override
    public String toString() {
        return "TileModel{" +
                "letter=" + letter.get() +
                ", modifiers=" + modifiers +
                ", isWildcard=" + wildcard.get() +
                ", wildcardLetter=" + wildcardLetter.get() +
                '}';
    }
}
