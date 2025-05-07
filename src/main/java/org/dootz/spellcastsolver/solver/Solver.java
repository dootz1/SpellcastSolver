package org.dootz.spellcastsolver.solver;

import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Move;
import org.dootz.spellcastsolver.game.board.Tile;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.solver.dictionary.DictionaryNode;
import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.utils.TileModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntConsumer;

public class Solver {
    private static final int[][] DIRECTIONS = {
            {-1, -1}, // top-left
            {-1,  0}, // top
            {-1,  1}, // top-right
            { 0,  1}, // right
            { 1,  1}, // bottom-right
            { 1,  0}, // bottom
            { 1, -1}, // bottom-left
            { 0, -1}  // left
    };
    private final Dictionary dictionary;
    private final Board board;
    private final Map<String, Move> result;
    public Solver(Dictionary dictionary, Board board) {
        this.dictionary = dictionary;
        this.board = board;
        this.result = new ConcurrentHashMap<>();
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public Board getBoard() {
        return board;
    }

    public Map<String, Move> getResult() {
        return result;
    }

    public void clearResult() {
        result.clear();
    }
    public List<Move> getMovesAsList() {
        return new ArrayList<>(result.values());
    }

    public void findValidMoves(int row, int col, int gems, boolean threadSafe, IntConsumer onProgress) throws InterruptedException {
        Board board = threadSafe ? this.board.copy() : this.board;
        Tile tile = board.getTile(row, col);
        int swaps = gems / 3;

        if (tile.hasModifier(TileModifier.FROZEN)) {
            onProgress.accept(26);
            return;
        }

        boolean[][] visited = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        Move move = new Move();
        DictionaryNode root = dictionary.getRoot();
        char letter = tile.getLetter();
        int progressUnits = 0;

        DictionaryNode childNode = root.getChild(letter);
        if (childNode != null) {
            move.appendTile(tile);
            findValidMoves(board, visited, move, childNode, row, col, swaps);
            move.popTile();
            onProgress.accept(1);
            progressUnits++;
        }
        if (Thread.interrupted()) throw new InterruptedException(); // when cancelled

        if (swaps > 0) {
            tile.setWildcard(true);
            for (char wildcardLetter = 'A'; wildcardLetter <= 'Z'; wildcardLetter++) {
                if (letter == wildcardLetter) continue;

                DictionaryNode wildcardChild = root.getChild(wildcardLetter);
                if (wildcardChild != null) {
                    tile.setWildcardLetter(wildcardLetter);
                    move.appendTile(tile);
                    findValidMoves(board, visited, move, wildcardChild, row, col, swaps - 1);
                    move.popTile();
                }
                onProgress.accept(1);
                progressUnits++;
                if (Thread.interrupted()) throw new InterruptedException(); // when cancelled
            }
            tile.setWildcard(false);
            tile.setWildcardLetter(letter);
        }

        onProgress.accept(26 - progressUnits); // Fake remaining progress
    }

    private void findValidMoves(Board board, boolean[][] visited, Move move, DictionaryNode node, int row, int col, int swaps) {
        if (node.isWord()) {
            result.compute(move.toString(), (key, oldWord) ->
                (oldWord == null || move.compareTo(oldWord) > 0) ? move.copy() : oldWord
            );
        }

        visited[row][col] = true;

        for (int[] direction: DIRECTIONS) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (!isInBounds(newRow, newCol) || visited[newRow][newCol]) {
                continue;
            }

            Tile tile = board.getTile(newRow, newCol);

            if (tile.hasModifier(TileModifier.FROZEN)) {
                continue;
            }

            char letter = tile.getLetter();

            // Normal move
            DictionaryNode childNode = node.getChild(letter);
            if (childNode != null) {
                move.appendTile(tile);
                findValidMoves(board, visited, move, childNode, newRow, newCol, swaps);
                move.popTile();
            }

            // Swap move
            if (swaps > 0) {
                tile.setWildcard(true);
                for (char wildcardLetter = 'A'; wildcardLetter <= 'Z'; wildcardLetter++) {
                    if (wildcardLetter == letter) continue;

                    DictionaryNode wildcardChild = node.getChild(wildcardLetter);
                    if (wildcardChild != null) {
                        tile.setWildcardLetter(wildcardLetter);
                        move.appendTile(tile);
                        findValidMoves(board, visited, move, wildcardChild, newRow, newCol, swaps - 1);
                        move.popTile();
                    }
                }
                tile.setWildcard(false);
                tile.setWildcardLetter(letter);
            }

        }

        visited[row][col] = false;
    }
    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < Constants.BOARD_SIZE && col >= 0 && col < Constants.BOARD_SIZE;
    }
}
