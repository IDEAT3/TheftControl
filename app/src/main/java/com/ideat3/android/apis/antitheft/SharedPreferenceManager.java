package com.ideat3.android.apis.antitheft;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by akshayjnambiar on 11/12/2015.
 */
public class SharedPreferenceManager {

    public static final String PREFERENCE_PASSWORD_FAILED_COUNT="PasswordFailedCount";
    public static final String PREFERENCES_TRUSTED_PHONENUMBER = "TrustedPhoneNumber";
    public static final String PREFERENCES_TIMESTAMP = "Timestamp";
    public static final String PREFERENCES_MISSEDCALL_COUNT = "MissedCallCount";
    public static final String PREFERENCES_WIPE_OUT = "WipeOutFlag";
    public static final String PREFERENCE_CODE_RED = "CodeRed";
    public static final String PREFERENCE_SEND_LOCATION = "SendLocationFlag";
    public static final int DEFAULT_COUNT = 0;
    public static final String DEFAULT_STRING = "Default";
    public static final long DEFAULT_TIMESTAMP = 0;
    public static final Boolean DEFAULT_BOOL = false;
    public static final int DEFAULT_INT=0;
    public static final long DEFAULT_LONG=0;

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    /*
     *  Main action   : initializes the shared preference varibles
     *  parameters    : void
     *  return value  : void
     *  Full Action   : The shared preference variables PREFERENCE_PASSWORD_FAILED_COUNT,
     *                  PREFERENCE_CODE_RED, PREFERENCES_MISSEDCALL_COUNT, PREFERENCES_TIMESTAMP
     *                  are initialized to their default values (Integer || Long  =  0,
     *                  String = "Default", Boolean = false}
     */
    public static void initSharedPreference(Context ctxt) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putInt(PREFERENCE_PASSWORD_FAILED_COUNT, DEFAULT_INT);
        editor.putBoolean(PREFERENCE_CODE_RED, DEFAULT_BOOL);
        editor.putInt(PREFERENCES_MISSEDCALL_COUNT, DEFAULT_INT);
        editor.putLong(PREFERENCES_TIMESTAMP, DEFAULT_TIMESTAMP);
        editor.commit();
    }

    /*
     * For each Shared preference variable a get and put method which is public
     */

    public static void storeInt(Context ctxt, String key, int value){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static void storeBoolean(Context ctxt, String key, Boolean value){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void storeLong(Context ctxt, String key, Long value){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void storeString(Context ctxt, String key, String value){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int loadInt(Context ctxt, String key){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return preferences.getInt(key,DEFAULT_INT);
    }

    public static Boolean loadBoolean(Context ctxt, String key){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return preferences.getBoolean(key, DEFAULT_BOOL);
    }

    public static Long loadLong(Context ctxt, String key){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return preferences.getLong(key, DEFAULT_LONG);
    }

    public static String loadString(Context ctxt, String key){
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        return preferences.getString(key, DEFAULT_STRING);
    }
}
