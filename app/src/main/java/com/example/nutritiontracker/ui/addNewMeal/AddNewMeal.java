package com.example.nutritiontracker.ui.addNewMeal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutritiontracker.Database.DatabaseHandler;
import com.example.nutritiontracker.R;
import com.example.nutritiontracker.models.NutritionData;

public class AddNewMeal extends Fragment {

    private AddNewMealViewModel addNewMealViewModel;
    private EditText mealDescriptionEditText;
    private Button addMealButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Get ViewModel
        addNewMealViewModel =
                new ViewModelProvider(this).get(AddNewMealViewModel.class);

        // Inflate the layout
        View root = inflater.inflate(R.layout.fragment_add_new_meal, container, false);

        // Bind the views
        mealDescriptionEditText = root.findViewById(R.id.mealDescriptionEditText);
        addMealButton = root.findViewById(R.id.addMealButton);

        // Set button click listener to add meal
        addMealButton.setOnClickListener(v -> addNewMeal());

        return root;
    }

    private void addNewMeal() {
        // Get the meal description
        String mealDescription = mealDescriptionEditText.getText().toString().trim();

        // Validate the input
        if (mealDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please describe your meal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_Prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if user ID is not found

        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the meal into the database
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());

        try{
            dbHandler.addFood(userId, mealDescription);
            Toast.makeText(getContext(), "Meal added successfully!", Toast.LENGTH_SHORT).show();
            // Clear the input field
            mealDescriptionEditText.setText("");
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to add meal", Toast.LENGTH_SHORT).show();
        }
    }
}
