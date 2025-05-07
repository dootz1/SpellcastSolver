package org.dootz.spellcastsolver.solver.board;

import org.dootz.spellcastsolver.utils.TileModifier;
import org.dootz.spellcastsolver.utils.TileUtils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class Tile {
    private char letter;
    private final Set<TileModifier> modifiers;
    private boolean wildcard;
    private char wildcardLetter;
    private int row;
    private int column;

    public Tile(char letter, int row, int column) {
        this(letter, EnumSet.noneOf(TileModifier.class), false, '\0', row, column);
    }

    public Tile(char letter, Set<TileModifier> modifiers, boolean wildcard, char wildcardLetter, int row, int column) {
        this.letter = letter;
        this.modifiers = modifiers;
        this.wildcard = wildcard;
        this.wildcardLetter = wildcardLetter;
        this.row = row;
        this.column = column;
    }

    public boolean hasModifier(TileModifier modifier) {
        return modifiers.contains(modifier);
    }

    public void addModifier(TileModifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(TileModifier modifier) {
        modifiers.remove(modifier);
    }

    public Set<TileModifier> getModifiers() { return modifiers;}

    public int getPoints() {
        int points = TileUtils.letterToPoints(wildcard ? wildcardLetter : letter);
        if (modifiers.contains(TileModifier.TRIPLE_LETTER)) {
            points *= 3;
        } else if (modifiers.contains(TileModifier.DOUBLE_LETTER)) {
            points *= 2;
        }
        return points;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }

    public char getWildcardLetter() {
        return wildcardLetter;
    }

    public void setWildcardLetter(char wildcardLetter) {
        this.wildcardLetter = wildcardLetter;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Tile copy() {
        return new Tile(letter, new HashSet<>(modifiers), wildcard, wildcardLetter, row, column);
    }
}
