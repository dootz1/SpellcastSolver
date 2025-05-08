module org.dootz.spellcastsolver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens org.dootz.spellcastsolver.controller to javafx.fxml;
    exports org.dootz.spellcastsolver;
}