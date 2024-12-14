package com.example.nutritiontracker.models;

public class Food {
    private String food_name;
    private double serving_qty;
    private double nf_calories;
    private double nf_protein;

    // Getters
    public String getFoodName() {
        return food_name;
    }
    public double getQuantity() {
        return serving_qty;
    }

    public double getCalories() {
        return nf_calories;
    }

    public double getProteins() {
        return nf_protein;
    }
}
