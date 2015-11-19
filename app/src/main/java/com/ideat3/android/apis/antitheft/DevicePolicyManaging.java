package com.ideat3.android.apis.antitheft;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by akshayjnambiar on 11/12/2015.
 */
public class DevicePolicyManaging extends Activity {
    static final int ACTIVATION_REQUEST = 0;//DevicePolicyManager.WIPE_EXTERNAL_STORAGE; // identifies our request id
    public static DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // Initialize Device Policy Manager service and our receiver class

    }

    public static void resetPhone(Context ctxt) {
        Log.d("TAG", "RESETing device now - all user data will be ERASED to factory settings");
        ComponentName cn=new ComponentName(ctxt, PasswordDetection.class);
        devicePolicyManager = (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (devicePolicyManager.isAdminActive(cn)) {
            try {
                devicePolicyManager.wipeData(ACTIVATION_REQUEST);
            } catch (Exception e) {
                Log.d("TAG",e.getMessage());
            }
        }
    }

    public int currentFailedAttempts() {
        return devicePolicyManager.getCurrentFailedPasswordAttempts();
    }
}