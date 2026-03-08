package com.example.bhojhon.controller;

import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminLoginController extends BaseController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize() {
        try {
            errorLabel.setVisible(false);
        } catch (Exception e) {
            System.err.println("Initialization error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {

        try {
            String email = emailField.getText() != null ? emailField.getText().trim() : "";
            String pass = passwordField.getText() != null ? passwordField.getText() : "";

            // Basic validation
            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Email and password are required.");
                errorLabel.setVisible(true);
                return;
            }

            if (email.equals("admin@gmail.com") && pass.equals("admin@gmail.com")) {
                navigateTo("/com/example/bhojhon/admin-dashboard-view.fxml", "Admin Dashboard");
            } else {
                errorLabel.setText("Invalid credentials.");
                errorLabel.setVisible(true);
            }

        } catch (Exception e) {
            errorLabel.setText("Something went wrong. Please try again.");
            errorLabel.setVisible(true);
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            goBack();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
}