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
        loginErrorLabel.setVisible(false);

        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please fill in all fields");
            loginErrorLabel.setVisible(true);
            return;
        }

        if (!isValidEmail(email)) {
            loginErrorLabel.setText("Please enter a valid email address");
            loginErrorLabel.setVisible(true);
            return;
        }

        // Authenticate
        RestaurantOwner owner = dbHelper.authenticateRestaurantOwner(email, password);

        if (owner != null) {
            if (!owner.isApproved()) {
                loginErrorLabel.setText("waiting for approval");
                loginErrorLabel.setVisible(true);
                return;
            }

            // Set session
            RestaurantSession.getInstance().setCurrentOwner(owner);

            // Navigate to dashboard
            navigateTo("/com/example/bhojhon/restaurant-dashboard-view.fxml");
        } else {
            loginErrorLabel.setText("Invalid email or password");
            loginErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegister() {
        registerErrorLabel.setText("");
        registerErrorLabel.setVisible(false);

        String restaurantName = registerRestaurantNameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        Station selectedStation = stationComboBox.getValue();

        // Validation
        if (restaurantName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || selectedStation == null) {
            registerErrorLabel.setText("Please fill in all fields");
            registerErrorLabel.setVisible(true);
            return;
        }

        if (!isValidEmail(email)) {
            registerErrorLabel.setText("Please enter a valid email address");
            registerErrorLabel.setVisible(true);
            return;
        }

        if (password.length() < 6) {
            registerErrorLabel.setText("Password must be at least 6 characters");
            registerErrorLabel.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerErrorLabel.setText("Passwords do not match");
            registerErrorLabel.setVisible(true);
            return;
        }

        // Register
        boolean success = dbHelper.registerRestaurantOwner(
                restaurantName, email, password, selectedStation.getId());

        if (success) {
            showAlert("Success", "Registration Pending", "waiting for approval");

            // Switch to login tab after registration
            switchToLogin();
            loginEmailField.setText(email);
            loginPasswordField.setText("");
        } else {
            registerErrorLabel.setText("Registration failed. Email may already be in use or Database Error.");
            registerErrorLabel.setVisible(true);
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
        loginToggleBtn.getStyleClass().add("login-toggle-active");
        registerToggleBtn.getStyleClass().remove("login-toggle-active");
        loginErrorLabel.setVisible(false);
        registerErrorLabel.setVisible(false);
    }

    @FXML
    public void switchToRegister() {
        tabPane.getSelectionModel().select(1);
        registerToggleBtn.getStyleClass().add("login-toggle-active");
        loginToggleBtn.getStyleClass().remove("login-toggle-active");
        loginErrorLabel.setVisible(false);
        registerErrorLabel.setVisible(false);
    }
}
