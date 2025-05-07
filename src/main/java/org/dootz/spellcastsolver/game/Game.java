package org.dootz.spellcastsolver.solver;

import org.dootz.spellcastsolver.solver.board.Board;
import org.dootz.spellcastsolver.solver.board.Word;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int round;
    private int gems;
    private Board board;

    public Game() {
        this(1, 3, new Board());
    }

    public Game(int round, int gems, Board board) {
        this.round = round;
        this.gems = gems;
        this.board = board;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
