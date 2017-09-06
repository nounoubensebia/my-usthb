package com.example.nouno.locateme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nouno on 06/09/2017.
 */

public class SharedPreference {

    public static void saveString (String key,String s,Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,s);
        editor.commit();
    }

    public static String loadString (String key,Context context)
    {
        if (verifyKey(key,context))
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String json = sharedPref.getString(key,null);
            return json;
        }
        else
        {
            return null;
        }
    }

    public static boolean verifyKey (String key,Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.contains(key);
    }
}
