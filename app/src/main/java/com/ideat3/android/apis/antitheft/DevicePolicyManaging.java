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
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;
    public static final Boolean debug = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public void resetPhone(int flag) {
        Log.d("TAG", "RESETing device now - all user data will be ERASED to factory settings");
        // Change : uncomment the below line
       // devicePolicyManager.wipeData(ACTIVATION_REQUEST);
    }

    public int currentFailedAttempts() {
        return devicePolicyManager.getCurrentFailedPasswordAttempts();
    }
}
