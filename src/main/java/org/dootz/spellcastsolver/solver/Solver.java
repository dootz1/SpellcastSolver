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
        MoveBuilder mb = new MoveBuilder();
        DictionaryNode root = dictionary.getRoot();
        char letter = tile.getLetter();
        int progressUnits = 0;

        DictionaryNode childNode = root.getChild(letter);
        if (childNode != null) {
            mb.push(tile);
            findValidMoves(board, visited, mb, childNode, row, col, swaps);
            mb.pop();
            onProgress.accept(1);
            progressUnits++;
        }
        if (Thread.interrupted()) throw new InterruptedException(); // when cancelled

        if (swaps > 0) {
            tile.setWildcard(true);

            int mask = root.getChildMask(); // all viable letters
            mask &= ~(1 << (letter - 'A')); // exclude original letter
            int increment = 25 / Integer.bitCount(mask);

            while (mask != 0) { // only use characters that are valid (instead of going through every character)
                int bit = Integer.numberOfTrailingZeros(mask);
                mask &= mask - 1; // clear processed bit
                char wildcardLetter = (char) ('A' + bit);

                tile.setWildcardLetter(wildcardLetter);
                DictionaryNode wildcardChild = root.getChildByIndex(bit);
                mb.push(tile);
                findValidMoves(board, visited, mb, wildcardChild, row, col, swaps - 1);
                mb.pop();

                onProgress.accept(increment);
                progressUnits += increment;
            }
            tile.setWildcard(false);
            tile.setWildcardLetter(letter);
        }

        onProgress.accept(26 - progressUnits); // Fake remaining progress
    }

    private void findValidMoves(Board board, boolean[][] visited, MoveBuilder mb, DictionaryNode node, int row, int col, int swaps) {
        if (node.isWord()) {
            String key = mb.word();
            Move best = result.get(key);
            if (mb.beats(best)) {           // O(1) primitive compare
                result.put(key, mb.toMove());// allocate only if needed
            }
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
                mb.push(tile);
                findValidMoves(board, visited, mb, childNode, newRow, newCol, swaps);
                mb.pop();
            }

            // Swap move
            if (swaps > 0) {
                tile.setWildcard(true);

                int mask = node.getChildMask(); // all viable letters
                mask &= ~(1 << (letter - 'A')); // exclude original letter

                while (mask != 0) { // only use characters that are valid (instead of going through every character)
                    int bit = Integer.numberOfTrailingZeros(mask);
                    mask &= mask - 1; // clear processed bit
                    char wildcardLetter = (char) ('A' + bit);

                    tile.setWildcardLetter(wildcardLetter);
                    DictionaryNode wildcardChild = node.getChildByIndex(bit);
                    mb.push(tile);
                    findValidMoves(board, visited, mb, wildcardChild, newRow, newCol, swaps - 1);
                    mb.pop();
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
final class MoveBuilder {
    private static final int MAX_LEN = 50;

    /* tiles in order */
    private final Tile[] tileBuf = new Tile[MAX_LEN];
    private int len = 0;

    /* char buffer for the word */
    private final char[] wordBuf = new char[MAX_LEN];

    /* cached word */
    private String cachedWord;

    /* running tallies */
    private int points = 0;
    private int gems   = 0;
    private int swaps  = 0;
    private int wordMultiplier = 1;

    /* ---------- mutate during DFS ---------- */
    public void push(Tile t) {
        tileBuf[len] = t;
        wordBuf[len] = t.isWildcard() ? t.getWildcardLetter() : t.getLetter();
        len++;

        points += t.getPoints();
        if (t.hasModifier(TileModifier.DOUBLE_WORD)) wordMultiplier <<= 1;
        if (t.hasModifier(TileModifier.TRIPLE_WORD))  wordMultiplier *= 3;
        if (t.hasModifier(TileModifier.GEM))          gems++;
        if (t.isWildcard())                           swaps++;
        cachedWord = null;
    }

    public void pop() {
        len--;
        Tile t = tileBuf[len];

        points -= t.getPoints();
        if (t.hasModifier(TileModifier.DOUBLE_WORD)) wordMultiplier >>= 1;
        if (t.hasModifier(TileModifier.TRIPLE_WORD))  wordMultiplier /= 3;
        if (t.hasModifier(TileModifier.GEM))          gems--;
        if (t.isWildcard())                           swaps--;
        cachedWord = null;
    }

    /* ---------- fast getters ---------- */
    public String word() {
        if (cachedWord == null) {
            cachedWord = new String(wordBuf, 0, len);
            return cachedWord;
        }
        return cachedWord;
    }

    /* ---------- compare move builder with existing move ---------- */
    public boolean beats(Move best) {
        if (best == null) return true;
        int totalPoints = points * wordMultiplier + (len >= 6 ? 10 : 0);
        if (totalPoints != best.points()) return totalPoints > best.points();
        if (swaps != best.swaps()) return swaps < best.swaps();
        return gems > best.gems();
    }

    /* ---------- build final immutable Move ---------- */

    public Move toMove() {
        /* copy tiles into an immutable List */
        List<Tile> tiles = new ArrayList<>(len);
        for (int i = 0; i < len; i++) tiles.add(tileBuf[i].copy());

        int totalPoints = points * wordMultiplier + (len >= 6 ? 10 : 0);
        return new Move(word(), tiles, totalPoints, gems, swaps);
    }
}




