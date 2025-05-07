package org.dootz.spellcastsolver.solver.multithreading;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.dootz.spellcastsolver.solver.Solver;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;
import org.dootz.spellcastsolver.utils.Constants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SolverTask extends Task<Void> {
    private static final int MAX_PROGRESS = Dictionary.ALPHABET_CHARACTERS * Constants.BOARD_SIZE * Constants.BOARD_SIZE;
    private static final int TERMINATION_TIMEOUT_SECONDS = 20;
    private final Solver solver;
    private final int swaps;
    private final boolean useMultithreading;
    private final ProgressReporter progressReporter;
    public SolverTask(Solver solver, int swaps, boolean multithreading) {
        this.solver = solver;
        this.swaps = swaps;
        this.useMultithreading = multithreading;
        this.progressReporter = new ProgressReporter();
        updateProgress(0, MAX_PROGRESS);
    }
    @Override
    protected Void call() {
        progressReporter.addPropertyChangeListener(evt ->
                Platform.runLater(() ->
                        updateProgress((int) evt.getNewValue(), MAX_PROGRESS)
                )
        );

        if (!useMultithreading) {
            runSequentially();
        } else {
            runInParallel();
        }

        return null;
    }

    private void runSequentially() {
        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                if (isCancelled()) return;
                int id = row * Constants.BOARD_SIZE + col;
                new SolveSingle(id, solver, row, col, swaps, false, progressReporter).run();
            }
        }
    }

    private void runInParallel() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int row = 0; row < Constants.BOARD_SIZE; row++) {
            for (int col = 0; col < Constants.BOARD_SIZE; col++) {
                int id = row * Constants.BOARD_SIZE + col;
                executor.submit(new SolveSingle(id, solver, row, col, swaps, true, progressReporter));
            }
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}
