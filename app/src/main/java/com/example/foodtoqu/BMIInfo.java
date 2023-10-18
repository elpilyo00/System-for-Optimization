package com.example.foodtoqu;

public class BMIInfo {
    private double bmi;
    private String category;
    private String healthRisk;

    public BMIInfo() {
        // Default constructor required for Firebase
    }

    public BMIInfo(double bmi, String category, String healthRisk) {
        this.bmi = bmi;
        this.category = category;
        this.healthRisk = healthRisk;
    }

    public double getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    public String getHealthRisk() {
        return healthRisk;
    }
}