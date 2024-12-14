package com.example.nutritiontracker.ui.profile;

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

import com.example.nutritiontracker.R;
import com.example.nutritiontracker.Database.DatabaseHandler;
import com.example.nutritiontracker.models.User;

public class Profile extends Fragment {

    private EditText etCurrentPassword, etNewPassword, etFirstName, etLastName, etUsername, etEmail, etCaloriesGoal, etProteinGoal;
    private Button btnReset, btnApply;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        etCurrentPassword = root.findViewById(R.id.et_current_password);
        etNewPassword = root.findViewById(R.id.et_new_password);
        etFirstName = root.findViewById(R.id.et_first_name);
        etLastName = root.findViewById(R.id.et_last_name);
        etUsername = root.findViewById(R.id.et_username);
        etEmail = root.findViewById(R.id.et_email);
        etCaloriesGoal = root.findViewById(R.id.et_calories_goal);
        etProteinGoal = root.findViewById(R.id.et_protein_goal);
        btnApply = root.findViewById(R.id.btn_apply);

        // Fetch user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_Prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if user ID is not found

        if (userId != -1) {
            // Fetch user details from database
            DatabaseHandler dbHandler = new DatabaseHandler(getContext());
            User user = dbHandler.getUserById(userId);

            if (user != null) {
                // Populate fields with user data
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etUsername.setText(user.getUsername());
                etEmail.setText(user.getEmail());
                etCaloriesGoal.setText(String.valueOf(user.getCaloriesGoal()));
                etProteinGoal.setText(String.valueOf(user.getProteinGoal()));
            }
        } else {
            // Handle case when user ID is not found
            // Maybe show an error message or redirect to login screen
            Toast.makeText(getContext(), "User not found, please log in again.", Toast.LENGTH_SHORT).show();
        }

        btnApply.setOnClickListener(v -> {
            // Handle apply button click
            applyChanges(userId);
        });

        return root;
    }

    private void applyChanges(int userId) {
        // Get the new values from the input fields
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String caloriesGoal = etCaloriesGoal.getText().toString().trim();
        String proteinGoal = etProteinGoal.getText().toString().trim();

        // Validate inputs (basic validation)
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()) {
            // Show an error message if any required field is empty
            Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(getContext(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the user details in the database
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        User updatedUser = new User(
                userId,
                username,
                newPassword.isEmpty() ? currentPassword : newPassword,  // Use new password if provided, else current password
                firstName,
                lastName,
                email,
                Integer.parseInt(caloriesGoal),
                Integer.parseInt(proteinGoal)
        );

        boolean success = dbHandler.updateUser(updatedUser);

        if (success) {
            // Notify the user that changes were applied
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Handle failure to update
            Toast.makeText(getContext(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
