package edu.uw.chitchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * helper method to put / get shared preference values
 * @Author Delvin Mackenzie
 * @ 3/14/2019
 */
public class PrefHelper {

    /**
     * gets int for specified shared preference
     *
     * @param key This is the first paramter to method
     * @param context  This is the second parameter to method
     * @return int This returns value stored in key value pair.
     */
    public static int getIntPreference (String key, Context context) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }

    /**
     *gets single String value from shared preferences
     * @param key This is the first paramter to method
     * @param context  This is the second parameter to method
     * @return String This returns value stored in key value pair.
     */
    public static String getStringPreference (String key, Context context) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    /**
     *puts single Int value to shared preferences
     * @param key This is the first paramter to method
     * @param value This is the second parameter to method
     * @param context  This is the third parameter to method
     * @return Nothing.
     */
    public static void putIntPreference (String key, int value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     *puts single Int value to shared preferences
     * @param key This is the first paramter to method
     * @param value This is the second parameter to method
     * @param context  This is the third parameter to method
     * @return Nothing.
     */
    public static void putStringPreference(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
