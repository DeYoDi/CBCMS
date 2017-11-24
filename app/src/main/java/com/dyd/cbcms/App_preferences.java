package com.dyd.cbcms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by A_KIRA on 11/15/2017.
 */

public class App_preferences extends Activity {

    public static final String AppPref = "AppPref";
    public static final String Language = "LangKey";
    public static final String Accuracy = "AccuracyKey";

    Context context = getApplicationContext();

   // SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    public String getLanguage(Context context) {
        context = getApplicationContext();
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppPref, 0);

        if (sharedpreferences.contains(Language)) {
            return sharedpreferences.getString(Language, "English");
        }
        else {
            return getResources().getString(R.string.Default_Language);
        }
    }
    public  String getAccuracy() {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppPref, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Accuracy)) {
            return sharedpreferences.getString(Accuracy, "");
        }
        else {
            return getResources().getString(R.string.Default_Accuracy);
        }
    }

    public void setSharedpreferences(String key , String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
