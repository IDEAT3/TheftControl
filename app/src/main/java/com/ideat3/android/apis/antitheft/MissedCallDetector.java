package com.ideat3.android.apis.antitheft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akshayjnambiar on 11/11/2015.
 */
public class MissedCallDetector extends BroadcastReceiver {

    static boolean isRinging=false;
    static boolean isReceived=false;
    static String callerPhoneNumber;
    private String TrustedPhoneNumber;
    private Boolean CodeRed;
    private long CurrentTimestamp;
    private long PrevTimestamp;
    private int MissedCallCount;

    @Override
    public void onReceive(Context mContext, Intent intent){

        CodeRed = SharedPreferenceManager.loadBoolean(mContext,SharedPreferenceManager.PREFERENCE_CODE_RED);
        Log.d("TAG",CodeRed.toString());
        if(CodeRed) {
            Log.d("TAG","Code Red call");
            TrustedPhoneNumber = SharedPreferenceManager.loadString(mContext,SharedPreferenceManager.
                    PREFERENCES_TRUSTED_PHONENUMBER);
            MissedCallCount = SharedPreferenceManager.loadInt(mContext,SharedPreferenceManager.
                    PREFERENCES_MISSEDCALL_COUNT);

            // Get current phone state
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state == null)
                return;

            //phone is ringing
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                isRinging = true;
                //get caller's number
                Bundle bundle = intent.getExtras();
                callerPhoneNumber = bundle.getString("incoming_number");
            }

            //phone is received
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                isReceived = true;
                // Need Code for sending location information

                SharedPreferenceManager.storeInt(mContext, SharedPreferenceManager.PREFERENCES_MISSEDCALL_COUNT,
                        SharedPreferenceManager.DEFAULT_INT);
                SharedPreferenceManager.storeBoolean(mContext, SharedPreferenceManager.PREFERENCE_CODE_RED,
                        false);
            }

            // phone is idle
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // detect missed call
                Log.d("TAG","Got missed call");
                if (isRinging && !isReceived && callerPhoneNumber.equals(TrustedPhoneNumber) &&
                        !(TrustedPhoneNumber.equals(R.string.DEFAULT_TRUSTED_PHONENUMBER))) {
                    MissedCallCount++;
                    SharedPreferenceManager.storeInt(mContext,SharedPreferenceManager.PREFERENCES_MISSEDCALL_COUNT,
                            MissedCallCount);
                    CurrentTimestamp = System.currentTimeMillis();
                    PrevTimestamp = SharedPreferenceManager.loadLong(mContext,SharedPreferenceManager.
                            PREFERENCES_TIMESTAMP);
                    Log.d("TAG", Integer.toString(MissedCallCount));
                    Log.d("TAG",Long.toString(Math.abs(CurrentTimestamp - PrevTimestamp)));
                    if (MissedCallCount >= 2 && Math.abs(CurrentTimestamp - PrevTimestamp) < 1000 * 60 * 30) { // 30 minutes
                        // Code to send location and wipe out
                        Log.d("TAG","Two Missed calls from trusted number");
                    }
                    Toast.makeText(mContext, "Got a missed call from : " + callerPhoneNumber, Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Missed call from trusted number");
                    SharedPreferenceManager.storeLong(mContext,SharedPreferenceManager.PREFERENCES_TIMESTAMP,
                            CurrentTimestamp);
                }
            }
        }
    }
}
