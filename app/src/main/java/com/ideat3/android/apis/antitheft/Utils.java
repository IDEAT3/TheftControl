package com.ideat3.android.apis.antitheft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.telephony.SmsManager;

/**
 * Created by akshayjnambiar on 11/11/2015.
 */
public class Utils extends Activity {

    public static void SendSMS(Context context, String address, String content) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(address, null, content, sentPI, deliveredPI);
    }


}
