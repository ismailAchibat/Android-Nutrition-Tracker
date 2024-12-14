package com.example.nutritiontracker.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private int caloriesGoal;
    private int proteinGoal;

    public User(int id, String username, String password, String firstName, String lastName, String email, int caloriesGoal, int proteinGoal) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.caloriesGoal = caloriesGoal;
        this.proteinGoal = proteinGoal;
    }

    // Getters and setters for each attribute
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getCaloriesGoal() { return caloriesGoal; }
    public int getProteinGoal() { return proteinGoal; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setCaloriesGoal(int caloriesGoal) { this.caloriesGoal = caloriesGoal; }
    public void setProteinGoal(int proteinGoal) { this.proteinGoal = proteinGoal; }
}