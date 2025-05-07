package org.dootz.spellcastsolver.game;

import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Tile;
import org.dootz.spellcastsolver.solver.Evaluator;
import org.dootz.spellcastsolver.utils.BoardUtils;
import org.dootz.spellcastsolver.utils.Constants;
import org.dootz.spellcastsolver.utils.TileModifier;
import org.dootz.spellcastsolver.utils.TileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private Board board;
    private Player player;
    private List<Round> rounds;
    private Round currentRound;

    public Game(Player player, Board board) {
        this.player = player;
        this.board = board;
        this.rounds = new ArrayList<>();
    }

    public void startNextRound(int roundNumber) {
        currentRound = new Round(roundNumber);
        rounds.add(currentRound);
    }

    public void submitMove(Evaluator.EvaluatedMove move) {
        player.addMove(move);
        player.modifyGems(move.getMove().getTotalGems() - move.getMove().getTotalSwaps() * 3);
        player.addPoints(move.getMove().getTotalPoints());

        currentRound.setChosenMove(new Evaluator.EvaluatedMove(move.getMove().copy(), move.getEvaluationScore()));
        currentRound.setPlayerSnapshot(player.copy());
        currentRound.setBoardSnapshot(board.copy());

        for (Tile tile: move.getMove().getTiles()) {
            int row = tile.getRow();
            int column = tile.getColumn();
            Tile boardTile = board.getTile(row, column);
            boardTile.getModifiers().clear();
            boardTile.setLetter(TileUtils.randomLetter());
        }

        // reassign modifiers
        List<Integer> validGemSpaces = new ArrayList<>();
        int gemsToReplace = Constants.GEM_TILES - board.getGems();
        boolean hasLetterMultiplier = board.hasLetterMultiplier();
        boolean hasWordMultiplier = board.hasWordMultiplier();

        for (int i = 0; i < Constants.BOARD_TILES; i++) {
            Tile boardTile = board.getTile(BoardUtils.indexToRow(i), BoardUtils.indexToColumn(i));
            if (!boardTile.hasModifier(TileModifier.GEM)) {
                validGemSpaces.add(i);
            }
        }

        if (!hasLetterMultiplier) {
            int index = BoardUtils.randomIndex(0, Constants.BOARD_TILES);
            board.getTile(BoardUtils.indexToRow(index), BoardUtils.indexToColumn(index)).addModifier(
                    BoardUtils.randomBoolean() ? TileModifier.DOUBLE_LETTER : TileModifier.TRIPLE_LETTER
            );
        }
        if (!hasWordMultiplier) {
            int index = BoardUtils.randomIndex(0, Constants.BOARD_TILES);
            board.getTile(BoardUtils.indexToRow(index), BoardUtils.indexToColumn(index)).addModifier(TileModifier.DOUBLE_WORD);
        }

        Collections.shuffle(validGemSpaces);
        for (int i = 0; i < gemsToReplace; i++) {
            int index = validGemSpaces.get(i);
            board.getTile(BoardUtils.indexToRow(index), BoardUtils.indexToColumn(index)).addModifier(TileModifier.GEM);
        }
    }

    public int getFinalScore() {
        return player.getTotalScore() + player.getGems();
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

}
