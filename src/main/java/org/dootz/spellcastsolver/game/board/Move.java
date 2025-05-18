package org.dootz.spellcastsolver.game.board;

import java.util.Collections;
import java.util.List;


public final class Move implements Comparable<Move> {
    private final String word;          // immutable key
    private final List<Tile> tiles;     // immutable copy
    private final int points;
    private final int gems;
    private final int swaps;

    public Move(String word, List<Tile> tiles, int points, int gems, int swaps) {
        this.word   = word;
        this.tiles  = Collections.unmodifiableList(tiles);
        this.points = points;
        this.gems   = gems;
        this.swaps  = swaps;
    }

    /* -------- public API -------- */
    public String word()   { return word; }
    public List<Tile> tiles() { return tiles; }
    public int points()    { return points; }
    public int gems()      { return gems; }
    public int swaps()     { return swaps; }
    public int gemProfit() { return gems - swaps * 3; }
    public boolean isLong() { return word.length() >= 6; }

    @Override public String toString()      { return word; }
    @Override public int hashCode()         { return word.hashCode(); }
    @Override public boolean equals(Object o) {
        return (o instanceof Move m) && word.equals(m.word);
    }

    @Override
    public int compareTo(Move o) {
        int c = Integer.compare(points, o.points);
        if (c != 0) return c;
        c = Integer.compare(o.swaps, swaps);
        if (c != 0) return c;
        return Integer.compare(gems, o.gems);
    }
}