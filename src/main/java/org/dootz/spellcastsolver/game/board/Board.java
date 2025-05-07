package org.dootz.spellcastsolver.solver.board;

import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.utils.TileModifier;

public class Board {
    private Tile[][] tiles;

    public Board() {
        this(new Tile[Constants.BOARD_SIZE][Constants.BOARD_SIZE]);
    }

    public Board(Tile[][] tiles) {this.tiles = tiles;}

    public Tile getTile(int row, int column) {
        return tiles[row][column];
    }

    public void setTile(int row, int column, Tile tile) {
        tiles[row][column] = tile;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Board copy() {
        Tile[][] newTiles = new Tile[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                newTiles[i][j] = tiles[i][j].copy();
            }
        }
        return new Board(newTiles);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile == null) {
                    builder.append('.');
                    continue;
                }

                builder.append(tile.getLetter());
                if (tile.hasModifier(TileModifier.GEM)) {
                    builder.append('!');
                }
                if (tile.hasModifier(TileModifier.DOUBLE_WORD)) {
                    builder.append('@');
                }
                if (tile.hasModifier(TileModifier.TRIPLE_WORD)) {
                    builder.append('#');
                }
                if (tile.hasModifier(TileModifier.DOUBLE_LETTER)) {
                    builder.append('$');
                }
                if (tile.hasModifier(TileModifier.TRIPLE_LETTER)) {
                    builder.append('%');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
