package com.example.bhojhon.controller;

import com.example.bhojhon.data.DatabaseHelper;
import com.example.bhojhon.model.OrderInfo;
import com.example.bhojhon.model.RestaurantOwner;
import com.example.bhojhon.util.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Optional;

import java.util.List;

public class AdminDashboardController extends BaseController {

    // ===== Stat Cards =====
    @FXML
    private Label statRestaurantsValue;
    @FXML
    private Label statOrdersValue;
    @FXML
    private Label statStationsValue;

    // ===== Orders Tab =====
    @FXML
    private TableView<OrderInfo> ordersTable;
    @FXML
    private TableColumn<OrderInfo, String> orderIdCol;
    @FXML
    private TableColumn<OrderInfo, String> nameCol;
    @FXML
    private TableColumn<OrderInfo, String> phoneCol;
    @FXML
    private TableColumn<OrderInfo, String> trainCol;
    @FXML
    private TableColumn<OrderInfo, String> seatCol;
    @FXML
    private TableColumn<OrderInfo, String> stationCol;
    @FXML
    private TableColumn<OrderInfo, String> itemsCol;
    @FXML
    private TableColumn<OrderInfo, Double> totalCol;
    @FXML
    private TableColumn<OrderInfo, String> dateCol;

    // ===== Restaurants Tab =====
    @FXML
    private TableView<RestaurantOwner> restaurantsTable;
    @FXML
    private TableColumn<RestaurantOwner, String> restaurantNameCol;
    @FXML
    private TableColumn<RestaurantOwner, String> ownerEmailCol;
    @FXML
    private TableColumn<RestaurantOwner, String> restaurantStationCol;
    @FXML
    private TableColumn<RestaurantOwner, String> regDateCol;
    @FXML
    private TableColumn<RestaurantOwner, RestaurantOwner> statusCol;
    @FXML
    private Label totalRestaurantsLabel;

    @Override
    public void initialize() {
        // Wire Orders columns
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        trainCol.setCellValueFactory(new PropertyValueFactory<>("trainNumber"));
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        stationCol.setCellValueFactory(new PropertyValueFactory<>("stationName"));
        itemsCol.setCellValueFactory(new PropertyValueFactory<>("orderItems"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        // Wire Restaurants columns
        restaurantNameCol.setCellValueFactory(new PropertyValueFactory<>("restaurantName"));
        ownerEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        restaurantStationCol.setCellValueFactory(new PropertyValueFactory<>("stationName"));
        regDateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        statusCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        statusCol.setCellFactory(new Callback<TableColumn<RestaurantOwner, RestaurantOwner>, TableCell<RestaurantOwner, RestaurantOwner>>() {
            @Override
            public TableCell<RestaurantOwner, RestaurantOwner> call(TableColumn<RestaurantOwner, RestaurantOwner> param) {
                return new TableCell<RestaurantOwner, RestaurantOwner>() {
                    private final Button approveBtn = new Button("Review");
                    private final Label approvedLbl = new Label("Approved");

                    {
                        approveBtn.getStyleClass().add("fm-btn-primary");
                        approveBtn.setStyle("-fx-background-color: #3b82f6; -fx-padding: 5 10; -fx-font-size: 12px;");
                        approveBtn.setOnAction(event -> {
                            RestaurantOwner owner = getTableView().getItems().get(getIndex());
                            showApprovalDialog(owner);
                        });
                        
                        approvedLbl.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-background-color: #d1fae5; -fx-padding: 4 12; -fx-background-radius: 12px;");
                    }

                    @Override
                    protected void updateItem(RestaurantOwner owner, boolean empty) {
                        super.updateItem(owner, empty);
                        if (empty || owner == null) {
                            setGraphic(null);
                        } else {
                            if (owner.isApproved()) {
                                setGraphic(approvedLbl);
                            } else {
                                setGraphic(approveBtn);
                            }
                        }
                    }
                };
            }
        });

        refreshAll();
    }

    /** Called by the Refresh button in the header */
    @FXML
    private void loadData() {
        refreshAll();
    }

    /** Refresh everything: stat cards + both tables */
    private void refreshAll() {
        DatabaseHelper db = new DatabaseHelper();

        // Stat cards
        if (statRestaurantsValue != null)
            statRestaurantsValue.setText(String.valueOf(db.getRestaurantCount()));
        if (statOrdersValue != null)
            statOrdersValue.setText(String.valueOf(db.getOrderCount()));
        if (statStationsValue != null)
            statStationsValue.setText(String.valueOf(db.getStationCount()));

        // Orders table
        ordersTable.setItems(FXCollections.observableArrayList(db.getAllOrdersInfo()));

        // Restaurants table
        loadRestaurants(db);
    }

    @FXML
    private void loadRestaurants() {
        loadRestaurants(new DatabaseHelper());
    }

    private void loadRestaurants(DatabaseHelper db) {
        List<RestaurantOwner> owners = db.getAllRestaurantOwners();
        ObservableList<RestaurantOwner> data = FXCollections.observableArrayList(owners);
        restaurantsTable.setItems(data);
        if (totalRestaurantsLabel != null)
            totalRestaurantsLabel.setText("Total Registered Restaurants: " + data.size());
    }

    @FXML
    private void handleLogout() {
        navigateTo("/com/example/bhojhon/main-menu-view.fxml");
    }

    private void showApprovalDialog(RestaurantOwner owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Restaurant Owner");
        alert.setHeaderText("Add Restaurant Owner?");
        alert.setContentText("Do you want to add this restaurant owner, yes or no?\n\nRestaurant: " + owner.getRestaurantName() + "\nEmail: " + owner.getEmail());
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/example/bhojhon/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("fm-border-radius");
        dialogPane.setStyle("-fx-background-color: white; -fx-font-family: 'Plus Jakarta Sans'; -fx-font-size: 14px;");

        ButtonType btnYes = new ButtonType("Yes", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            DatabaseHelper db = new DatabaseHelper();
            boolean success = db.approveRestaurantOwner(owner.getId());
            if (success) {
                owner.setApproved(true);
                restaurantsTable.refresh();
            }
        }
    }
}
