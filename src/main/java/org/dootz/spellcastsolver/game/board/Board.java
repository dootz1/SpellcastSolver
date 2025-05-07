package org.dootz.spellcastsolver.game.board;

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

    public int getGems() {
        int gems = 0;
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (tiles[i][j].hasModifier(TileModifier.GEM)) gems++;
            }
        }
        return gems;
    }

    public boolean hasLetterMultiplier() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                Tile tile = tiles[i][j];
                if (tile.hasModifier(TileModifier.TRIPLE_LETTER) || tile.hasModifier(TileModifier.DOUBLE_LETTER))
                    return true;
            }
        }
        return false;
    }

    public boolean hasWordMultiplier() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                Tile tile = tiles[i][j];
                if (tile.hasModifier(TileModifier.TRIPLE_WORD) || tile.hasModifier(TileModifier.DOUBLE_WORD))
                    return true;
            }
        }
        return false;
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

                builder.append(tile);
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
