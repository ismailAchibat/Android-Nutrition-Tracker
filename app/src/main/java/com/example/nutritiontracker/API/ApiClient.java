package com.example.nutritiontracker.API;
import okhttp3.*;

import com.example.nutritiontracker.models.Food;
import com.example.nutritiontracker.models.NutritionData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
public class ApiClient {
    private static final String API_URL = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    private static final String APP_KEY = "da230045aee77c6d010767c012a52d53";
    private static final String APP_ID = "935b8e9a";

    public interface FetchDataCallback {
        void onSuccess(NutritionData nutritionData);
        void onFailure(String errorMessage);
    }


    public void fetchData(String query, final FetchDataCallback callback) {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create JSON request body
        String jsonBody = "{ \"query\": \"" + query + "\" }";

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

        // Build request
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("x-app-key", APP_KEY)
                .addHeader("x-app-id", APP_ID)
                .post(body)
                .build();

        // Make asynchronous API call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure("Request failed: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("API Response: " + responseData);  // Log raw response

                    // Parse the response using Gson
                    Gson gson = new Gson();
                    try {
                        // Attempt to parse the response as an object, not an array
                        ApiResponse apiResponse = gson.fromJson(responseData, ApiResponse.class);

                        // Handle the parsed response...
                        // For example:
                        if (apiResponse != null && apiResponse.getFoods() != null) {
                            double totalCalories = 0;
                            double totalProteins = 0;

                            for (Food food : apiResponse.getFoods()) {
                                totalCalories += food.getCalories();
                                totalProteins += food.getProteins();
                            }

                            // Create NutritionData object and return it via callback
                            NutritionData nutritionData = new NutritionData(totalCalories, totalProteins);
                            if (callback != null) {
                                callback.onSuccess(nutritionData);
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();  // Log the error
                        if (callback != null) {
                            callback.onFailure("Failed to parse JSON: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Request failed: " + response.code());
                    if (callback != null) {
                        callback.onFailure("Request failed with code: " + response.code());
                    }
                }
            }
        });
    }
}