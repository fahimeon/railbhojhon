package com.example.bhojhon.model;

/**
 * Model class for restaurant owner accounts
 */
public class RestaurantOwner {
    private int id;
    private String restaurantName;
    private String email;
    private String password;
    private int stationId;
    private String stationName;
    private String createdAt;
    private boolean isApproved;

    public RestaurantOwner(int id, String restaurantName, String email, String password,
            int stationId, String stationName, String createdAt, boolean isApproved) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.email = email;
        this.password = password;
        this.stationId = stationId;
        this.stationName = stationName;
        this.createdAt = createdAt;
        this.isApproved = isApproved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
