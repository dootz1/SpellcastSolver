package org.dootz.spellcastsolver.solver.multithreading;

import org.dootz.spellcastsolver.game.Player;
import org.dootz.spellcastsolver.solver.Solver;

public class SolveSingle implements Runnable {
    private final Solver solver;
    private final int id;
    private final int row;
    private final int column;
    private final int gems;
    private final boolean threadSafe;
    private final ProgressReporter progressReporter;
    public SolveSingle(int id, Solver solver, int gems, int row, int column, boolean threadSafe, ProgressReporter progressReporter) {
        this.id = id;
        this.solver = solver;
        this.gems = gems;
        this.row = row;
        this.column = column;
        this.threadSafe = threadSafe;
        this.progressReporter = progressReporter;
    }

    @Override
    public void run() {
        try {
            solver.findValidMoves(row, column, gems, threadSafe, progressReporter::accumulateProgress);
        } catch (InterruptedException e) {
            System.out.println("Thread #" + id + " was interrupted and will exit.");
            return;
        }
//        System.out.println("Thread #" + id + " is completed");
    }
}
