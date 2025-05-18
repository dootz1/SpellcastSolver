package org.dootz.spellcastsolver.simulation;

import org.dootz.spellcastsolver.SpellcastSolver;
import org.dootz.spellcastsolver.game.Game;
import org.dootz.spellcastsolver.game.Player;
import org.dootz.spellcastsolver.game.Round;
import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.game.board.Move;
import org.dootz.spellcastsolver.solver.Evaluator;
import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.utils.BoardUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.dootz.spellcastsolver.utils.BenchmarkUtils.solveBoardConcurrently;

public class GameSimulator {
    private static final int GAMES = 100;
    private static final String OUTPUT_FILE = "benchmark/simulation_results.txt";

    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        dictionary.importFromFile(SpellcastSolver.class.getResourceAsStream("dictionary.txt"));

        List<Game> allGames = new ArrayList<>();

        long totalDuration;

        long simulationStart = System.nanoTime();

        for (int gameNumber = 1; gameNumber <= GAMES; gameNumber++) {
            System.out.printf("Simulating Game #%d\n", gameNumber);
            Game game = playSingleGame(dictionary);
            allGames.add(game);
        }

        long simulationEnd = System.nanoTime();
        totalDuration = simulationEnd - simulationStart;

        int totalScore = 0;
        int highestGameScore = 0;
        int lowestGameScore = Integer.MAX_VALUE;
        Move highestScoringMove = null;
        // Write everything at the end
        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
            for (int i = 0; i < allGames.size(); i++) {
                Game game = allGames.get(i);
                out.printf("=== Game %d ===\n", i + 1);

                totalScore += game.getFinalScore();
                highestGameScore = Math.max(highestGameScore, game.getFinalScore());
                lowestGameScore = Math.min(lowestGameScore, game.getFinalScore());

                for (Round round : game.getRounds()) {
                    Move move = round.getChosenMove().getMove();
                    if (highestScoringMove == null || move.points() > highestScoringMove.points()) {
                        highestScoringMove = move;
                    }
                }

                for (Round round : game.getRounds()) {
                    int roundNumber = round.getRoundNumber();
                    Move move = round.getChosenMove().getMove();
                    int gemsBefore = roundNumber == 1 ? 3 : game.getRounds().get(roundNumber - 2).getPlayerSnapshot().getGems();
                    int gemsAfter = round.getPlayerSnapshot().getGems();
                    double evalScore = round.getChosenMove().getEvaluationScore();

                    out.printf("Round %d:\n", roundNumber);
                    out.println(round.getBoardSnapshot());
                    out.printf("Move: %s | Points: %d | Gems: %d | EvalScore: %.2f | Swaps: %d | Gems Before: %d | After: %d\n\n",
                            move, move.points(), move.gems(), evalScore, move.swaps(), gemsBefore, gemsAfter);
                }

                out.printf("Game %d Score: %d | Gems left: %d\n\n", i + 1, game.getFinalScore(), game.getPlayer().getGems());
            }

            double averageScore = totalScore / (double) GAMES;
            double totalTimeMillis = totalDuration / 1_000_000.0;
            double timePerGame = totalTimeMillis / GAMES;
            List<Integer> sortedScores = allGames.stream()
                    .map(Game::getFinalScore)
                    .sorted()
                    .toList();

            double medianScore;
            int size = sortedScores.size();
            if (size % 2 == 1) {
                // Odd number of elements, take the middle one
                medianScore = sortedScores.get(size / 2);
            } else {
                // Even number of elements, take the average of the two middle ones
                medianScore = (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
            }

            out.println("=== Simulation Summary ===");
            out.printf("Games Simulated: %d\n", GAMES);
            out.printf("Average Game Score: %.2f\n", averageScore);
            out.printf("Median Game Score: %.2f\n", medianScore);
            out.printf("Highest Game Score: %d\n", highestGameScore);
            out.printf("Lowest Game Score: %d\n", lowestGameScore);
            out.printf("Top Single Move: %s (Points: %d)\n",
                    highestScoringMove, highestScoringMove != null ? highestScoringMove.points() : 0);
            out.printf("Total Solve Time: %.2fms\n", totalTimeMillis);
            out.printf("Time per Game: %.2fms\n", timePerGame);

        } catch (IOException e) {
            System.err.println("Failed to write simulation results: " + e.getMessage());
        }
    }

    private static Game playSingleGame(Dictionary dictionary) {
        Player player = new Player(3);
        Board initialBoard = BoardUtils.randomBoard(true);
        Game game = new Game(player, initialBoard);

        for (int roundNum = 1; roundNum <= 5; roundNum++) {
            System.out.printf("  Playing Round #%d\n", roundNum);
            game.startNextRound(roundNum);

            Solver solver = new Solver(dictionary, game.getBoard());

            try {
                solveBoardConcurrently(solver, player.getGems());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Execution interrupted for round " + roundNum);
                continue;
            }

            List<Move> moves = solver.getMovesAsList();
            Evaluator evaluator = new Evaluator();
            List<Evaluator.EvaluatedMove> evaluatedMoves = evaluator.evaluateMoves(moves, roundNum, player.getGems());
            evaluatedMoves.sort(Comparator.comparingDouble(Evaluator.EvaluatedMove::getEvaluationScore).reversed());
            Evaluator.EvaluatedMove best = evaluatedMoves.isEmpty() ?
                    new Evaluator.EvaluatedMove(new Move("", Collections.emptyList(), 0, 0, 0), 0) : evaluatedMoves.getFirst();

            game.submitMove(best);
        }

        return game;
    }
}
