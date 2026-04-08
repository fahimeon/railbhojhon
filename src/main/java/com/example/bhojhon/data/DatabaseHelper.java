package com.example.bhojhon.data;

import com.example.bhojhon.model.TicketInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper class for SQLite database operations.
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:railkhabar.db";

    public DatabaseHelper() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id TEXT," +
                "ticket_number TEXT," +
                "train_number TEXT," +
                "name TEXT," +
                "phone TEXT," +
                "seat TEXT," +
                "journey_date TEXT," +
                "delivery_note TEXT," +
                "station_name TEXT," +
                "order_items TEXT," +
                "total_amount REAL," +
                "order_date TEXT" +
                ");";

        String createStationsTableSQL = "CREATE TABLE IF NOT EXISTS stations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "code TEXT NOT NULL" +
                ");";

        String createRestaurantOwnersTableSQL = "CREATE TABLE IF NOT EXISTS restaurant_owners (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "restaurant_name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "station_id INTEGER NOT NULL," +
                "created_at TEXT NOT NULL," +
                "is_approved INTEGER DEFAULT 0," +
                "FOREIGN KEY (station_id) REFERENCES stations(id)" +
                ");";

        String createFoodItemsTableSQL = "CREATE TABLE IF NOT EXISTS food_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "restaurant_id INTEGER NOT NULL," +
                "price REAL NOT NULL," +
                "category TEXT," +
                "description TEXT," +
                "image_url TEXT," +
                "FOREIGN KEY (restaurant_id) REFERENCES restaurant_owners(id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {
            stmt.execute(createOrdersTableSQL);
            stmt.execute(createStationsTableSQL);
            stmt.execute(createRestaurantOwnersTableSQL);

            // Allow adding column to existing database
            try {
                stmt.execute("ALTER TABLE restaurant_owners ADD COLUMN is_approved INTEGER DEFAULT 0");
            } catch (SQLException e) {
                // Column might already exist, ignore
            }

            stmt.execute(createFoodItemsTableSQL);

            // Populate stations if empty
            populateStations(conn);

            System.out.println("Database initialized and tables checked/created.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateStations(Connection conn) throws SQLException {
        // Check if stations already exist
        String checkSQL = "SELECT COUNT(*) FROM stations";
        try (Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(checkSQL)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Stations already populated
            }
        }

        // Insert predefined stations
        String insertSQL = "INSERT INTO stations(name, code) VALUES(?, ?)";
        String[][] stations = {
                { "Dhaka Kamalapur", "DKA" },
                { "Chittagong", "CTG" },
                { "Comilla", "CML" },
                { "Feni", "FEN" },
                { "Laksam", "LKS" },
                { "Rajshahi", "RJH" },
                { "Ishwardi", "ISH" },
                { "Pabna", "PAB" },
                { "Tangail", "TNG" }
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (String[] station : stations) {
                pstmt.setString(1, station[0]);
                pstmt.setString(2, station[1]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Stations populated successfully.");
        }
    }

    public boolean saveTicket(TicketInfo ticket) {
        String insertSQL = "INSERT INTO orders(ticket_number, train_number, name, phone, seat, journey_date, delivery_note) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, ticket.getPnr());
            pstmt.setString(2, ticket.getTrainNumber());
            pstmt.setString(3, ticket.getPassengerName());
            pstmt.setString(4, ticket.getPhoneNumber());
            pstmt.setString(5, ticket.getSeatNumber());
            pstmt.setString(6, ticket.getJourneyDate());
            pstmt.setString(7, ticket.getDeliveryNote());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<TicketInfo> getAllOrders() {
        java.util.List<TicketInfo> orders = new java.util.ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                TicketInfo ticket = new TicketInfo(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("ticket_number"),
                        rs.getString("train_number"),
                        rs.getString("seat"),
                        rs.getString("journey_date"),
                        rs.getString("delivery_note"));
                orders.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean saveOrder(String orderId, String name, String phone, String pnr, String trainNumber,
            String seat, String journeyDate, String deliveryNote, String stationName,
            String orderItems, double totalAmount, String orderDate) {
        String insertSQL = "INSERT INTO orders(order_id, ticket_number, train_number, name, phone, seat, " +
                "journey_date, delivery_note, station_name, order_items, total_amount, order_date) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, orderId);
            pstmt.setString(2, pnr);
            pstmt.setString(3, trainNumber);
            pstmt.setString(4, name);
            pstmt.setString(5, phone);
            pstmt.setString(6, seat);
            pstmt.setString(7, journeyDate);
            pstmt.setString(8, deliveryNote);
            pstmt.setString(9, stationName);
            pstmt.setString(10, orderItems);
            pstmt.setDouble(11, totalAmount);
            pstmt.setString(12, orderDate);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Order saved: " + orderId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<com.example.bhojhon.model.OrderInfo> getAllOrdersInfo() {
        java.util.List<com.example.bhojhon.model.OrderInfo> orders = new java.util.ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                com.example.bhojhon.model.OrderInfo order = new com.example.bhojhon.model.OrderInfo(
                        rs.getString("order_id") != null ? rs.getString("order_id") : "N/A",
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("train_number"),
                        rs.getString("seat"),
                        rs.getString("station_name") != null ? rs.getString("station_name") : "N/A",
                        rs.getString("order_items") != null ? rs.getString("order_items") : "N/A",
                        rs.getDouble("total_amount"),
                        rs.getString("order_date") != null ? rs.getString("order_date") : "N/A");
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    // ========== RESTAURANT OWNER METHODS ==========

    /**
     * Get all registered restaurant owners (for Admin Dashboard)
     */
    public java.util.List<com.example.bhojhon.model.RestaurantOwner> getAllRestaurantOwners() {
        java.util.List<com.example.bhojhon.model.RestaurantOwner> list = new java.util.ArrayList<>();
        String query = "SELECT ro.id, ro.restaurant_name, ro.email, ro.password, " +
                "ro.station_id, s.name as station_name, ro.created_at, ro.is_approved " +
                "FROM restaurant_owners ro " +
                "JOIN stations s ON ro.station_id = s.id " +
                "ORDER BY ro.created_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new com.example.bhojhon.model.RestaurantOwner(
                        rs.getInt("id"),
                        rs.getString("restaurant_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("created_at"),
                        rs.getInt("is_approved") == 1));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all restaurant owners: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get all stations
     */
    public java.util.List<com.example.bhojhon.model.Station> getStations() {
        java.util.List<com.example.bhojhon.model.Station> stations = new java.util.ArrayList<>();
        String query = "SELECT * FROM stations ORDER BY name";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                com.example.bhojhon.model.Station station = new com.example.bhojhon.model.Station(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getString("name")); // Using name as city for simplicity
                stations.add(station);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stations: " + e.getMessage());
            e.printStackTrace();
        }
        return stations;
    }

    /**
     * Get registered restaurants for a specific station
     */
    public java.util.List<com.example.bhojhon.model.Restaurant> getRegisteredRestaurantsByStationId(int stationId) {
        java.util.List<com.example.bhojhon.model.Restaurant> restaurants = new java.util.ArrayList<>();
        String query = "SELECT * FROM restaurant_owners WHERE station_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, stationId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Mapping RestaurantOwner to Restaurant model for the UI
                // We use +1000 to avoid ID conflicts with hardcoded ones in DataManager
                restaurants.add(new com.example.bhojhon.model.Restaurant(
                        rs.getInt("id") + 1000,
                        rs.getString("restaurant_name"),
                        rs.getInt("station_id"),
                        "Registered Kitchen", // Default cuisine
                        4.5 // Default rating
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching registered restaurants: " + e.getMessage());
            e.printStackTrace();
        }
        return restaurants;
    }

    /**
     * Register a new restaurant owner
     */
    public boolean registerRestaurantOwner(String restaurantName, String email, String password, int stationId) {
        String insertSQL = "INSERT INTO restaurant_owners(restaurant_name, email, password, station_id, created_at, is_approved) " +
                "VALUES(?, ?, ?, ?, ?, 0)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, restaurantName);
            pstmt.setString(2, email.toLowerCase());
            pstmt.setString(3, hashPassword(password));
            pstmt.setInt(4, stationId);
            pstmt.setString(5, java.time.LocalDateTime.now().toString());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Restaurant owner registered: " + email);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering restaurant owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticate restaurant owner
     */
    public com.example.bhojhon.model.RestaurantOwner authenticateRestaurantOwner(String email, String password) {
        String query = "SELECT ro.*, s.name as station_name FROM restaurant_owners ro " +
                "JOIN stations s ON ro.station_id = s.id " +
                "WHERE ro.email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email.toLowerCase());
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(hashPassword(password))) {
                    return new com.example.bhojhon.model.RestaurantOwner(
                            rs.getInt("id"),
                            rs.getString("restaurant_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("station_id"),
                            rs.getString("station_name"),
                            rs.getString("created_at"),
                            rs.getInt("is_approved") == 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating restaurant owner: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Approve restaurant owner
     */
    public boolean approveRestaurantOwner(int ownerId) {
        String updateSQL = "UPDATE restaurant_owners SET is_approved = 1 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, ownerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error approving restaurant owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update restaurant owner profile
     */
    public boolean updateRestaurantOwnerProfile(int id, String restaurantName, String email, String newPassword) {
        String updateSQL;
        boolean updatePassword = newPassword != null && !newPassword.trim().isEmpty();
        if (updatePassword) {
            updateSQL = "UPDATE restaurant_owners SET restaurant_name=?, email=?, password=? WHERE id=?";
        } else {
            updateSQL = "UPDATE restaurant_owners SET restaurant_name=?, email=? WHERE id=?";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, restaurantName);
            pstmt.setString(2, email.toLowerCase());
            
            if (updatePassword) {
                pstmt.setString(3, hashPassword(newPassword));
                pstmt.setInt(4, id);
            } else {
                pstmt.setInt(3, id);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating restaurant owner profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count total registered restaurants (for Admin stat cards)
     */
    public int getRestaurantCount() {
        String query = "SELECT COUNT(*) FROM restaurant_owners";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting restaurants: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Count total orders (for Admin stat cards)
     */
    public int getOrderCount() {
        String query = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting orders: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Count total stations (for Admin stat cards)
     */
    public int getStationCount() {
        String query = "SELECT COUNT(*) FROM stations";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting stations: " + e.getMessage());
        }
        return 0;
    }

    // ========== FOOD ITEM METHODS ==========

    /**
     * Save a new food item or update existing one
     */
    public boolean saveFoodItem(com.example.bhojhon.model.FoodItem item) {
        String insertSQL = "INSERT INTO food_items(name, restaurant_id, price, category, description, image_url) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE food_items SET name=?, price=?, category=?, description=?, image_url=? WHERE id=?";

        boolean isUpdate = item.getId() > 0 && item.getId() < 1000; // IDs >= 1000 are likely hardcoded/temporary

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(isUpdate ? updateSQL : insertSQL)) {

            if (isUpdate) {
                pstmt.setString(1, item.getName());
                pstmt.setDouble(2, item.getPrice());
                pstmt.setString(3, item.getCategory());
                pstmt.setString(4, item.getDescription());
                pstmt.setString(5, item.getImageUrl());
                pstmt.setInt(6, item.getId());
            } else {
                pstmt.setString(1, item.getName());
                pstmt.setInt(2, item.getRestaurantId());
                pstmt.setDouble(3, item.getPrice());
                pstmt.setString(4, item.getCategory());
                pstmt.setString(5, item.getDescription());
                pstmt.setString(6, item.getImageUrl());
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving food item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get food items for a specific restaurant
     */
    public java.util.List<com.example.bhojhon.model.FoodItem> getFoodItemsByRestaurantId(int restaurantId) {
        java.util.List<com.example.bhojhon.model.FoodItem> items = new java.util.ArrayList<>();

        // Adjust for UI offset if necessary (registered restaurants have +1000 offset
        // in UI/DataManager)
        int actualDbId = restaurantId >= 1000 ? restaurantId - 1000 : restaurantId;

        String query = "SELECT * FROM food_items WHERE restaurant_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, actualDbId);
            java.sql.ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new com.example.bhojhon.model.FoodItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("restaurant_id"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("image_url")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching food items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Delete a food item
     */
    public boolean deleteFoodItem(int itemId) {
        String deleteSQL = "DELETE FROM food_items WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting food item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Simple password hashing (for demo purposes)
     */
    private String hashPassword(String password) {
        // Simple hash for demo - in production use BCrypt or similar
        return Integer.toString(password.hashCode());
    }
}
