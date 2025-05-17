package org.dootz.spellcastsolver.simulation;

import org.dootz.spellcastsolver.SpellcastSolver;
import org.dootz.spellcastsolver.solver.Evaluator;
import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.game.board.Board;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.utils.BoardUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.dootz.spellcastsolver.utils.BenchmarkUtils.solveBoardConcurrently;
import static org.dootz.spellcastsolver.utils.BenchmarkUtils.solveBoardSequentially;

public class BoardSimulator {
    private static final int BOARD_COUNT = 100;
    private static final boolean ENABLE_MULTITHREADING = true; // toggle threading
    private static final int[] SWAP_COUNTS = {0, 3, 6, 9}; // try all swap amounts
    private static final int GEMS = SWAP_COUNTS[3];
    private static final int ROUND = 3;
    public static void main(String[] args) {
        Dictionary dictionary = loadDictionary();
        String mode = ENABLE_MULTITHREADING ? "MT" : "ST";
        String filename = String.format("results_%s_%dswaps.txt", mode, GEMS / 3);

        File outputFile = new File("benchmark", filename);
        if (!outputFile.getParentFile().exists()) {
            boolean created = outputFile.getParentFile().mkdirs();
            if (!created) {
                System.err.println("Failed to create output directory: " + outputFile.getParent());
            }
        }

        List<Board> boards = new ArrayList<>(BOARD_COUNT);
        List<Evaluator.EvaluatedMove> bestMoves = new ArrayList<>(BOARD_COUNT);
        List<Long> times = new ArrayList<>(BOARD_COUNT);
        List<Integer> frequencies = new ArrayList<>(BOARD_COUNT);

        for (int i = 0; i < BOARD_COUNT; i++) {
            Board board = BoardUtils.randomBoard(false);
            Solver solver = new Solver(dictionary, board);
            boards.add(board);

            long duration;

            try {
                if (ENABLE_MULTITHREADING) {
                    duration = solveBoardConcurrently(solver, GEMS);
                } else {
                    duration = solveBoardSequentially(solver, GEMS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Execution interrupted for board " + i);
                continue;
            }

            long evaluationTimeStart = System.nanoTime();
            var moves = solver.getMovesAsList();
            Evaluator evaluator = new Evaluator();
            var evaluatedMoves = evaluator.evaluateMoves(moves, ROUND, GEMS);
            evaluatedMoves.sort(Comparator.comparingDouble(Evaluator.EvaluatedMove::getEvaluationScore).reversed());
            if (!evaluatedMoves.isEmpty()) {
                bestMoves.add(evaluatedMoves.getFirst());
            }

            times.add(duration + (System.nanoTime() - evaluationTimeStart));
            frequencies.add(evaluatedMoves.size());
            System.out.printf("Finished %d: %.2fms\n", i, duration / 1_000_000.0);
        }

        printSummary(boards, bestMoves, frequencies, times, outputFile);
    }

    private static Dictionary loadDictionary() {
        Dictionary dictionary = new Dictionary();
        dictionary.importFromFile(SpellcastSolver.class.getResourceAsStream("dictionary.txt"));
        return dictionary;
    }

    private static void printSummary(List<Board> boards, List<Evaluator.EvaluatedMove> bestMoves,
                                     List<Integer> frequencies, List<Long> times, File outputFile) {
        int totalWords = 0;
        int totalPoints = 0;
        long totalTime = 0;
        double totalEvalScore = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (int i = 0; i < boards.size(); i++) {
                Board board = boards.get(i);
                Evaluator.EvaluatedMove bestMove = bestMoves.get(i);
                long time = times.get(i);

                writer.println(board);
                writer.printf("%s %d: %.2f %n%n", bestMove, bestMove.getMove().getTotalPoints(), bestMove.getEvaluationScore());

                System.out.println(board);
                System.out.printf("%s %d%n%n", bestMove, bestMove.getMove().getTotalPoints());

                totalWords += frequencies.get(i);
                totalPoints += bestMove.getMove().getTotalPoints();
                totalTime += time;
                totalEvalScore += bestMove.getEvaluationScore();
            }

            int avgWordsFound = totalWords / boards.size();
            double avgPoints = (double) totalPoints / boards.size();
            double avgTimeMs = totalTime / 1_000_000.0 / boards.size();
            double avgEvalScore = totalEvalScore / boards.size();

            writer.printf("SUMMARY: GEMS=%d, ROUND=%d%n", GEMS, ROUND);
            writer.printf("AVERAGE WORDS FOUND: %d%n", avgWordsFound);
            writer.printf("AVERAGE POINTS PER WORD: %.2f%n", avgPoints);
            writer.printf("AVERAGE EVALUATION SCORE PER WORD: %.2f%n", avgEvalScore);
            writer.printf("AVERAGE TIME PER BOARD: %.2fms%n", avgTimeMs);
            writer.printf("TOTAL TIME TAKEN: %.2fms%n", totalTime / 1_000_000.0);

            System.out.println("Results written to " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write results to file: " + e.getMessage());
        }
    }
}
