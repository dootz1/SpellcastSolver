package org.dootz.spellcastsolver.game;

import org.dootz.spellcastsolver.solver.Evaluator;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int gems;
    private int points;
    private List<Evaluator.EvaluatedMove> movesPlayed;
    public Player(int gems) {
        this.gems = gems;
        this.movesPlayed = new ArrayList<>(5);
        this.points = 0;
    }

    public void addMove(Evaluator.EvaluatedMove move) {
        movesPlayed.add(move);
    }

    public int getTotalScore() {
        return movesPlayed.stream().mapToInt(move -> move.getMove().getTotalPoints()).sum();
    }

    public int getGems() { return gems; }
    public void modifyGems(int amount) { gems += amount; }
    public void addPoints(int amount) { points += amount; }
    public int getPoints() { return points; }
    public Player copy() {
        Player copy = new Player(this.gems);
        copy.points = this.points;
        copy.movesPlayed = new ArrayList<>(5);
        for (Evaluator.EvaluatedMove move: movesPlayed) {
            copy.movesPlayed.add(new Evaluator.EvaluatedMove(move.getMove().copy(), move.getEvaluationScore()));
        }
        return copy;
    }
}

