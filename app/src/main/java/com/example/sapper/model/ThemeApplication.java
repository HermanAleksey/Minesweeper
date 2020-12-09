package com.example.sapper.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.sapper.model.constant.Constant;

public class ThemeApplication extends Application {
    // App level variable to retain selected spinner value


    @Override
    public void onCreate() {
        super.onCreate();
        Constant constant = new Constant();
        SharedPreferences sharedPreferences = getSharedPreferences(constant.getAPP_PREFERENCES_THEME(), MODE_PRIVATE);
        ThemeApplication.currentPosition = sharedPreferences.getInt(constant.getCURRENT_THEME(), 0);
    }

    public static int currentPosition;
}