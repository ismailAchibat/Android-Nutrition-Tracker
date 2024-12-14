package com.example.nutritiontracker.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.nutritiontracker.Database.DatabaseHandler;
import com.example.nutritiontracker.R;
import com.example.nutritiontracker.databinding.FragmentHomeBinding;
import com.example.nutritiontracker.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHandler dbHandler;
    private String currentDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the database handler
        dbHandler = new DatabaseHandler(getContext());

        // Get current date in yyyy-MM-dd format
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_Prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if user ID is not found

        // Validate the user ID
        if (userId == -1) {
            Toast.makeText(getContext(), "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            Log.e("noUser", "no user");
        }

        // Setup UI components
        setupDateFilter(userId);
        updateHomePage(currentDate, userId);

        return root;
    }

    private void setupDateFilter(int userId) {
        binding.dateFilter.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                        updateHomePage(selectedDate, userId);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void updateHomePage(String date, int userId) {
        // Fetch user from the database
        User user = dbHandler.getUserById(userId);
        if (user == null) {
            Toast.makeText(getContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show();
            Log.e("HomeFragment", "User object is null for userId: " + userId);
            return;
        }

        // Fetch user calorie and protein goals
        int calorieGoal = user.getCaloriesGoal();
        int proteinGoal = user.getProteinGoal();

        // Fetch total calories and proteins for the day
        int[] totalCaloriesAndProtein = dbHandler.getTotalCaloriesAndProtein(userId, date);
        int totalCalories = totalCaloriesAndProtein[0];
        int totalProtein = totalCaloriesAndProtein[1];

        // Update calorie goal and achieved calories
        binding.caloriesText.setText(String.format("%d kcal / %d kcal", totalCalories, calorieGoal));

        // Update protein goal and achieved protein
        binding.proteinText.setText(String.format("%d g / %d g", totalProtein, proteinGoal));

        // Update calorie progress bar
        int calorieProgress = calorieGoal > 0 ? (int) (((float) totalCalories / calorieGoal) * 100) : 0;
        binding.caloriesProgressBar.setProgress(calorieProgress);

        // Update protein progress bar
        int proteinProgress = proteinGoal > 0 ? (int) (((float) totalProtein / proteinGoal) * 100) : 0;
        binding.proteinProgressBar.setProgress(proteinProgress);

        // Update food list
        Cursor cursor = dbHandler.getFoodsForUser(userId, date);
        StringBuilder foodList = new StringBuilder();

        // Get the column indices
        int foodNameIndex = cursor.getColumnIndex("food_name");
        int caloriesIndex = cursor.getColumnIndex("food_calories");
        int proteinsIndex = cursor.getColumnIndex("food_protein");

        if (foodNameIndex != -1 && caloriesIndex != -1) {
            // Iterate over the cursor to fetch data
            while (cursor.moveToNext()) {
                String foodName = cursor.getString(foodNameIndex);
                int calories = cursor.getInt(caloriesIndex);
                int proteins = cursor.getInt(proteinsIndex);
                foodList.append(String.format("%s - %d kcal - %d g of proteins\n", foodName, calories, proteins));
            }
        }

        cursor.close();

        binding.foodList.setText(foodList.length() > 0 ? foodList.toString() : "No foods logged for this day.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}