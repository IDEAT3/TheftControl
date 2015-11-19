package com.ideat3.android.apis.antitheft;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
 * This is the main and laucher class. This class
 *  1) loads startpage.xml
 *  2) initializes the shared preference variables
 *  3) Checks for admin privileges
 *  4)
 */

public class StartActivity extends AppCompatActivity {

    public static final Boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);             // 1. layout/startpage.xml

        if(debug) {
            Log.d("TAG", "Started App");                // Only in case of debugging
          // DevicePolicyManaging.resetPhone(this);
            // to uncomment below comment out sendsms and resetphone in phonetrackactivity
            Intent locateIntent = new Intent(this, PhoneTrackerActivity.class);
            locateIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            this.startActivity(locateIntent);
        }

        // the function is present in same class which initializes shared preference variables
        initSharedPreference();



        /*   */
        /**
         * Checks for admin privileges and gets those if not present from DeviceAdminManager
         * {ACTION_ADD_DEVICE_ADMIN} ask the user to add a new device administrator to the system.
         * The desired policy is the ComponentName of the policy in the {EXTRA_DEVICE_ADMIN}
         * extra field.  This will invoke a UI to bring the user through adding the device
         * administrator to the system (or allowing them to reject it). You can optionally
         * include the {EXTRA_ADD_EXPLANATION} field to provide the user with additional
         * explanation (in addition to your component's description) about what is being added.
         * If your administrator is already active, this will ordinarily return immediately
         * (without user intervention).  However, if your administrator has been updated and
         * is requesting additional uses-policy flags, the user will be presented with the new
         * list.  New policies will not be available to the updated administrator until the
         * user has accepted the new list.
         */
        ComponentName cn=new ComponentName(this, PasswordDetection.class);
        DevicePolicyManager mgr=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        if (!mgr.isAdminActive(cn)) {
            Log.d("TAG","Getting device admin privileges");
            Intent intent= new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));
            startActivity(intent);
            Log.d("TAG","getting not admin");
        }

        final EditText ed1;
        ed1 = (EditText)findViewById(R.id.trusted_phone_number);
        final Button b;
        b = (Button) findViewById(R.id.btnSetPhoneNumber);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                String s = ed1.getText().toString();
                editor.putString(MissedCallDetector.PREFERENCES_TRUSTED_PHONENUMBER, s);
                editor.commit();
                Log.d("TAG",s);
                Log.d("TAG",preferences.getString(MissedCallDetector.PREFERENCES_TRUSTED_PHONENUMBER,
                        getResources().getString(R.string.DEFAULT_TRUSTED_PHONENUMBER)));
            }
        });
    }

    /*
     *  Main action   : initializes the shared preference varibles
     *  parameters    : void
     *  return value  : void
     *  Full Action   : The shared preference variables PREFERENCE_PASSWORD_FAILED_COUNT, 
     *                  PREFERENCE_CODE_RED, PREFERENCES_MISSEDCALL_COUNT, PREFERENCES_TIMESTAMP
     *                  are initialized to their default values (Integer || Long  =  0,
     *                  String = "Default", Boolean = false}
     */
    public void initSharedPreference() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PasswordDetection.PREFERENCE_PASSWORD_FAILED_COUNT, PasswordDetection.DEFAULT_INT);
        editor.putBoolean(MissedCallDetector.PREFERENCE_CODE_RED, MissedCallDetector.DEFAULT_BOOL);
        editor.putInt(MissedCallDetector.PREFERENCES_MISSEDCALL_COUNT, PasswordDetection.DEFAULT_INT);
        editor.commit();

    }
}
