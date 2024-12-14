package com.example.nutritiontracker.API;

import com.example.nutritiontracker.models.Food;

public class ApiResponse {
    private Food[] foods;

    public Food[] getFoods() {
        return foods;
    }

    public void setFoods(Food[] foods) {
        this.foods = foods;
    }
}
