package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;

public class SettingsModel {
    private final BooleanProperty isSolving;
    private final LongProperty timeStartedSolving;
    private final DoubleProperty progress;
    private final BooleanProperty multithreadingEnabled;
    private final IntegerProperty maxSwaps;
    private final BooleanProperty showInvalidBoardWarning;
    public SettingsModel() {
        this(false, 0, 0, true, 0, false);
    }

    public SettingsModel(boolean isSolving, long timeStartedSolving, double progress, boolean multithreadingEnabled, int maxSwaps, boolean showInvalidBoardWarning) {
        this.isSolving = new SimpleBooleanProperty(isSolving);
        this.timeStartedSolving = new SimpleLongProperty(timeStartedSolving);
        this.progress = new SimpleDoubleProperty(progress);
        this.multithreadingEnabled = new SimpleBooleanProperty(multithreadingEnabled);
        this.maxSwaps = new SimpleIntegerProperty(maxSwaps);
        this.showInvalidBoardWarning = new SimpleBooleanProperty(showInvalidBoardWarning);
    }

    public boolean isSolving() {
        return isSolving.get();
    }

    public BooleanProperty isSolvingProperty() {
        return isSolving;
    }

    public void setIsSolving(boolean isSolving) {
        this.isSolving.set(isSolving);
    }

    public long getTimeStartedSolving() {
        return timeStartedSolving.get();
    }

    public LongProperty timeStartedSolvingProperty() {
        return timeStartedSolving;
    }

    public void setTimeStartedSolving(long timeStartedSolving) {
        this.timeStartedSolving.set(timeStartedSolving);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public boolean isMultithreadingEnabled() {
        return multithreadingEnabled.get();
    }

    public BooleanProperty multithreadingEnabledProperty() {
        return multithreadingEnabled;
    }

    public void setMultithreadingEnabled(boolean multithreadingEnabled) {
        this.multithreadingEnabled.set(multithreadingEnabled);
    }

    public int getMaxSwaps() {
        return maxSwaps.get();
    }

    public IntegerProperty maxSwapsProperty() {
        return maxSwaps;
    }

    public void setMaxSwaps(int maxSwaps) {
        this.maxSwaps.set(maxSwaps);
    }

    public boolean isShowInvalidBoardWarning() {
        return showInvalidBoardWarning.get();
    }

    public BooleanProperty showInvalidBoardWarningProperty() {
        return showInvalidBoardWarning;
    }

    public void setShowInvalidBoardWarning(boolean showInvalidBoardWarning) {
        this.showInvalidBoardWarning.set(showInvalidBoardWarning);
    }

    @Override
    public String toString() {
        return "SettingsModel{" +
                "isSolving=" + isSolving.get() +
                ", progress=" + progress.get() +
                ", multithreadingEnabled=" + multithreadingEnabled.get() +
                ", maxSwaps=" + maxSwaps.get() +
                '}';
    }
}
