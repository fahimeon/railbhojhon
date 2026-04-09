package com.example.bhojhon.util;

/**
 * Abstract base controller class.
 * Demonstrates INHERITANCE and provides common navigation functionality.
 * All screen controllers will extend this base class.
 */
public abstract class BaseController {
    protected NavigationManager navigationManager = NavigationManager.getInstance();

    /**
     * Set navigation manager reference (optional given singleton usage)
     */
    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    /**
     * Navigate to welcome screen
     */
    protected void goToWelcome() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/welcome-view.fxml",
                "Train Food Pre-Order System - Bangladesh");
        // Welcome is usually the root, so good practice to clear history or manage it.
        // For now, if we go to welcome explicitly, we might want to clear history if
        // it's a "Logout" action,
        // but if it's just navigation, we keep it. However, keeping history when going
        // to "Home" can be confusing.
        // Let's assume explicit "Home" navigation should reset flow usually, or we just
        // push it.
        // For "Back" button implementation, we use goBack().
    }

    /**
     * Navigate back to the previous screen
     */
    protected void goBack() {
        navigationManager.goBack();
    }

    /**
     * Navigate to train search screen
     */
    protected void goToTrainSearch() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/train-search-view.fxml",
                "Search Train");
    }

    /**
     * Navigate to station list screen
     */
    protected void goToStationList() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/station-list-view.fxml",
                "Select Station");
    }

    /**
     * Navigate to restaurant list screen
     */
    protected void goToRestaurantList() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/restaurant-list-view.fxml",
                "Select Restaurant");
    }

    /**
     * Navigate to food menu screen
     */
    protected void goToFoodMenu() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/food-menu-view.fxml",
                "Food Menu");
    }

    /**
     * Navigate to cart screen
     */
    protected void goToCart() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/cart-view.fxml",
                "Shopping Cart");
    }

    /**
     * Navigate to order confirmation screen
     */
    protected void goToOrderConfirmation() {
        navigationManager.navigateTo(
                "/com/example/bhojhon/order-confirmation-view.fxml",
                "Order Confirmed");
    }

    /**
     * Navigate to any FXML file with a default title.
     * 
     * @param fxmlPath Path to the FXML file
     */
    protected void navigateTo(String fxmlPath) {
        if (navigationManager != null) {
            navigationManager.navigateTo(fxmlPath, "BhojonOnRails");
        }
    }

    /**
     * Navigate to any FXML file with a custom title.
     * 
     * @param fxmlPath Path to the FXML file
     * @param title    Title of the window
     */
    protected void navigateTo(String fxmlPath, String title) {
        if (navigationManager != null) {
            navigationManager.navigateTo(fxmlPath, title);
        }
    }

    /**
     * Abstract method to be implemented by each controller
     * Called when screen is initialized
     */
    public abstract void initialize();
}
