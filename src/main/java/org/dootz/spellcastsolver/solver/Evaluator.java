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

    // Precomputed values - Credits: WintrCat
    private static final double[] AVERAGE_PROJECTED_SCORES = {32.7, 57.5, 74, 86};
    private static final double[] AVERAGE_PROJECTED_NET_GEM_PROFIT = {2.8, 1, -0.8, -2.6};

    public List<EvaluatedMove> evaluateMoves(List<Move> moves, int round, int gems) {
        List<EvaluatedMove> evaluatedMoves = new ArrayList<>(moves.size());
        boolean isFinalRound = round == 5;
        for (Move move: moves) {
            double score = move.points() + estimatedFutureSwapValue(round, Math.min(10,  gems + move.gemProfit()));
            if (isFinalRound) score += move.gems();
            evaluatedMoves.add(new EvaluatedMove(move, score));
        }
        return evaluatedMoves;
    }

    public double evaluateShuffle(EvaluatedMove topMove, int round, int gems) {
        if (gems < 1) return 0; // Can't shuffle without gems

        double remainingGems = gems - 1; // Spend 1 gem to shuffle
        double simulatedScore = 0;

        // Estimate the score gain from this round
        int swapTier = Math.min(3, (int) (remainingGems / 3));
        double thisRoundScore = AVERAGE_PROJECTED_SCORES[swapTier];
        if (round == 1) thisRoundScore *= 0.5; // first round doesn't have 2x word multiplier
        simulatedScore += thisRoundScore;

        // Estimate net gem change after this round
        remainingGems += AVERAGE_PROJECTED_NET_GEM_PROFIT[swapTier];

        if (round == 5) {
            // Last round: gems are added directly to score
            simulatedScore += remainingGems;
        } else {
            // In earlier rounds: estimate future value of remaining gems
            simulatedScore += estimatedFutureSwapValue(round, remainingGems);
        }

        // Evaluate opportunity cost of spending a gem
        double spentGemValue = estimatedGemValue(gems) - estimatedGemValue(gems - 1);

//        System.out.println("Added extra: " + spentGemValue);
//        System.out.printf("EvalScore+e: %.2f, ShuffleScore: %.2f, Difference: %.2f\n",
//                topMove.getEvaluationScore() + spentGemValue, simulatedScore,  topMove.getEvaluationScore() + spentGemValue - simulatedScore);
        // Compare shuffle vs. best move + saved gem
        return topMove.getEvaluationScore() + spentGemValue - simulatedScore;
    }

    private double estimatedFutureSwapValue(int round, double remainingGems) {
        if (round >= 5) return 0;

        if (round == 4) { // second to last round, only include full swaps
            return estimatedGemValue(remainingGems - remainingGems % 3);
        }

        return estimatedGemValue(remainingGems); // round <3, include partial swaps
    }

    private double estimatedGemValue(double gems) {
        int fullSwaps = (int) (gems / 3);
        double partialSwaps = gems % 3;

        double value = AVERAGE_PROJECTED_SCORES[fullSwaps];

        double delta = AVERAGE_PROJECTED_SCORES[Math.min(3, fullSwaps + 1)] - AVERAGE_PROJECTED_SCORES[fullSwaps];
        return value + delta / 3.0 * partialSwaps;
    }
}
