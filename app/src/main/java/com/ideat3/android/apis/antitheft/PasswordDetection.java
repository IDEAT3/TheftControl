package com.ideat3.android.apis.antitheft;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akshayjnambiar on 11/11/2015.
 */
public class PasswordDetection extends DeviceAdminReceiver {

    public static final String PREFERENCE_PASSWORD_FAILED_COUNT="PasswordFailedCount";
    public static final int DEFAULT_INT=0;
    public static final int DEFAULT_LONG=0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onEnabled(Context ctxt, Intent intent) {
        Log.d("TAG","Password Detection Enabled");

        // set failed attempts 0
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putInt(PREFERENCE_PASSWORD_FAILED_COUNT, DEFAULT_INT);
        editor.commit();

        ComponentName cn=new ComponentName(ctxt, PasswordDetection.class);
        DevicePolicyManager mgr=(DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mgr.setPasswordQuality(cn,
                DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);

        onPasswordChanged(ctxt, intent);
    }

    @Override
    public void onPasswordChanged(Context ctxt, Intent intent) {
        DevicePolicyManager mgr=
                (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        int msgId;

        if (mgr.isActivePasswordSufficient()) {
            msgId=R.string.compliant;
        }
        else {
            msgId=R.string.not_compliant;
        }

        //Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {

        Toast.makeText(ctxt, R.string.Debug_password_failed, Toast.LENGTH_LONG).show();
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        int count_failed_attempts = preferences.getInt(PREFERENCE_PASSWORD_FAILED_COUNT, DEFAULT_INT);
        count_failed_attempts++;
        editor.putInt(PREFERENCE_PASSWORD_FAILED_COUNT, count_failed_attempts);
        editor.commit();

        // Checking if the function is working

        if(count_failed_attempts%3==0) {
            editor.putBoolean(MissedCallDetector.PREFERENCE_CODE_RED, true);
            editor.commit();

            Log.d("TAG", Integer.toString(count_failed_attempts));
            Utils.SendSMS(ctxt,"Code Red");
        }
        Log.d("TAG","Password Failed");

    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        Log.d("TAG", "Password success");
        Toast.makeText(ctxt, R.string.Debug_password_success, Toast.LENGTH_LONG).show();
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        editor = preferences.edit();
        editor.putInt(PREFERENCE_PASSWORD_FAILED_COUNT, DEFAULT_INT);
        editor.putInt(MissedCallDetector.PREFERENCES_MISSEDCALL_COUNT, DEFAULT_INT);
        editor.putBoolean(MissedCallDetector.PREFERENCE_CODE_RED, MissedCallDetector.DEFAULT_BOOL);
        editor.commit();
    }

    public void sendSmsByManager(String text, Context ctxt) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(preferences.getString(MissedCallDetector.PREFERENCES_TRUSTED_PHONENUMBER,"Default"), null, text, null, null);
            //  Only for debugging
            Toast.makeText(ctxt.getApplicationContext(), "Your sms has been send", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            // Only for debugging
            Toast.makeText(ctxt.getApplicationContext(), "Your sms has failed", Toast.LENGTH_LONG).show();
        }
    }
}
