package com.example.bhojhon.controller;

import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class UserFormController extends BaseController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField seatField;
    @FXML
    private TextField deliveryNoteField;
    @FXML
    private TextField pnrField;
    @FXML
    private TextField trainNumberField;
    @FXML
    private TextField journeyDateField; // kept for FXML compatibility
    @FXML
    private DatePicker journeyDatePicker;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleSubmit() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String seat = seatField.getText().trim();
        String note = deliveryNoteField.getText().trim();
        String pnr = pnrField.getText().trim();
        String trainNum = trainNumberField.getText().trim();

        // Read date from DatePicker; fall back to text field if needed
        String date = "";
        if (journeyDatePicker != null) {
            if (journeyDatePicker.getValue() != null) {
                date = journeyDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } else if (journeyDatePicker.getEditor().getText() != null && !journeyDatePicker.getEditor().getText().trim().isEmpty()) {
                date = journeyDatePicker.getEditor().getText().trim();
            }
        }
        if (date.isEmpty() && journeyDateField != null) {
            date = journeyDateField.getText().trim();
        }

        if (!validateInput(name, phone, email, seat, pnr, trainNum, date)) {
            return;
        }

        // Save to Database
        com.example.bhojhon.model.TicketInfo ticket = new com.example.bhojhon.model.TicketInfo(
                name, phone, pnr, trainNum, seat, date, note);
        new com.example.bhojhon.data.DatabaseHelper().saveTicket(ticket);

        // Save to CartManager for session
        com.example.bhojhon.data.CartManager cart = com.example.bhojhon.data.CartManager.getInstance();
        cart.setPassengerDetails(name, phone, seat, note, pnr, date, email);
        cart.setSelectedTrainNumber(trainNum);

        // Navigate to Train Selection
        navigateTo("/com/example/bhojhon/train-search-view.fxml");
    }

    @FXML
    private void handleBack() {
        goBack(); // Uses BaseController's goBack
    }

    @FXML
    private void handleAutoLogin() {
        String name = "Auto Passenger";
        String phone = "01700000000";
        String email = "auto@example.com";
        String seat = "KA-12";
        String pnr = "1234567890";
        String trainNum = "101";
        String date = "25-03-2026";
        String note = "Auto login note";
        
        com.example.bhojhon.model.TicketInfo ticket = new com.example.bhojhon.model.TicketInfo(
                name, phone, pnr, trainNum, seat, date, note);
        new com.example.bhojhon.data.DatabaseHelper().saveTicket(ticket);

        com.example.bhojhon.data.CartManager cart = com.example.bhojhon.data.CartManager.getInstance();
        cart.setPassengerDetails(name, phone, seat, note, pnr, date, email);
        cart.setSelectedTrainNumber(trainNum);

        navigateTo("/com/example/bhojhon/train-search-view.fxml");
    }

    private boolean validateInput(String name, String phone, String email, String seat, String pnr, String train,
            String date) {
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || seat.isEmpty() || pnr.isEmpty() || train.isEmpty()
                || date.isEmpty()) {
            showError("Please fill in all required fields.");
            return false;
        }

        // Phone Validation (simple digit check, 11 digits for BD generic)
        if (!phone.matches("\\d{11}")) {
            showError("Invalid phone number (must be 11 digits).");
            return false;
        }

        // Email Validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailRegex, email)) {
            showError("Invalid email address.");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
