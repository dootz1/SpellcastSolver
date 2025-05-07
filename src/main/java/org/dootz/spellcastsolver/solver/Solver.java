package org.dootz.spellcastsolver.solver;

import org.dootz.spellcastsolver.solver.board.Board;
import org.dootz.spellcastsolver.solver.board.Tile;
import org.dootz.spellcastsolver.solver.board.Word;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.solver.dictionary.DictionaryNode;
import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.utils.TileModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
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
    private Board board;
    private final Map<String, Word> result;
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

    public void setBoard(Board board) {
        this.board = board;
    }

    public Map<String, Word> getResult() {
        return result;
    }

    public void clearResult() {
        result.clear();
    }
    public List<Word> getResultAsList() {
        return new ArrayList<>(result.values());
    }

    public void solveSingle(int row, int col, int swaps, boolean threadSafe, IntConsumer onProgress) throws InterruptedException {
        Board board = threadSafe ? this.board.copy() : this.board;
        Tile tile = board.getTile(row, col);

        if (tile.hasModifier(TileModifier.FROZEN)) {
            onProgress.accept(26);
            return;
        }

        boolean[][] visited = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        Word word = new Word();
        DictionaryNode root = dictionary.getRoot();
        char letter = tile.getLetter();
        int progressUnits = 0;

        DictionaryNode childNode = root.getChild(letter);
        if (childNode != null) {
            word.appendTile(tile);
            solve(board, visited, word, childNode, row, col, swaps);
            word.popTile();
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
                    word.appendTile(tile);
                    solve(board, visited, word, wildcardChild, row, col, swaps - 1);
                    word.popTile();
                }
                onProgress.accept(1);
                progressUnits++;
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                } // when cancelled
            }
            tile.setWildcard(false);
            tile.setWildcardLetter(letter);
        }

        onProgress.accept(26 - progressUnits); // Fake remaining progress
    }

//    public void solve(int swaps, boolean multithreading) {
//        if (multithreading) {
//            solveMultithreaded(swaps);
//        } else {
//            solveSequential(swaps);
//        }
//    }

//    private void solveMultithreaded(int swaps) {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        List<Future<?>> futures = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                final int row = i;
//                final int col = j;
//                futures.add(executor.submit(() -> solveSingle(row, col, swaps, true)));
//            }
//        }
//
//        // Wait for all tasks to complete
//        for (Future<?> future : futures) {
//            try {
//                future.get(); // waits for task completion
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//
//        executor.shutdown();
//    }
//
//    private void solveSequential(int swaps) {
//        for (int i = 0; i < Board.BOARD_SIZE; i++) {
//            for (int j = 0; j < Board.BOARD_SIZE; j++) {
//                solveSingle(i, j, swaps, false);
//            }
//        }
//    }

    private void solve(Board board, boolean[][] visited, Word word, DictionaryNode node, int row, int col, int swaps) {
        if (node.isWord()) {
            result.compute(word.toString(), (key, oldWord) ->
                (oldWord == null || word.compareTo(oldWord) > 0) ? word.copy() : oldWord
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
                word.appendTile(tile);
                solve(board, visited, word, childNode, newRow, newCol, swaps);
                word.popTile();
            }

            // Swap move
            if (swaps > 0) {
                tile.setWildcard(true);
                for (char wildcardLetter = 'A'; wildcardLetter <= 'Z'; wildcardLetter++) {
                    if (wildcardLetter == letter) continue;

                    DictionaryNode wildcardChild = node.getChild(wildcardLetter);
                    if (wildcardChild != null) {
                        tile.setWildcardLetter(wildcardLetter);
                        word.appendTile(tile);
                        solve(board, visited, word, wildcardChild, newRow, newCol, swaps - 1);
                        word.popTile();
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
