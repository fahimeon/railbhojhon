package com.example.bhojhon.controller;

import com.example.bhojhon.data.CartManager;
import com.example.bhojhon.data.DataManager;
import com.example.bhojhon.model.FoodItem;
import com.example.bhojhon.model.Restaurant;
import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class FoodMenuController extends BaseController {

    @FXML
    private Label restaurantInfoLabel;

    @FXML
    private FlowPane menuGrid;

    @FXML
    private Label cartCountLabel;

    private Restaurant selectedRestaurant;

    @Override
    public void initialize() {
        selectedRestaurant = (Restaurant) navigationManager.getData("selectedRestaurant");

        if (selectedRestaurant != null) {
            restaurantInfoLabel.setText(selectedRestaurant.getName() + " - " + selectedRestaurant.getCuisine());
            refreshGrid();
        }

        updateCartCount();
    }

    private void refreshGrid() {
        if (selectedRestaurant != null) {
            List<FoodItem> foodItems = DataManager.getInstance().getFoodItemsByRestaurantId(selectedRestaurant.getId());
            menuGrid.getChildren().clear();
            for (FoodItem item : foodItems) {
                menuGrid.getChildren().add(createFoodCard(item));
            }
            updateCartCount();
        }
    }

    private VBox createFoodCard(FoodItem item) {
        VBox card = new VBox(12);
        card.getStyleClass().add("fm-food-card");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(130);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(false);
        String url = item.getImageUrl();
        if (url != null && !url.isEmpty()) {
            try {
                imageView.setImage(new Image(url, true));
            } catch (Exception e) {
                // Ignore missing images natively
            }
        }
        
        // Wrap image safely with subtle clip styling
        VBox imageWrapper = new VBox(imageView);
        imageWrapper.setAlignment(Pos.CENTER);
        imageWrapper.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8px;");
        imageWrapper.setMinHeight(130);

        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        nameLabel.setWrapText(true);

        Label descLabel = new Label(item.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        descLabel.setWrapText(true);
        descLabel.setMinHeight(40);
        descLabel.setMaxHeight(40);

        Label priceLabel = new Label(item.getFormattedPrice());
        priceLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: #16a34a;");

        HBox actionBox = createActionBox(item);

        HBox bottomBox = new HBox(10, priceLabel, new Region(), actionBox);
        HBox.setHgrow(bottomBox.getChildren().get(1), Priority.ALWAYS);
        bottomBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageWrapper, nameLabel, descLabel, bottomBox);
        return card;
    }

    private HBox createActionBox(FoodItem item) {
        int qty = CartManager.getInstance().getQuantity(item);
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);

        if (qty > 0) {
            Button minusBtn = new Button("-");
            String btnStyle = "-fx-min-width: 32px; -fx-min-height: 32px; -fx-background-radius: 50%; -fx-font-weight: bold; -fx-cursor: hand;";
            minusBtn.setStyle(btnStyle + "-fx-background-color: #fee2e2; -fx-text-fill: #ef4444;");
            minusBtn.setOnAction(e -> {
                CartManager.getInstance().addItem(item, -1);
                refreshGrid();
            });

            Label qtyLabel = new Label(String.valueOf(qty));
            qtyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e293b;");

            Button plusBtn = new Button("+");
            plusBtn.setStyle(btnStyle + "-fx-background-color: #dcfce7; -fx-text-fill: #22c55e;");
            plusBtn.setOnAction(e -> {
                CartManager.getInstance().addItem(item, 1);
                refreshGrid();
            });

            box.getChildren().addAll(minusBtn, qtyLabel, plusBtn);
        } else {
            Button addBtn = new Button("Add to Cart");
            addBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8 16; -fx-cursor: hand;");
            addBtn.setOnAction(e -> {
                CartManager.getInstance().addItem(item, 1);
                refreshGrid();
            });
            box.getChildren().add(addBtn);
        }
        return box;
    }

    private void updateCartCount() {
        int count = CartManager.getInstance().getItemCount();
        cartCountLabel.setText("Cart: " + count + " items");
    }

    @FXML
    private void handleViewCart() {
        if (CartManager.getInstance().isEmpty()) {
            showAlert("Your cart is empty", Alert.AlertType.WARNING);
            return;
        }
        goToCart();
    }

    @FXML
    private void handleBack() {
        goBack();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Food Menu");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
