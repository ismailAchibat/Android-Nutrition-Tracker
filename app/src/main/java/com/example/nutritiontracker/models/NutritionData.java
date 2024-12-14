package com.example.nutritiontracker.models;

public class NutritionData {
    private double totalCalories;
    private double totalProteins;

    // Constructor
    public NutritionData(double totalCalories, double totalProteins) {
        this.totalCalories = totalCalories;
        this.totalProteins = totalProteins;
    }

    // Getters
    public double getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProteins() {
        return totalProteins;
    }
}
