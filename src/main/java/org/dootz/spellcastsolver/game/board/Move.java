package org.dootz.spellcastsolver.game.board;

import org.dootz.spellcastsolver.utils.TileModifier;

import java.util.ArrayList;
import java.util.List;

public class Move {
    private final List<Tile> tiles;
    private double evaluationScore;

    public Move() {
        this(new ArrayList<>(20));
    }

    public Move(List<Tile> tiles) {
        this.tiles = tiles;
        this.evaluationScore = 0;
    }

    public boolean isLongWord() {
        return tiles.size() >= 6;
    }

    public int getTotalPoints() {
        int points = 0;
        int multiplier = 1;
        for (Tile tile: tiles) {
            points += tile.getPoints();
            if (tile.hasModifier(TileModifier.DOUBLE_WORD)) {
                multiplier <<= 1; // multiply by 2
            }
            if (tile.hasModifier(TileModifier.TRIPLE_WORD)) {
                multiplier *= 3;
            }
        }
        points *= multiplier;
        if (isLongWord()) {
            points += 10;
        }
        return points;
    }

    public int getTotalGems() {
        int gems = 0;
        for (Tile tile: tiles) {
            if (tile.hasModifier(TileModifier.GEM)) {
                gems++;
            }
        }
        return gems;
    }

    public int getTotalSwaps() {
        int swaps = 0;
        for (Tile tile: tiles) {
            if (tile.isWildcard()) {
                swaps++;
            }
        }
        return swaps;
    }

    public int gemProfit() {
        return getTotalGems() - getTotalSwaps() * 3;
    }
    public void appendTile(Tile tile) {
        tiles.add(tile);
    }
    public void popTile() {
        tiles.removeLast();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Move copy() {
        List<Tile> copied = new ArrayList<>(tiles.size());
        for (Tile tile: tiles) {
            copied.add(tile.copy());
        }
        return new Move(copied);
    }

    public int compareTo(Move other) { // no null check for performance
        int cmp = Integer.compare(this.getTotalPoints(), other.getTotalPoints()); // higher points
        if (cmp != 0) return cmp;

        cmp = Integer.compare(other.getTotalSwaps(), this.getTotalSwaps()); // lower swaps
        if (cmp != 0) return cmp;

        return Integer.compare(this.getTotalGems(), other.getTotalGems()); // higher gems
    }

    public double getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(double evaluationScore) {
        this.evaluationScore = evaluationScore;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(tiles.size());
        for (Tile tile: tiles) {
            builder.append(tile.isWildcard() ? tile.getWildcardLetter() : tile.getLetter());
        }
        return builder.toString();
    }
}
