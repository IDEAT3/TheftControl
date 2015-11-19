package com.ideat3.android.apis.antitheft;

import android.app.admin.DevicePolicyManager;
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


    public static final String PREFERENCES_TRUSTED_PHONENUMBER = "TrustedPhoneNumber";
    public static final String PREFERENCES_TIMESTAMP = "Timestamp";
    public static final long DEFAULT_TIMESTAMP = 999;
    public static final String PREFERENCES_MISSEDCALL_COUNT = "MissedCallCount";
    public static final int DEFAULT_COUNT = 0;
    public static final String PREFERENCES_WIPE_OUT = "WipeOutFlag";
    public static final String PREFERENCE_CODE_RED = "CodeRed";
    public static final Boolean DEFAULT_BOOL = false;
    public static final String PREFERENCE_SEND_LOCATION = "SendLocationFlag";

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

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();

        CodeRed = preferences.getBoolean(PREFERENCE_CODE_RED, DEFAULT_BOOL);
        Log.d("TAG",CodeRed.toString());
        if(CodeRed) {
            Log.d("TAG","Code Red call");
            TrustedPhoneNumber = preferences.getString(PREFERENCES_TRUSTED_PHONENUMBER,
                    mContext.getResources().getString(R.string.DEFAULT_TRUSTED_PHONENUMBER));
            MissedCallCount = preferences.getInt(PREFERENCES_MISSEDCALL_COUNT, DEFAULT_COUNT);
            Log.d("TAG", " kill " + Integer.toString(MissedCallCount));

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
                //  Change : Need Code for sending location information

                editor.putInt(MissedCallDetector.PREFERENCES_MISSEDCALL_COUNT, PasswordDetection.DEFAULT_INT);
                editor.putBoolean(MissedCallDetector.PREFERENCE_CODE_RED, false);
                editor.commit();

            }

            // phone is idle
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // detect missed call
                Log.d("TAG","Got missed call");
                CurrentTimestamp = System.currentTimeMillis();
                PrevTimestamp = preferences.getLong(PREFERENCES_TIMESTAMP,
                        DEFAULT_TIMESTAMP);
                Log.d("TAG",Long.toString(CurrentTimestamp- PrevTimestamp));
                if (isRinging && !isReceived && callerPhoneNumber.equals(TrustedPhoneNumber) && !(TrustedPhoneNumber.equals(R.string.DEFAULT_TRUSTED_PHONENUMBER))) {
                    MissedCallCount++;
                    editor.putInt(PREFERENCES_MISSEDCALL_COUNT, MissedCallCount);
                    editor.commit();

                    Log.d("TAG", Integer.toString(MissedCallCount));
                    if (MissedCallCount >= 2 && Math.abs(CurrentTimestamp - PrevTimestamp) < 1000 * 60 * 30 && Math.abs(CurrentTimestamp - PrevTimestamp) >10000) { // 30 minutes


                        Intent locateIntent = new Intent(mContext, PhoneTrackerActivity.class);
                        locateIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        mContext.startActivity(locateIntent);

                        Log.d("TAG", "Two Missed calls from trusted number");
                    }
                    Toast.makeText(mContext, "Got a missed call from : " + callerPhoneNumber, Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Missed call from trusted number");
                    editor.putLong(PREFERENCES_TIMESTAMP, CurrentTimestamp);
                    editor.commit();
                }
            }
        }
    }
}
