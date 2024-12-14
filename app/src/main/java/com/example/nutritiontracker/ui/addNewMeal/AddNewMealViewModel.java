package com.example.nutritiontracker.ui.addNewMeal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewMealViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddNewMealViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the add new meal fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}