package org.dootz.spellcastsolver.solver.multithreading;

import org.dootz.spellcastsolver.solver.Solver;

public class SolveSingle implements Runnable {
    private final Solver solver;
    private final int id;
    private final int row;
    private final int column;
    private final int swaps;
    private final boolean threadSafe;
    private final ProgressReporter progressReporter;
    public SolveSingle(int id, Solver solver, int row, int column, int swaps, boolean threadSafe, ProgressReporter progressReporter) {
        this.id = id;
        this.solver = solver;
        this.row = row;
        this.column = column;
        this.swaps = swaps;
        this.threadSafe = threadSafe;
        this.progressReporter = progressReporter;
    }

    @Override
    public void run() {
        try {
            solver.solveSingle(row, column, swaps, threadSafe, progressReporter::accumulateProgress);
        } catch (InterruptedException e) {
//            System.out.println("Thread #" + id + " was interrupted and will exit.");
            return;
        }
//        System.out.println("Thread #" + id + " is completed");
    }
}
