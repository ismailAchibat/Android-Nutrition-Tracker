package com.example.nutritiontracker.ui.authentification;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nutritiontracker.Database.DatabaseHandler;
import com.example.nutritiontracker.R;

public class SignUp extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText caloriesGoalEditText;
    private EditText proteinsGoalEditText;
    private Button signUpButton;
    private TextView loginLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        firstNameEditText = findViewById(R.id.editText_firstname);
        lastNameEditText = findViewById(R.id.editText_lastname);
        emailEditText = findViewById(R.id.editText_email);
        caloriesGoalEditText = findViewById(R.id.editText_calories_goal);
        proteinsGoalEditText = findViewById(R.id.editText_proteins_goal);
        signUpButton = findViewById(R.id.button_signup);
        loginLinkTextView = findViewById(R.id.textView_login_link);

        // Set click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // Set click listener for the login link
        loginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginActivity();
            }
        });
    }

    private void handleSignUp() {
        // Retrieve user input from the form fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String caloriesGoalStr = caloriesGoalEditText.getText().toString().trim();
        String proteinsGoalStr = proteinsGoalEditText.getText().toString().trim();

        // Validate the user input
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || caloriesGoalStr.isEmpty() || proteinsGoalStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int caloriesGoal;
        int proteinsGoal;

        try {
            caloriesGoal = Integer.parseInt(caloriesGoalStr);
            proteinsGoal = Integer.parseInt(proteinsGoalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for goals", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the new user to the database
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // Check if username or email is already taken
        Cursor cursor = dbHandler.getReadableDatabase().query(
                "users", // Table name
                new String[]{"id"}, // Columns to return
                "username = ? OR email = ?", // Selection criteria
                new String[]{username, email}, // Selection arguments
                null, null, null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(this, "Username or email already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor != null) cursor.close();

        // Add the user to the database
        dbHandler.addUser(username, password, firstName, lastName, email, caloriesGoal, proteinsGoal);

        // Show a success message
        Toast.makeText(this, "Sign up successful! Please log in.", Toast.LENGTH_SHORT).show();

        // Navigate to the login activity
        Intent loginIntent = new Intent(SignUp.this, LogIn.class);
        startActivity(loginIntent);
        finish();
    }


    private void navigateToLoginActivity() {
        // Start the login activity
        Intent intent = new Intent(SignUp.this, LogIn.class);
        startActivity(intent);
    }
}