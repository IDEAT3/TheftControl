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

    @Override
    public void onEnabled(Context ctxt, Intent intent) {
        Log.d("TAG", "Password Detection Enabled");

        // set failed attempts 0

        SharedPreferenceManager.storeInt(ctxt, SharedPreferenceManager.PREFERENCE_PASSWORD_FAILED_COUNT,
                SharedPreferenceManager.DEFAULT_INT);

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
        int count_failed_attempts = SharedPreferenceManager.loadInt(ctxt,SharedPreferenceManager.
                PREFERENCE_PASSWORD_FAILED_COUNT);
        count_failed_attempts++;
        SharedPreferenceManager.storeInt(ctxt, SharedPreferenceManager.PREFERENCE_PASSWORD_FAILED_COUNT,
                count_failed_attempts);


        // Checking if the function is working
        int d = DevicePolicyManaging.devicePolicyManager.getCurrentFailedPasswordAttempts();
        Log.d("TAG", Integer.toString(d));
        if(count_failed_attempts>=3) {
            SharedPreferenceManager.storeBoolean(ctxt, SharedPreferenceManager.PREFERENCE_CODE_RED, true);
            Log.d("TAG", Integer.toString(count_failed_attempts));
            sendSmsByManager("Code Red",ctxt);
        }
        Log.d("TAG","Password Failed");

    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        Log.d("TAG", "Password success");
        Toast.makeText(ctxt, R.string.Debug_password_success, Toast.LENGTH_LONG).show();

        SharedPreferenceManager.storeInt(ctxt, SharedPreferenceManager.PREFERENCE_PASSWORD_FAILED_COUNT,
                SharedPreferenceManager.DEFAULT_COUNT);
        SharedPreferenceManager.storeInt(ctxt, SharedPreferenceManager.PREFERENCES_MISSEDCALL_COUNT,
                SharedPreferenceManager.DEFAULT_INT);
        SharedPreferenceManager.storeBoolean(ctxt, SharedPreferenceManager.PREFERENCE_CODE_RED,
                SharedPreferenceManager.DEFAULT_BOOL);
    }

    public void sendSmsByManager(String text, Context ctxt) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SharedPreferenceManager.loadString(ctxt,SharedPreferenceManager.
                    PREFERENCES_TRUSTED_PHONENUMBER), null, text, null, null);

            //  Only for debugging
            Toast.makeText(ctxt.getApplicationContext(), "Your sms has been send", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            // Only for debugging
            Toast.makeText(ctxt.getApplicationContext(), "Your sms has failed", Toast.LENGTH_LONG).show();
        }
    }
}
