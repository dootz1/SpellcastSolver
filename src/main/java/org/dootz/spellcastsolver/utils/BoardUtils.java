package org.dootz.spellcastsolver.utils;

import org.dootz.spellcastsolver.solver.board.Board;
import org.dootz.spellcastsolver.solver.board.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardUtils {
    private static final Random random = new Random();
    public static Board randomBoard(boolean tripleTile) {
        Board board = new Board();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                Tile tile = new Tile(TileUtils.randomLetter(), i, j);
                board.setTile(i, j, tile);
            }
        }

        int randomIndex = randomIndex(0, Constants.BOARD_TILES);
        board.getTile(indexToRow(randomIndex), indexToColumn(randomIndex)).addModifier(
                tripleTile ? TileModifier.TRIPLE_LETTER : TileModifier.DOUBLE_LETTER);
        randomIndex = randomIndex(0, Constants.BOARD_TILES);
        board.getTile(indexToRow(randomIndex), indexToColumn(randomIndex)).addModifier(TileModifier.DOUBLE_WORD);
        List<Integer> randomIndexes = randomIndexes(Constants.GEM_TILES, 0, Constants.BOARD_TILES);
        for (int index: randomIndexes) {
            board.getTile(indexToRow(index), indexToColumn(index)).addModifier(TileModifier.GEM);
        }

        return board;
    }

    public static int indexToRow(int index) {
        return index / Constants.BOARD_SIZE;
    }

    public static int indexToColumn(int index) {
        return index % Constants.BOARD_SIZE;
    }

    private static int randomIndex(int start, int end) {
        return start + random.nextInt(end);
    }

    public static List<Integer> randomIndexes(int amount, int poolStart, int poolEnd) {
        List<Integer> numbers = new ArrayList<>();

        for (int i = poolStart; i < poolEnd; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers.subList(0, amount);
    }

}
