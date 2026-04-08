package com.example.bhojhon.controller;

import com.example.bhojhon.data.CartManager;
import com.example.bhojhon.data.DataManager;
import com.example.bhojhon.exception.TrainSearchException;
import com.example.bhojhon.model.Train;
import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Train Search screen.
 * Allows users to search for trains by train number.
 */
public class TrainSearchController extends BaseController {

    @FXML
    private TextField trainNumberField;

    @FXML
    private Label trainNameLabel;

    @FXML
    private Label routeLabel;

    @FXML
    private Label stationCountLabel;

    private Train selectedTrain;

    @Override
    public void initialize() {
        // Clear any previous selections
        selectedTrain = null;
        trainNameLabel.setText("");
        routeLabel.setText("");
        stationCountLabel.setText("");
    }

    /**
     * Helper to clear train details
     */
    private void clearTrainDetails() {
        trainNameLabel.setText("");
        routeLabel.setText("");
        stationCountLabel.setText("");
    }

    /**
     * Validate train number input
     */
    private String validateTrainNumber() throws TrainSearchException {
        String trainNumber = trainNumberField.getText().trim();
        if (trainNumber.isEmpty()) {
            throw new TrainSearchException("Please enter a train number");
        }
        return trainNumber;
    }

    /**
     * Find train by number or throw exception
     */
    private Train findTrainByNumber(String trainNumber) throws TrainSearchException {
        Train train = DataManager.getInstance().getTrainByNumber(trainNumber);
        if (train == null) {
            throw new TrainSearchException("Train not found! Try: 101, 102, 201, or 202");
        }
        return train;
    }

    /**
     * Handle search button click
     */
    @FXML
    private void handleSearch() {
        try {
            String trainNumber = validateTrainNumber();
            selectedTrain = findTrainByNumber(trainNumber);

            trainNameLabel.setText(selectedTrain.getName());
            routeLabel.setText(selectedTrain.getRoute());
            stationCountLabel.setText(selectedTrain.getStations().size() + " stations");
            CartManager.getInstance().setSelectedTrainNumber(trainNumber);
        } catch (TrainSearchException e) {
            clearTrainDetails();
            selectedTrain = null;
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            clearTrainDetails();
            selectedTrain = null;
            showAlert("Unexpected error while searching train: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle view stations button click
     */
    @FXML
    private void handleViewStations() {
        if (selectedTrain == null) {
            showAlert("Please search for a train first", Alert.AlertType.WARNING);
            return;
        }

        // Pass selected train to next screen
        navigationManager.putData("selectedTrain", selectedTrain);
        goToStationList();
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
        alert.setTitle("Train Search");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
