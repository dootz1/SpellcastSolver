package org.dootz.spellcastsolver.solver.multithreading;
import javafx.beans.property.SimpleIntegerProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
public class ProgressReporter {
    private final PropertyChangeSupport propertyChangeSupport;
    private int progress = 0;

    public ProgressReporter() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void accumulateProgress(int progress){
        this.propertyChangeSupport.firePropertyChange("progress", this.progress, this.progress + progress);
        this.progress = this.progress + progress;
    }

    public void clearProgress() {
        this.propertyChangeSupport.firePropertyChange("progress", this.progress, 0);
        this.progress = 0;
    }

    public int getProgress() {
        return progress;
    }
}