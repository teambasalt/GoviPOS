package com.govipos.inventory.shell;

import com.govipos.inventory.model.InventoryAction;
import com.govipos.inventory.model.WarehouseSummary;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Shared observable session state holder allowing inventory sub-modules
 * (topBar, statRibbonPane, actionCardsPane) to communicate without tight coupling.
 */
public final class InventorySessionContext {

    private final StringProperty branchName =
            new SimpleStringProperty("Piliyandala #01");
    private final StringProperty officerName =
            new SimpleStringProperty("Roshan Jayawardena");
    private final StringProperty officerRole =
            new SimpleStringProperty("Inventory Officer");
    private final IntegerProperty activeBatchesCount =
            new SimpleIntegerProperty(1420);
    private final IntegerProperty lowCountAlerts =
            new SimpleIntegerProperty(3);
    private final IntegerProperty expiredBatchesCount =
            new SimpleIntegerProperty(2);
    private final ObjectProperty<InventoryAction> currentAction =
            new SimpleObjectProperty<>(InventoryAction.DASHBOARD);

    public StringProperty branchNameProperty() {
        return branchName;
    }

    public String getBranchName() {
        return branchName.get();
    }

    public void setBranchName(String name) {
        this.branchName.set(name);
    }

    public StringProperty officerNameProperty() {
        return officerName;
    }

    public String getOfficerName() {
        return officerName.get();
    }

    public void setOfficerName(String name) {
        this.officerName.set(name);
    }

    public StringProperty officerRoleProperty() {
        return officerRole;
    }

    public String getOfficerRole() {
        return officerRole.get();
    }

    public void setOfficerRole(String role) {
        this.officerRole.set(role);
    }

    public IntegerProperty activeBatchesCountProperty() {
        return activeBatchesCount;
    }

    public int getActiveBatchesCount() {
        return activeBatchesCount.get();
    }

    public void setActiveBatchesCount(int count) {
        this.activeBatchesCount.set(count);
    }

    public IntegerProperty lowCountAlertsProperty() {
        return lowCountAlerts;
    }

    public int getLowCountAlerts() {
        return lowCountAlerts.get();
    }

    public void setLowCountAlerts(int count) {
        this.lowCountAlerts.set(count);
    }

    public IntegerProperty expiredBatchesCountProperty() {
        return expiredBatchesCount;
    }

    public int getExpiredBatchesCount() {
        return expiredBatchesCount.get();
    }

    public void setExpiredBatchesCount(int count) {
        this.expiredBatchesCount.set(count);
    }

    public ObjectProperty<InventoryAction> currentActionProperty() {
        return currentAction;
    }

    public InventoryAction getCurrentAction() {
        return currentAction.get();
    }

    public void setCurrentAction(InventoryAction action) {
        this.currentAction.set(action);
    }

    public void applySummary(WarehouseSummary summary) {
        if (summary != null) {
            this.activeBatchesCount.set(summary.activeBatches());
            this.lowCountAlerts.set(summary.lowCountAlerts());
            this.expiredBatchesCount.set(summary.expiredBatches());
        }
    }
}
