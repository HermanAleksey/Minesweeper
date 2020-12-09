package com.example.sapper.view;

import android.app.Activity;
import android.content.Intent;

import com.example.sapper.R;
import com.example.sapper.model.ThemeApplication;

public class Utils {
//    private static int sTheme;

    public final static int THEME_NONE = 0;
    public final static int THEME_OLD = 1;
    public final static int THEME_MATERIAL = 2;

    public static void applySelectedTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (ThemeApplication.currentPosition) {
            default:
            case THEME_NONE:
                activity.setTheme(R.style.Theme_AppCompat_Light);
                break;
            case THEME_OLD:
                activity.setTheme(R.style.Theme_OldMode);
                break;
            case THEME_MATERIAL:
                activity.setTheme(R.style.Theme_Material);
                break;
        }
    }
}
