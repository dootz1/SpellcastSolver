package org.dootz.spellcastsolver.utils;

import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardUtils {
    private static final Random random = new Random();

    private BoardUtils() {}
    public static Board randomBoard(boolean tripleTile) {
        Board board = new Board();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                Tile tile = new Tile(TileUtils.randomLetter(), i, j);
                board.setTile(i, j, tile);
            }
        }

        int letterModIndex = randomIndex(0, Constants.BOARD_TILES);
        TileModifier letterMod = tripleTile && randomBoolean()
                ? TileModifier.TRIPLE_LETTER
                : TileModifier.DOUBLE_LETTER;
        board.getTile(indexToRow(letterModIndex), indexToColumn(letterModIndex)).addModifier(letterMod);

        // Add word multiplier modifier
        int wordModIndex = randomIndex(0, Constants.BOARD_TILES);
        board.getTile(indexToRow(wordModIndex), indexToColumn(wordModIndex)).addModifier(TileModifier.DOUBLE_WORD);

        // Add gem modifiers
        for (int index : randomIndexes(Constants.GEM_TILES, 0, Constants.BOARD_TILES)) {
            board.getTile(indexToRow(index), indexToColumn(index)).addModifier(TileModifier.GEM);
        }

        return board;
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
    public static int indexToRow(int index) {
        return index / Constants.BOARD_SIZE;
    }

    public static int indexToColumn(int index) {
        return index % Constants.BOARD_SIZE;
    }

    public static int randomIndex(int start, int end) {
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
