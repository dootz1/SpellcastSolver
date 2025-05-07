package org.dootz.spellcastsolver.utils;

import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.solver.multithreading.ProgressReporter;
import org.dootz.spellcastsolver.solver.multithreading.SolveSingle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BenchmarkUtils {

    private static final int TIMEOUT_SECONDS = 10;
    public static long solveBoardConcurrently(Solver solver, int gems) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long startTime = System.nanoTime();

        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                int id = row * Constants.BOARD_SIZE + col;
                executor.submit(new SolveSingle(id, solver, gems, row, col, true, new ProgressReporter()));
            }
        }

        executor.shutdown();
        if (!executor.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            System.err.println("Timeout occurred before all tasks completed.");
        }

        return System.nanoTime() - startTime;
    }

    public static long solveBoardSequentially(Solver solver, int gems) throws InterruptedException {
        long startTime = System.nanoTime();

        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                solver.findValidMoves(row, col, gems, false, x -> {});
            }
        }

        return System.nanoTime() - startTime;
    }
}
