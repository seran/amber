package com.fortlab.amber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ReceiveSms extends BroadcastReceiver {

    private static final String TAG = "SMSRECEIVER";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;

            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            ContactHelper contactHelper = new ContactHelper();

            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0;i< msgs.length;i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        String contactId = contactHelper.contactStoredNumberByPhoneNumber(context, msg_from);
                        if (databaseHelper.checkContactExists(contactId) && msgBody.equals("AMBER")) {
                            Intent serviceIntent = new Intent(context, AlarmService.class);
                            context.startService(serviceIntent);
                            Toast.makeText(context, "Amber Alert", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Standard SMS", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
