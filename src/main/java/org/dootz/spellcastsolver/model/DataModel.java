package org.dootz.spellcastsolver.model;

public class DataModel {
    private final BoardModel boardModel;
    private final ContextMenuModel contextMenuModel;
    private final SettingsModel settingsModel;
    private final TableModel tableModel;

    public DataModel(BoardModel boardModel, SettingsModel settingsModel, TableModel tableModel, ContextMenuModel contextMenuModel) {
        this.boardModel = boardModel;
        this.settingsModel = settingsModel;
        this.tableModel = tableModel;
        this.contextMenuModel = contextMenuModel;
    }

    public BoardModel getBoardModel() {
        return boardModel;
    }

    public ContextMenuModel getContextMenuModel() {
        return contextMenuModel;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    public TableModel getTableModel() {
        return tableModel;
    }
}
