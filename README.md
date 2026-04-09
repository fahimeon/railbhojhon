# BhojonOnRails - Train Food Pre-Order System

Welcome to **BhojonOnRails**, a JavaFX-based desktop application designed to facilitate food pre-ordering for train passengers in Bangladesh. This system allows users to search for trains, view routes, select stations, and order food from available restaurants.

## 🚀 Project Overview

**BhojonOnRails** serves as a digital platform connecting train passengers with local restaurants at competitive prices. The application is built with a focus on a clean user interface, seamless navigation, and robust architectural principles.

### Key Features
*   **Train Search**: Search for trains by number (e.g., "101", "102").
*   **Station Selection**: View available stations on a train's route.
*   **Restaurant Browsing**: Browse restaurants available at specific stations.
*   **Food Menu**: View categorized food items with prices in BDT.
*   **Cart Management**: specialized cart system for managing orders.
*   **Order Confirmation**: Review and confirm your meal pre-orders.

## 🛠 Technology Stack

*   **Language**: Java 17+
*   **UI Framework**: JavaFX (with FXML)
*   **Build Tool**: Maven
*   **Styling**: CSS
*   **Architecture**: MVC (Model-View-Controller)

---

## 🏛 Object-Oriented Programming (OOP) Features

This project strictly adheres to OOP principles to ensure maintainability and scalability.

### 1. Encapsulation
Data hiding is implemented across all Model classes. Fields are kept `private` to restrict direct access, and `public` getters/setters are provided.
*   **Example**: In `Train.java`, fields like `trainNumber` and `route` are private. Access is controlled via `getTrainNumber()` or `setTrainNumber()`.

### 2. Inheritance
A hierarchical structure is used to promote code reuse.
*   **Example**: `Train` and `FoodItem` both extend the `BaseEntity` class. This allows them to inherit common properties like `id` and `name` without rewriting code.

### 3. Polymorphism
Objects are treated as instances of their parent class but behave according to their specific implementation.
*   **Example**: The `getDisplayInfo()` method is defined in `BaseEntity` but overridden in subclasses like `Train` and `FoodItem`. Calling this method on a generic `BaseEntity` reference triggers the specific behavior of the actual object (showing train details vs. food details).

### 4. Abstraction
Complexity is hidden by exposing only essential details.
*   **Example**: `BaseEntity` is an **abstract class**. It defines the structure (like `id` and `name`) and enforces that all subclasses must implement `getDisplayInfo()`, but it cannot be instantiated directly. This abstracts the concept of a generic "Entity".

---

## 🏗 MVC Architecture

The application follows the **Model-View-Controller (MVC)** design pattern, separating concerns into three distinct layers.

### 1. Model (`com.example.bhojhon.model`)
Represents the data and business logic of the application.
*   **Classes**: `Train`, `Station`, `Restaurant`, `FoodItem`, `CartItem`.
*   **Role**: These classes hold data (e.g., a Train's route) and have no knowledge of the UI.
*   **Data Management**: `DataManager` (Singleton) acts as a central repository to fetch and manage these models.

### 2. View (`src/main/resources/.../*.fxml`)
Represents the user interface (UI).
*   **Files**: `welcome-view.fxml`, `train-search-view.fxml`, `food-menu-view.fxml`, etc.
*   **Role**: Defines the layout and appearance of screens using FXML. It contains no business logic.

### 3. Controller (`com.example.bhojhon.controller`)
Acts as the intermediary between Model and View.
*   **Classes**: `TrainSearchController`, `RestaurantListController`, etc.
*   **Role**:
    *   Listens to user actions (clicks, inputs) from the **View**.
    *   Updates the **Model** (e.g., adds item to Cart).
    *   Refreshes the **View** with new data (e.g., shows list of trains).

### How it ties together:
1.  **User** enters "101" in `TrainSearchController` (View).
2.  **Controller** asks `DataManager` (Model) for the train with ID "101".
3.  **Model** returns user the `Train` object.
4.  **Controller** updates the labels on the screen (View) with the train's details.

## 🧭 Navigation System

The application uses a centralized navigation system to manage screen transitions and data passing.

1.  **NavigationManager (Singleton)**:
    *   This class (`com.example.bhojhon.util.NavigationManager`) manages the primary `Stage` of the JavaFX application.
    *   It handles loading FXML files, setting scenes, and applying the global CSS stylesheet.
    *   **Data Passing**: It maintains a `sharedData` map (Key-Value pairs) to pass objects (like `selectedTrain`) between different screens.

2.  **BaseController (Abstract Parent)**:
    *   All controllers extend `BaseController`.
    *   It provides ready-to-use methods like `goToTrainSearch()`, `goToStationList()`, etc.
    *   This means any controller can switch screens without duplicating navigation logic.

**Example Flow**:
When a user searches for a train and clicks "Next":
1.  `TrainSearchController` puts the train object into `NavigationManager` (`navManager.putData("selectedTrain", train)`).
2.  It calls `goToStationList()`.
3.  `NavigationManager` loads the Station List view.
4.  `StationListController` retrieves the train object from `NavigationManager` (`navManager.getData("selectedTrain")`) to show relevant stations.

### 📍 Main Entry Point
The navigation starts in the main application class: **`HelloApplication.java`**.
*   It gets the singleton instance of `NavigationManager`.
*   It sets the primary `Stage` (window) for the manager.
*   It triggers the first navigation to the "Welcome" screen.

```java
// Inside HelloApplication.java
NavigationManager.getInstance().setPrimaryStage(stage);
NavigationManager.getInstance().navigateTo("/com/example/bhojhon/welcome-view.fxml", ...);
```

---

## 🔄 How Everything is Working

The application flow is linear and intuitive:

1.  **Splash/Welcome**: The app starts with a welcome screen.
2.  **Train Search**: User enters a train number. The system validates it against the `DataManager`.
3.  **Route/Station**: If found, the user views the route and selects a station where they want food delivered.
4.  **Restaurant List**: Based on the selected station, available restaurants are displayed.
5.  **Food Menu**: Selecting a restaurant shows its menu. Users can add items to their **Cart**.
6.  **Cart & Checkout**: `CartManager` tracks user selections. The user reviews the total cost and places the order.
7.  **Order Confirmation**: A unique Order ID is generated, and the order is saved (in memory for MVP).

## 💻 How to Run

1.  Open the project in **IntelliJ IDEA**.
2.  Ensure **Java 17 JDK** is configured.
3.  Load the **Maven** changes to download dependencies.
4.  Run the application using the `HelloApplication` class or main wrapper.
    ```bash
    mvn javafx:run
    ```

---
*Developed for the Advanced Agentic Coding Project.*
