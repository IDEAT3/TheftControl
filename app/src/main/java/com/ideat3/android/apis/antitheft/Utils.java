package com.ideat3.android.apis.antitheft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akshayjnambiar on 11/11/2015.
 */
public class Utils extends Activity {

    private static SharedPreferences preferences;

    public static void SendSMS(Context ctxt, String content) {
        Log.d("TAG", "Send sms" + Boolean.toString(!StartActivity.debug ));
        if(!StartActivity.debug) {
            preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(preferences.getString(MissedCallDetector.PREFERENCES_TRUSTED_PHONENUMBER, "Default"), null, content, null, null);
                //  Only for debugging
                Toast.makeText(ctxt.getApplicationContext(), "Your sms has been send", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                // Only for debugging
                Toast.makeText(ctxt.getApplicationContext(), "Your sms has failed", Toast.LENGTH_LONG).show();
            }
        }
    }


}
