package com.example.demosecuresms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Vu Trong Linh
 * Date:  9/22/2022
 */
public class SmsBroadCastReceiver extends BroadcastReceiver {

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        //Specify the bundle to get object based on SMS protocol "pdus"
        Object[] objects = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[objects.length];
        Intent in = new Intent(context, DisplaySMSActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String originNumber = "";
        String msgContent = "";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objects.length; i++) {
            smsMessage[i] = SmsMessage.createFromPdu((byte[]) objects[i]);
            //get the received SMS content
            msgContent = smsMessage[i].getDisplayMessageBody();

            // get the sender phone number
            originNumber = smsMessage[i].getDisplayOriginatingAddress();

            // aggregate the messages together  when long message are fragmented
            sb.append(msgContent);

            // abort broadcast to cellphone inbox
            abortBroadcast();

        }
        // fill the sender's phone number into Intent
        in.putExtra("originNumber", originNumber);

        //fill the entire message body into Intent
        in.putExtra("msgContent", new String(sb));

        // start the DisplaySMSActivity
        context.startActivity(in);
    }
}
