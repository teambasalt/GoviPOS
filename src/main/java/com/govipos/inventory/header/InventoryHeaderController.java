package com.govipos.inventory.header;

import java.util.List;

import com.govipos.inventory.shell.InventorySessionContext;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

/**
 * JavaFX Controller for the Inventory Manager Header bar
 * (Brand mark, Branch pill, Change role dropdown menu, User identity & avatar).
 */
public class InventoryHeaderController {

    private static final List<String> AVAILABLE_ROLES = List.of(
            "Admin",
            "Cashier",
            "Branch Manager",
            "Inventory Manager"
    );

    @FXML private Label branchNameLabel;
    @FXML private Button changeRoleBtn;
    @FXML private Label officerNameLabel;
    @FXML private Label officerRoleLabel;

    private InventorySessionContext sessionContext;
    private ContextMenu roleDropdownMenu;

    @FXML
    private void initialize() {
        buildRoleDropdownMenu();
    }

    public void init(InventorySessionContext sessionContext) {
        this.sessionContext = sessionContext;
        if (branchNameLabel != null) {
            branchNameLabel.textProperty().bind(this.sessionContext.branchNameProperty());
        }
        if (officerNameLabel != null) {
            officerNameLabel.textProperty().bind(this.sessionContext.officerNameProperty());
        }
        if (officerRoleLabel != null) {
            officerRoleLabel.textProperty().bind(this.sessionContext.officerRoleProperty());
        }
    }

    private void buildRoleDropdownMenu() {
        roleDropdownMenu = new ContextMenu();
        roleDropdownMenu.getStyleClass().add("role-dropdown-menu");

        for (String role : AVAILABLE_ROLES) {
            MenuItem item = new MenuItem(role);
            item.getStyleClass().add("role-menu-item");
            item.setOnAction(e -> onSelectRole(role));
            roleDropdownMenu.getItems().add(item);
        }
    }

    private void onSelectRole(String role) {
        if (sessionContext != null) {
            sessionContext.setOfficerRole(role);
        }
    }

    @FXML
    private void onChangeRole() {
        if (changeRoleBtn == null) {
            return;
        }
        if (roleDropdownMenu == null) {
            buildRoleDropdownMenu();
        }
        if (roleDropdownMenu.isShowing()) {
            roleDropdownMenu.hide();
        } else {
            roleDropdownMenu.show(changeRoleBtn, Side.BOTTOM, 0, 4);
        }
    }
}
