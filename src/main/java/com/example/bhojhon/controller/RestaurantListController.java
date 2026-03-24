package com.example.bhojhon.controller;

import com.example.bhojhon.data.DataManager;
import com.example.bhojhon.model.Restaurant;
import com.example.bhojhon.model.Station;
import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controller for the Restaurant List screen.
 * Displays restaurants available at the selected station.
 */
public class RestaurantListController extends BaseController {

    @FXML
    private Label stationInfoLabel;

    @FXML
    private ListView<Restaurant> restaurantListView;

    private Station selectedStation;

    @Override
    public void initialize() {

        selectedStation = (Station) navigationManager.getData("selectedStation");

        if (selectedStation != null) {
            stationInfoLabel.setText("Restaurants at " + selectedStation.getName());

            var restaurants = DataManager.getInstance()
                    .getRestaurantsByStationId(selectedStation.getId());

            restaurantListView.getItems().addAll(restaurants);

            // Custom cell UI for better visual experience
            restaurantListView.setCellFactory(listView -> new ListCell<>() {

                @Override
                protected void updateItem(Restaurant restaurant, boolean empty) {
                    super.updateItem(restaurant, empty);

                    if (empty || restaurant == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    // Restaurant Name (Large + Bold)
                    Label nameLabel = new Label(restaurant.getName());
                    nameLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

                    // Cuisine / Type (Secondary text)
                    Label cuisineLabel = new Label(restaurant.getCuisine());
                    cuisineLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

                    // Rating (Highlighted)
                    Label ratingLabel = new Label("★ " + restaurant.getRating());
                    ratingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f59e0b;");

                    VBox textBox = new VBox(4, nameLabel, cuisineLabel);
                    HBox.setHgrow(textBox, Priority.ALWAYS);

                    HBox row = new HBox(12, textBox, ratingLabel);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    row.getStyleClass().add("station-box");

                    setGraphic(row);
                    setText(null);
                }
            });

            // Add single-click listener
            restaurantListView.setOnMouseClicked(event -> {
                if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 1) {
                    handleSelectRestaurant();
                }
            });
        }
    }

    /**
     * Handle restaurant selection
     */
    @FXML
    private void handleSelectRestaurant() {
        Restaurant selectedRestaurant = restaurantListView.getSelectionModel().getSelectedItem();

        if (selectedRestaurant == null) {
            showAlert("Please select a restaurant", Alert.AlertType.WARNING);
            return;
        }

        navigationManager.putData("selectedRestaurant", selectedRestaurant);
        goToFoodMenu();
    }

    /**
     * Handle back button
     */
    /**
     * Handle back button
     */
    @FXML
    private void handleBack() {
        goBack();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Restaurant Selection");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
