package org.dootz.spellcastsolver.solver;

import org.dootz.spellcastsolver.game.board.Move;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {
    public static class EvaluatedMove {
        private final Move move;
        private final double evaluationScore;
        public EvaluatedMove(Move move, double evaluationScore) {
            this.move = move;
            this.evaluationScore = evaluationScore;
        }

        public Move getMove() {
            return move;
        }

        public double getEvaluationScore() {
            return evaluationScore;
        }

        @Override
        public String toString() {
            return move.toString();
        }
    }
    private static final double[] AVERAGE_PROJECTED_SCORES = {32.7, 57.5, 74, 86}; // 0 - 3 swaps

    public List<EvaluatedMove> evaluateMoves(List<Move> moves, int round, int gems) {
        List<EvaluatedMove> evaluatedMoves = new ArrayList<>(moves.size());
        boolean isFinalRound = round == 5;
        for (Move move: moves) {
            double score = move.getTotalPoints() + estimatedSwapValue(round, Math.min(10,  gems + move.gemProfit()));
            if (isFinalRound) score += move.getTotalGems();
            evaluatedMoves.add(new EvaluatedMove(move, score));
        }
        return evaluatedMoves;
    }
    private double estimatedSwapValue(int round, int remainingGems) {
        if (round >= 5) return 0;
        int fullSwaps = remainingGems / 3;
        int partialSwaps = remainingGems % 3;

        double value = AVERAGE_PROJECTED_SCORES[fullSwaps];

        if (round < 4 && fullSwaps < AVERAGE_PROJECTED_SCORES.length - 1 && partialSwaps > 0) {
            double delta = AVERAGE_PROJECTED_SCORES[fullSwaps + 1] - AVERAGE_PROJECTED_SCORES[fullSwaps];
            value += delta / 3.0 * partialSwaps;
        }

        return value;
    }
}
