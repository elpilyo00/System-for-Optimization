package com.example.foodtoqu;
public class UserProfile {
    private double height; // Height in feet
    private double weight; // Weight in pounds
//    private String username; // Add any other user information you want to store

    // Constructors
    public UserProfile() {
        // Default constructor required for Firebase
    }

    public UserProfile(double height, double weight) {
        this.height = height;
        this.weight = weight;

    }

    // Getters and setters
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
}
