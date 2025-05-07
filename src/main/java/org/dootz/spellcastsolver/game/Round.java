package org.dootz.spellcastsolver.game;

import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Move;
import org.dootz.spellcastsolver.solver.Evaluator;

import java.util.ArrayList;
import java.util.List;

public class Round {
    private int roundNumber;
    private Player playerSnapshot;
    private Board boardSnapshot;
    private Evaluator.EvaluatedMove chosenMove;

    public Round(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public void setChosenMove(Evaluator.EvaluatedMove chosenMove) {
        this.chosenMove = chosenMove;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Player getPlayerSnapshot() {
        return playerSnapshot;
    }

    public void setPlayerSnapshot(Player playerSnapshot) {
        this.playerSnapshot = playerSnapshot;
    }

    public Board getBoardSnapshot() {
        return boardSnapshot;
    }

    public void setBoardSnapshot(Board boardSnapshot) {
        this.boardSnapshot = boardSnapshot;
    }

    public Evaluator.EvaluatedMove getChosenMove() {
        return chosenMove;
    }
}
