package com.example.bhojhon.data;

import com.example.bhojhon.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;

    private List<Train> trains;
    private List<Station> stations;
    private List<Restaurant> restaurants;
    private List<FoodItem> foodItems;

    private DataManager() {
        initializeData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initializeData() {
        initializeStations();
        initializeTrains();
        initializeRestaurants();
        initializeFoodItems();
    }

    private void initializeStations() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            stations = dbHelper.getStations();
        } catch (Exception e) {
            System.err.println("Error loading stations in DataManager: " + e.getMessage());
            stations = new ArrayList<>();
        }

        // Fallback or ensure stations are loaded for initial setup
        if (stations.isEmpty()) {
            stations.add(new Station(1, "Dhaka Kamalapur", "DKA", "Dhaka"));
            stations.add(new Station(2, "Chittagong", "CTG", "Chittagong"));
            stations.add(new Station(3, "Comilla", "CML", "Comilla"));
            stations.add(new Station(4, "Feni", "FEN", "Feni"));
            stations.add(new Station(5, "Laksam", "LKS", "Laksam"));
            stations.add(new Station(6, "Rajshahi", "RJH", "Rajshahi"));
            stations.add(new Station(7, "Ishwardi", "ISH", "Ishwardi"));
            stations.add(new Station(8, "Pabna", "PAB", "Pabna"));
            stations.add(new Station(9, "Tangail", "TNG", "Tangail"));
        }
    }

    private Station getStationByCode(String code) {
        return stations.stream().filter(s -> s.getStationCode().equals(code)).findFirst().orElse(null);
    }

    private void initializeTrains() {
        trains = new ArrayList<>();

        Train train1 = new Train(1, "Suborno Express", "101", "Dhaka - Chittagong");
        addStationToTrain(train1, "DKA");
        addStationToTrain(train1, "LKS");
        addStationToTrain(train1, "CML");
        addStationToTrain(train1, "FEN");
        addStationToTrain(train1, "CTG");
        trains.add(train1);

        Train train2 = new Train(2, "Turna Nishitha", "102", "Dhaka - Chittagong");
        addStationToTrain(train2, "DKA");
        addStationToTrain(train2, "CML");
        addStationToTrain(train2, "FEN");
        addStationToTrain(train2, "CTG");
        trains.add(train2);

        Train train3 = new Train(3, "Silk City Express", "201", "Dhaka - Rajshahi");
        addStationToTrain(train3, "DKA");
        addStationToTrain(train3, "TNG");
        addStationToTrain(train3, "PAB");
        addStationToTrain(train3, "ISH");
        addStationToTrain(train3, "RJH");
        trains.add(train3);

        Train train4 = new Train(4, "Padma Express", "202", "Dhaka - Rajshahi");
        addStationToTrain(train4, "DKA");
        addStationToTrain(train4, "TNG");
        addStationToTrain(train4, "PAB");
        addStationToTrain(train4, "RJH");
        trains.add(train4);
    }

    private void addStationToTrain(Train train, String code) {
        Station s = getStationByCode(code);
        if (s != null) {
            train.addStation(s);
        }
    }

    private void initializeRestaurants() {
        restaurants = new ArrayList<>();
        restaurants.add(new Restaurant(1, "Star Kabab", 1, "Bangladeshi", 4.5));
        restaurants.add(new Restaurant(2, "Sultan's Dine", 1, "Bangladeshi/Mughlai", 4.7));
        restaurants.add(new Restaurant(3, "Kacchi Bhai", 1, "Biriyani Specialist", 4.8));
        restaurants.add(new Restaurant(4, "Handi Restaurant", 2, "Bangladeshi", 4.6));
        restaurants.add(new Restaurant(5, "Mezban House", 2, "Chittagong Special", 4.5));
        restaurants.add(new Restaurant(6, "Nanna Biriyani", 2, "Biriyani", 4.4));
        restaurants.add(new Restaurant(7, "Roshomalai Palace", 3, "Bengali Sweets", 4.3));
        restaurants.add(new Restaurant(8, "Morog Polao House", 3, "Bangladeshi", 4.2));
        restaurants.add(new Restaurant(9, "Silk City Kabab", 6, "Bangladeshi", 4.4));
        restaurants.add(new Restaurant(10, "Rajshahi Hotel", 6, "Bangladeshi", 4.0));
        restaurants.add(new Restaurant(11, "Tangail Chomchom", 9, "Bengali Sweets", 4.6));
        restaurants.add(new Restaurant(12, "Pabna Paradise", 8, "Bangladeshi", 4.1));
        restaurants.add(new Restaurant(13, "Feni Garden", 4, "Local Cuisine", 4.2));
        restaurants.add(new Restaurant(14, "Laksam Junction Hotel", 5, "Bangladeshi", 3.9));
        restaurants.add(new Restaurant(15, "Ishwardi Food Court", 7, "Snacks & Meals", 4.0));
    }

    private void initializeFoodItems() {
        foodItems = new ArrayList<>();

        // Example images: replace with real URLs
        String imgBiryani = "/com/example/bhojhon/images/biryani.png";
        String imgPilaf = "/com/example/bhojhon/images/pilaf.png";
        String imgRoast = "/com/example/bhojhon/images/roast_chicken.png";
        String imgKabab = "/com/example/bhojhon/images/kebab.png";
        String imgCurry = "/com/example/bhojhon/images/curry.png";
        String imgKorma = "/com/example/bhojhon/images/curry.png";
        String imgAyran = "/com/example/bhojhon/images/borhani.png";
        String imgFirni = "/com/example/bhojhon/images/firni.png";

        foodItems.add(new FoodItem(1, "Kacchi Biriyani", 1, 250, "Main Course", "Authentic Dhaka-style mutton biriyani",
                imgBiryani));
        foodItems.add(new FoodItem(2, "Beef Tehari", 1, 180, "Main Course", "Spicy beef rice dish", imgBiryani));
        foodItems.add(new FoodItem(3, "Chicken Roast", 1, 200, "Side Dish", "Bengali style chicken roast", imgRoast));
        foodItems.add(new FoodItem(4, "Shami Kabab", 1, 120, "Appetizer", "6 pieces of traditional kabab", imgKabab));

        // Sultan's Dine menu
        foodItems.add(new FoodItem(5, "Morog Polao", 2, 150, "Main Course", "Chicken pilaf Bengali style",
                imgPilaf));
        foodItems.add(new FoodItem(6, "Beef Rezala", 2, 220, "Main Course", "Creamy beef curry",
                imgCurry));
        foodItems.add(new FoodItem(7, "Mutton Korma", 2, 280, "Main Course", "Rich mutton curry",
                imgKorma));
        foodItems.add(new FoodItem(8, "Borhani", 2, 50, "Beverage", "Traditional yogurt drink",
                imgAyran));

        // Kacchi Bhai menu
        foodItems.add(new FoodItem(9, "Special Kacchi", 3, 300, "Main Course", "Premium mutton biriyani",
                imgBiryani));
        foodItems.add(new FoodItem(10, "Chicken Biriyani", 3, 180, "Main Course", "Dhaka style chicken biriyani",
                imgBiryani));
        foodItems.add(new FoodItem(11, "Firni", 3, 80, "Dessert", "Traditional rice pudding",
                imgFirni));

        // Similarly, add images for all other food items
        // e.g.:
        // foodItems.add(new FoodItem(12, "Mezban Beef", 4, 200, "Main Course",
        // "Chittagong specialty beef curry",
        // "https://via.placeholder.com/60?text=Mezban+Beef"));
    }

    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    public Train getTrainByNumber(String trainNumber) {
        return trains.stream().filter(t -> t.getTrainNumber().equals(trainNumber)).findFirst().orElse(null);
    }

    public List<Station> getAllStations() {
        return new ArrayList<>(stations);
    }

    public Station getStationById(int id) {
        return stations.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public List<Restaurant> getRestaurantsByStationId(int stationId) {
        // Hardcoded restaurants
        List<Restaurant> stationRestaurants = restaurants.stream()
                .filter(r -> r.getStationId() == stationId)
                .collect(Collectors.toList());

        // Dynamic restaurants from database
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            List<Restaurant> dynamicRestaurants = dbHelper.getRegisteredRestaurantsByStationId(stationId);
            stationRestaurants.addAll(dynamicRestaurants);
        } catch (Exception e) {
            System.err.println("Error fetching dynamic restaurants in DataManager: " + e.getMessage());
        }

        return stationRestaurants;
    }

    public Restaurant getRestaurantById(int id) {
        return restaurants.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    public List<FoodItem> getFoodItemsByRestaurantId(int restaurantId) {
        // Hardcoded food items
        List<FoodItem> restaurantFoodItems = foodItems.stream()
                .filter(f -> f.getRestaurantId() == restaurantId)
                .collect(Collectors.toCollection(ArrayList::new));

        // Dynamic food items from database
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            List<FoodItem> dynamicItems = dbHelper.getFoodItemsByRestaurantId(restaurantId);
            restaurantFoodItems.addAll(dynamicItems);
        } catch (Exception e) {
            System.err.println("Error fetching dynamic food items in DataManager: " + e.getMessage());
        }

        return restaurantFoodItems;
    }

    public FoodItem getFoodItemById(int id) {
        return foodItems.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
    }
}
