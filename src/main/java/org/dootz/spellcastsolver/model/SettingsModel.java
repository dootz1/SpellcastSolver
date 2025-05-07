package org.dootz.spellcastsolver.model;

import javafx.beans.property.*;

public class SettingsModel {
    private final BooleanProperty isSolving;
    private final LongProperty timeStartedSolving;
    private final DoubleProperty progress;
    private final BooleanProperty multithreadingEnabled;
    private final IntegerProperty playerGems;
    private final IntegerProperty gameRound;
    private final BooleanProperty showInvalidBoardWarning;
    public SettingsModel() {
        this(false, 0, 0, true, 3, 1, false);
    }

    public SettingsModel(boolean isSolving, long timeStartedSolving, double progress, boolean multithreadingEnabled, int playerGems, int gameRound, boolean showInvalidBoardWarning) {
        this.isSolving = new SimpleBooleanProperty(isSolving);
        this.timeStartedSolving = new SimpleLongProperty(timeStartedSolving);
        this.progress = new SimpleDoubleProperty(progress);
        this.multithreadingEnabled = new SimpleBooleanProperty(multithreadingEnabled);
        this.playerGems = new SimpleIntegerProperty(playerGems);
        this.gameRound = new SimpleIntegerProperty(gameRound);
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

    public int getPlayerGems() {
        return playerGems.get();
    }

    public IntegerProperty playerGemsProperty() {
        return playerGems;
    }

    public void setPlayerGems(int playerGems) {
        this.playerGems.set(playerGems);
    }

    public int getGameRound() {
        return gameRound.get();
    }

    public IntegerProperty gameRoundProperty() {
        return gameRound;
    }

    public void setGameRound(int gameRound) {
        this.gameRound.set(gameRound);
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
                ", timeStartedSolving=" + timeStartedSolving.get() +
                ", progress=" + progress.get() +
                ", multithreadingEnabled=" + multithreadingEnabled.get() +
                ", playerGems=" + playerGems.get() +
                ", gameRound=" + gameRound.get() +
                ", showInvalidBoardWarning=" + showInvalidBoardWarning.get() +
                '}';
    }
}
