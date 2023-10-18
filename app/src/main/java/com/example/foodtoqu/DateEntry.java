package com.example.foodtoqu;

import java.util.List;

public class DateEntry {
    private String date;
    private List<FoodItem2> foodItems;

    public DateEntry(String date, List<FoodItem2> foodItems) {
        this.date = date;
        this.foodItems = foodItems;
    }

    public String getDate() {
        return date;
    }

    public List<FoodItem2> getFoodItems() {
        return foodItems;
    }
}
