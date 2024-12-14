package com.example.nutritiontracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nutritiontracker.API.ApiClient;
import com.example.nutritiontracker.models.NutritionData;
import com.example.nutritiontracker.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;  // Increment version due to schema change

    // Database Name
    private static final String DATABASE_NAME = "nutrition_tracker.db";

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_FOODS = "foods";

    // Columns names for Users table
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CALORIES_GOAL = "calories_goal";
    private static final String KEY_PROTEINS_GOAL = "proteins_goal";

    // Columns names for Foods table
    private static final String KEY_FOOD_ID = "food_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FOOD_NAME = "food_name";
    private static final String KEY_FOOD_CALORIES = "food_calories";
    private static final String KEY_FOOD_PROTEIN = "food_protein";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_FOOD_DATE = "food_date";  // New column for date

    // API client
    private ApiClient apiClient = new ApiClient();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table (unchanged)
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_PASSWORD + " TEXT, "
                + KEY_FIRSTNAME + " TEXT, "
                + KEY_LASTNAME + " TEXT, "
                + KEY_EMAIL + " TEXT, "
                + KEY_CALORIES_GOAL + " INTEGER, "
                + KEY_PROTEINS_GOAL + " INTEGER)";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Foods table (updated to include food_date)
        String CREATE_FOODS_TABLE = "CREATE TABLE " + TABLE_FOODS + " ("
                + KEY_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_USER_ID + " INTEGER, "
                + KEY_FOOD_NAME + " TEXT, "
                + KEY_FOOD_CALORIES + " INTEGER, "
                + KEY_FOOD_PROTEIN + " INTEGER, "
                + KEY_FOOD_DATE + " DATE, "
                + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "))";
        db.execSQL(CREATE_FOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
        // Create tables again
        onCreate(db);
    }

    // Add a food item to the database with current date
    public void addFood(int userId, String foodDescription) {
        // Get current date in yyyy-MM-dd format
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Fetch food data asynchronously
        apiClient.fetchData(foodDescription, new ApiClient.FetchDataCallback() {
            @Override
            public void onSuccess(NutritionData nutritionData) {
                // After fetching the data, insert it into the database
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_USER_ID, userId);  // Link food to the user
                values.put(KEY_FOOD_NAME, foodDescription);
                values.put(KEY_FOOD_CALORIES, nutritionData.getTotalCalories());
                values.put(KEY_FOOD_PROTEIN, nutritionData.getTotalProteins());
                values.put(KEY_FOOD_DATE, currentDate);  // Add current date
                db.insert(TABLE_FOODS, null, values);
                db.close();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure case (e.g., show an error message)
                System.out.println("Error: " + errorMessage);
            }
        });
    }

    // Get foods for a specific user on a specific date
    public Cursor getFoodsForUser(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FOODS +
                " WHERE " + KEY_USER_ID + " = ? AND " + KEY_FOOD_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date});
        return cursor;
    }

    // Get total calories and protein for a specific user on a specific date
    public int[] getTotalCaloriesAndProtein(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + KEY_FOOD_CALORIES + ") AS total_calories, " +
                "SUM(" + KEY_FOOD_PROTEIN + ") AS total_protein " +
                "FROM " + TABLE_FOODS +
                " WHERE " + KEY_USER_ID + " = ? AND " + KEY_FOOD_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date});

        int totalCalories = 0;
        int totalProtein = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int caloriesColumnIndex = cursor.getColumnIndex("total_calories");
            int proteinColumnIndex = cursor.getColumnIndex("total_protein");

            // Ensure the column index is valid (greater than or equal to 0)
            if (caloriesColumnIndex >= 0 && proteinColumnIndex >= 0) {
                totalCalories = cursor.getInt(caloriesColumnIndex);
                totalProtein = cursor.getInt(proteinColumnIndex);
            } else {
                // Handle the case when the column index is invalid (columns don't exist)
                System.out.println("Column index not found. Ensure column names are correct.");
            }

            cursor.close();
        }

        return new int[]{totalCalories, totalProtein};
    }

    // Method to add a new user to the database
    public void addUser(String username, String password, String firstName, String lastName, String email, int caloriesGoal, int proteinsGoal) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a ContentValues object to store the user data
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password); // Consider hashing the password for security
        values.put(KEY_FIRSTNAME, firstName);
        values.put(KEY_LASTNAME, lastName);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CALORIES_GOAL, caloriesGoal);
        values.put(KEY_PROTEINS_GOAL, proteinsGoal);

        // Insert the user data into the "users" table
        db.insert(TABLE_USERS, null, values);

        // Close the database
        db.close();
    }

    // Method to check if a user exists and validate credentials
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isValid = false;

        // Query to select the user with the given username and password
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        // If a matching user is found, credentials are valid
        if (cursor != null && cursor.moveToFirst()) {
            isValid = true;
        }

        // Close the cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return isValid;
    }

    //Get the user ID
    public int getUserId(String username) {
        int userId = -1;  // Default value if the user is not found
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to get the user ID based on the username
            String query = "SELECT id FROM users WHERE username = ?";
            cursor = db.rawQuery(query, new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve the user ID from the first column of the result
                userId = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userId;
    }

    //get the user by id
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_ID + "=?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRSTNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LASTNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CALORIES_GOAL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PROTEINS_GOAL))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return user;
    }

    //Update a user
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add the updated user details to ContentValues
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FIRSTNAME, user.getFirstName());
        values.put(KEY_LASTNAME, user.getLastName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_CALORIES_GOAL, user.getCaloriesGoal());
        values.put(KEY_PROTEINS_GOAL, user.getProteinGoal());

        // Update the user in the database
        int rowsUpdated = db.update(
                TABLE_USERS,
                values,
                KEY_ID + "=?",
                new String[]{String.valueOf(user.getId())}
        );

        db.close();
        return rowsUpdated > 0;
    }

    // Delete a user
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_USERS, KEY_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }
}
