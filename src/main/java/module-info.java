module org.dootz.spellcastsolver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens org.dootz.spellcastsolver to javafx.fxml;
    exports org.dootz.spellcastsolver;
}