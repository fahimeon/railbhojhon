package com.example.bhojhon.controller;

import com.example.bhojhon.data.DatabaseHelper;
import com.example.bhojhon.model.RestaurantOwner;
import com.example.bhojhon.model.Station;
import com.example.bhojhon.util.BaseController;
import com.example.bhojhon.util.RestaurantSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RestaurantAuthController extends BaseController {

    @FXML
    private TabPane tabPane;

    // Login fields
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginErrorLabel;

    // Register fields
    @FXML
    private TextField registerRestaurantNameField;
    @FXML
    private TextField registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private ComboBox<Station> stationComboBox;
    @FXML
    private Label registerErrorLabel;

    private DatabaseHelper dbHelper;

    @Override
    public void initialize() {
        dbHelper = new DatabaseHelper();
        loadStations();
    }

    private void loadStations() {
        List<Station> stations = dbHelper.getStations();
        stationComboBox.getItems().addAll(stations);
    }

    @FXML
    private void handleLogin() {
        loginErrorLabel.setText("");

        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please fill in all fields");
            return;
        }

        if (!isValidEmail(email)) {
            loginErrorLabel.setText("Please enter a valid email address");
            return;
        }

        // Authenticate
        RestaurantOwner owner = dbHelper.authenticateRestaurantOwner(email, password);

        if (owner != null) {
            // Set session
            RestaurantSession.getInstance().setCurrentOwner(owner);

            // Navigate to dashboard
            navigateTo("/com/example/bhojhon/restaurant-dashboard-view.fxml");
        } else {
            loginErrorLabel.setText("Invalid email or password");
        }
    }

    @FXML
    private void handleRegister() {
        registerErrorLabel.setText("");

        String restaurantName = registerRestaurantNameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        Station selectedStation = stationComboBox.getValue();

        // Validation
        if (restaurantName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || selectedStation == null) {
            registerErrorLabel.setText("Please fill in all fields");
            return;
        }

        if (!isValidEmail(email)) {
            registerErrorLabel.setText("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            registerErrorLabel.setText("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerErrorLabel.setText("Passwords do not match");
            return;
        }

        // Register
        boolean success = dbHelper.registerRestaurantOwner(
                restaurantName, email, password, selectedStation.getId());

        if (success) {
            showAlert("Success", "Restaurant Registered!",
                    "Your restaurant has been successfully registered and is now visible to passengers.");

            // Auto-login after registration
            RestaurantOwner owner = dbHelper.authenticateRestaurantOwner(email, password);
            if (owner != null) {
                RestaurantSession.getInstance().setCurrentOwner(owner);
                navigateTo("/com/example/bhojhon/restaurant-dashboard-view.fxml");
            }
        } else {
            registerErrorLabel.setText("Registration failed. Email may already be in use.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private Button loginToggleBtn;
    @FXML
    private Button registerToggleBtn;

    @FXML
    private void handleBack() {
        navigateTo("/com/example/bhojhon/main-menu-view.fxml");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @FXML
    public void switchToLogin() {
        tabPane.getSelectionModel().select(0);
        loginToggleBtn.getStyleClass().add("ra-toggle-active");
        registerToggleBtn.getStyleClass().remove("ra-toggle-active");
    }

    @FXML
    public void switchToRegister() {
        tabPane.getSelectionModel().select(1);
        registerToggleBtn.getStyleClass().add("ra-toggle-active");
        loginToggleBtn.getStyleClass().remove("ra-toggle-active");
    }
}
