package com.example.bhojhon.controller;

import com.example.bhojhon.data.DatabaseHelper;
import com.example.bhojhon.model.RestaurantOwner;
import com.example.bhojhon.util.BaseController;
import com.example.bhojhon.util.RestaurantSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RestaurantProfileController extends BaseController {

    @FXML private Label restaurantNameLabel;
    @FXML private Label stationLabel;
    @FXML private Label statusLabel;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private DatabaseHelper dbHelper;
    private RestaurantOwner currentOwner;

    @Override
    public void initialize() {
        dbHelper = new DatabaseHelper();
        currentOwner = RestaurantSession.getInstance().getCurrentOwner();

        if (currentOwner != null) {
            updateHeaderLabels();
            nameField.setText(currentOwner.getRestaurantName());
            emailField.setText(currentOwner.getEmail());
        }
    }

    private void updateHeaderLabels() {
        restaurantNameLabel.setText(currentOwner.getRestaurantName());
        stationLabel.setText("Station: " + currentOwner.getStationName());
    }

    @FXML
    private void handleSaveProfile() {
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: #ef4444;"); // Error red default

        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            statusLabel.setText("Name and email cannot be empty.");
            return;
        }

        boolean success = dbHelper.updateRestaurantOwnerProfile(currentOwner.getId(), newName, newEmail, newPassword);

        if (success) {
            // Update the session explicitly:
            currentOwner.setRestaurantName(newName);
            currentOwner.setEmail(newEmail.toLowerCase());
            
            statusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 14px; -fx-font-weight: bold;"); // Success green
            statusLabel.setText("Profile updated successfully!");
            updateHeaderLabels(); // Reflect new name in header instantly
        } else {
            statusLabel.setText("Failed to update. Email might already be in use.");
        }
    }

    @FXML
    private void handleNavigateToDashboard() {
        navigateTo("/com/example/bhojhon/restaurant-dashboard-view.fxml");
    }

    @FXML
    private void handleBack() {
        RestaurantSession.getInstance().setCurrentOwner(null);
        navigateTo("/com/example/bhojhon/restaurant-auth-view.fxml");
    }
}
