package com.example.bhojhon.controller;

import com.example.bhojhon.data.DatabaseHelper;
import com.example.bhojhon.model.FoodItem;
import com.example.bhojhon.model.RestaurantOwner;
import com.example.bhojhon.util.BaseController;
import com.example.bhojhon.util.FileUtil;
import com.example.bhojhon.util.RestaurantSession;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RestaurantDashboardController extends BaseController {

    @FXML
    private Label restaurantNameLabel;
    @FXML
    private Label stationLabel;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label errorLabel;

    @FXML
    private TableView<FoodItem> menuTableView;
    @FXML
    private TableColumn<FoodItem, String> imageColumn;
    @FXML
    private TableColumn<FoodItem, String> nameColumn;
    @FXML
    private TableColumn<FoodItem, String> categoryColumn;
    @FXML
    private TableColumn<FoodItem, Double> priceColumn;
    @FXML
    private TableColumn<FoodItem, Void> actionColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> categoryComboBox;

    private DatabaseHelper dbHelper;
    private RestaurantOwner currentOwner;
    private File selectedImageFile;

    @Override
    public void initialize() {
        dbHelper = new DatabaseHelper();
        currentOwner = RestaurantSession.getInstance().getCurrentOwner();

        if (currentOwner != null) {
            restaurantNameLabel.setText(currentOwner.getRestaurantName());
            stationLabel.setText("Station: " + currentOwner.getStationName());

            setupTable();
            loadMenu();

            categoryComboBox.getItems().addAll("Main Course", "Side Dish", "Appetizer", "Dessert", "Beverage",
                    "Snacks");
        }
    }

    private void setupTable() {
        imageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImageUrl()));
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(url, true));
                        setGraphic(imageView);
                        setAlignment(Pos.CENTER);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText((empty || price == null) ? null : String.format("৳%.0f", price));
            }
        });

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Remove");
            {
                deleteBtn.setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #b71c1c; -fx-font-size: 11px;");
                deleteBtn.setOnAction(event -> {
                    FoodItem item = getTableRow().getItem();
                    if (item != null) {
                        handleDeleteItem(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    setGraphic(deleteBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    @FXML
    private void loadMenu() {
        menuTableView.getItems().clear();
        List<FoodItem> items = dbHelper.getFoodItemsByRestaurantId(currentOwner.getId());
        menuTableView.getItems().addAll(items);
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Food Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            fileNameLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleAddFoodItem() {
        errorLabel.setText("");
        String name = nameField.getText().trim();
        String category = categoryComboBox.getValue();
        String priceText = priceField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty() || category == null || priceText.isEmpty() || selectedImageFile == null) {
            errorLabel.setText("Please fill in all fields and select an image.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            String imageUrl = FileUtil.uploadImage(selectedImageFile);

            FoodItem item = new FoodItem(0, name, currentOwner.getId(), price, category, description, imageUrl);
            if (dbHelper.saveFoodItem(item)) {
                loadMenu();
                clearForm();
            } else {
                errorLabel.setText("Failed to save item to database.");
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid price format.");
        } catch (IOException e) {
            errorLabel.setText("Failed to upload image.");
            e.printStackTrace();
        }
    }

    private void handleDeleteItem(FoodItem item) {
        if (dbHelper.deleteFoodItem(item.getId())) {
            loadMenu();
        }
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        descriptionField.clear();
        categoryComboBox.setValue(null);
        fileNameLabel.setText("No file selected");
        selectedImageFile = null;
    }

    @FXML
    private void handleNavigateToProfile() {
        navigateTo("/com/example/bhojhon/restaurant-profile-view.fxml");
    }

    @FXML
    private void handleBack() {
        RestaurantSession.getInstance().setCurrentOwner(null);
        navigateTo("/com/example/bhojhon/restaurant-auth-view.fxml");
    }
}
