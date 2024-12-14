package com.example.nutritiontracker.ui.authentification;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.nutritiontracker.MainActivity;
import com.example.nutritiontracker.R;

public class LogIn extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpLinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by their IDs
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_login);
        signUpLinkTextView = findViewById(R.id.textView_signup_link);

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Set click listener for the sign-up link
        signUpLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUpActivity();
            }
        });
    }

    private void handleLogin() {
        // Retrieve user input from the form fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the user input (basic validation)
        if (username.isEmpty() || password.isEmpty()) {
            // Show an error if either field is empty
            Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a DatabaseHandler instance to check user credentials
        DatabaseHandler dbHandler = new DatabaseHandler(this);

        // Authenticate the user
        boolean isUserValid = dbHandler.checkUser(username, password);

        if (isUserValid) {
            // Get the user ID after successful login
            int userId = dbHandler.getUserId(username);

            // Store the user ID in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_Prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId", userId);
            editor.apply();

            // User is valid, proceed to the main activity (or home screen)
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finish the login activity so the user cannot navigate back to it
        } else {
            // Invalid credentials, show an error message
            Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToSignUpActivity() {
        // Start the sign-up activity
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);
    }
}