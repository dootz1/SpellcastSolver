package org.dootz.spellcastsolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.dootz.spellcastsolver.controller.BoardController;
import org.dootz.spellcastsolver.controller.ContextMenuController;
import org.dootz.spellcastsolver.controller.SettingsController;
import org.dootz.spellcastsolver.controller.TableController;
import org.dootz.spellcastsolver.model.*;
import org.dootz.spellcastsolver.solver.dictionary.Dictionary;

import java.io.IOException;

public class SpellcastSolver extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMousePressed = false;

    @Override
    public void start(Stage stage) throws IOException {
        String stylesheet = SpellcastSolver.class.getResource("css/root.css").toExternalForm();
        AnchorPane root = createRoot(stylesheet);
        AnchorPane main = createMainLayout();
        root.getChildren().add(main);

        setupDivider(main);

        BoardController boardController = loadBoardController(main);
        SettingsController settingsController = loadSettingsController(main);
        TableController tableController = loadTableController(main);

        createTopBar(root);

        var contextMenuPair = createContextMenuWithController();
        Popup contextMenu = contextMenuPair.getKey();
        ContextMenuController contextMenuController = contextMenuPair.getValue();

        BoardModel boardModel = new BoardModel();
        SettingsModel settingsModel = new SettingsModel();
        TableModel tableModel = new TableModel();
        ContextMenuModel contextMenuModel = new ContextMenuModel();
        DataModel model = new DataModel(boardModel, settingsModel, tableModel, contextMenuModel);

        Dictionary dictionary = new Dictionary();
        dictionary.importFromFile(SpellcastSolver.class.getResourceAsStream("dictionary.txt"));

        initControllers(boardController, settingsController, tableController, contextMenuController, model, dictionary, contextMenu);

        stage.initStyle(StageStyle.TRANSPARENT);
        setupDragEventHandlers(root, stage);

        Scene scene = new Scene(root, 1440, 900);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("SpellcastSolver");
        stage.setScene(scene);
        stage.show();
    }

    private AnchorPane createRoot(String stylesheet) {
        AnchorPane root = new AnchorPane();
        root.getStylesheets().add(stylesheet);
        root.setId("root");
        return root;
    }

    private AnchorPane createMainLayout() {
        AnchorPane main = new AnchorPane();
        main.getStyleClass().add("main");
        main.setLayoutX(69);
        main.setLayoutY(111);
        return main;
    }

    private void setupDivider(AnchorPane main) {
        Line line = new Line();
        line.getStyleClass().add("divider");
        line.setStartX(602);
        line.setStartY(55);
        line.setEndX(602);
        line.setEndY(670);
        main.getChildren().add(line);
    }

    private BoardController loadBoardController(AnchorPane main) throws IOException {
        FXMLLoader boardLoader = new FXMLLoader(SpellcastSolver.class.getResource("fxml/board.fxml"));
        AnchorPane board = boardLoader.load();
        board.setLayoutX(635);
        board.setLayoutY(29);
        main.getChildren().add(board);
        return boardLoader.getController();
    }

    private SettingsController loadSettingsController(AnchorPane main) throws IOException {
        FXMLLoader settingsLoader = new FXMLLoader(SpellcastSolver.class.getResource("fxml/settings.fxml"));
        VBox settings = settingsLoader.load();
        settings.setLayoutX(57);
        settings.setLayoutY(21);
        main.getChildren().add(settings);
        return settingsLoader.getController();
    }

    private TableController loadTableController(AnchorPane main) throws IOException {
        FXMLLoader tableLoader = new FXMLLoader(SpellcastSolver.class.getResource("fxml/table.fxml"));
        VBox table = tableLoader.load();
        table.setLayoutX(58);
        table.setLayoutY(135);
        main.getChildren().add(table);
        return tableLoader.getController();
    }

    private void createTopBar(AnchorPane root) throws IOException {
        FXMLLoader topBarLoader = new FXMLLoader(SpellcastSolver.class.getResource("fxml/top-bar.fxml"));
        AnchorPane topBar = topBarLoader.load();
        topBar.setLayoutX(69);
        topBar.setLayoutY(18);
        root.getChildren().add(topBar);
    }

    private Pair<Popup, ContextMenuController> createContextMenuWithController() throws IOException {
        Popup contextMenu = new Popup();
        FXMLLoader popupLoader = new FXMLLoader(SpellcastSolver.class.getResource("fxml/context-menu.fxml"));
        VBox content = popupLoader.load();
        contextMenu.getContent().setAll(content);
        contextMenu.setAutoHide(true);

        ContextMenuController contextMenuController = popupLoader.getController();
        return new Pair<>(contextMenu, contextMenuController);
    }

    private void initControllers(BoardController boardController, SettingsController settingsController,
                                 TableController tableController, ContextMenuController contextMenuController,
                                 DataModel model, Dictionary dictionary, Popup contextMenu) {
        boardController.initModel(model);
        settingsController.initModel(model);
        settingsController.setDictionary(dictionary);
        tableController.initModel(model);
        contextMenuController.setPopup(contextMenu);
        contextMenuController.initModel(model);
    }

    private void setupDragEventHandlers(AnchorPane root, Stage stage) {
        root.setOnMousePressed(event -> {
            if (!(event.getButton() == MouseButton.PRIMARY)) return;
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            isMousePressed = true;
        });

        root.setOnMouseReleased(event -> {
            if (!(event.getButton() == MouseButton.PRIMARY)) return;
            isMousePressed = false;
        });

        root.setOnMouseDragged(event -> {
            if (!(event.getButton() == MouseButton.PRIMARY) || !isMousePressed) return;
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}