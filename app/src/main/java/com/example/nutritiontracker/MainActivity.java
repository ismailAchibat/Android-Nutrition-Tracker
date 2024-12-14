package com.example.nutritiontracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nutritiontracker.Database.DatabaseHandler;
import com.example.nutritiontracker.databinding.ActivityMainBinding;
import com.example.nutritiontracker.models.User;
import com.example.nutritiontracker.ui.authentification.LogIn;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Welcome to our application", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Access the header layout
        View headerView = navigationView.getHeaderView(0);

        // Get references to the TextViews in the header
        TextView nameTextView = headerView.findViewById(R.id.textView1);
        TextView emailTextView = headerView.findViewById(R.id.textView2);

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_Prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if user ID is not found

        if (userId != -1) {
            // Create an instance of DatabaseHandler
            DatabaseHandler databaseHandler = new DatabaseHandler(this);

            // Fetch the user data by ID
            User user = databaseHandler.getUserById(userId);

            // Check if the user exists and set the data
            if (user != null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                String email = user.getEmail();

                nameTextView.setText(fullName);
                emailTextView.setText(email);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle logout action
        if (id == R.id.action_logout) {
            // Clear SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Remove all data
            editor.apply();

            // Redirect to the login screen
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
            finish();  // Optional: To prevent user from coming back to the MainActivity after logging out

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
