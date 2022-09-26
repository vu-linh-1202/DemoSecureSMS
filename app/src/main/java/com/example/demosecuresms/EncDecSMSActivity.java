package com.example.demosecuresms;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class used to call when the activity is first created
 * <p>
 * Created by Vu Trong Linh
 * Date: 9/23/2022
 */
public class EncDecSMSActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    EditText recNum, secretKey, msgContent;
    Button send, cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        recNum = findViewById(R.id.recNum);
        secretKey = findViewById(R.id.secretKey);
        msgContent = findViewById(R.id.msgContent);
        send = findViewById(R.id.Send);
        cancel = findViewById(R.id.cancel);

        // finish the activity when click Cancel button
        cancel.setOnClickListener(view -> finish());

        // encrypt the message and send when click Send button
        send.setOnClickListener(view -> {
            String recNumString = recNum.getText().toString();
            String secretKeyString = secretKey.getText().toString();
            String msgContentString = msgContent.getText().toString();

            // check for the validity of the user input
            // key length should be 16 characters as defined by AES-128-bit
            if (recNumString.length() > 0 && secretKeyString.length() > 0
                    && msgContentString.length() > 0
                    && secretKeyString.length() == 16) {

                // encrypt the message
                byte[] encryptedMsg = encryptSMS(secretKeyString, msgContentString);

                // convert the byte array to hex format in order for
                // transmission
                String msgString = byte2hex(encryptedMsg);

                // send the message through SMS
                sendSMS(recNumString, msgString);

                // finish
                finish();
            } else
                Toast.makeText(getBaseContext(), "Please enter phone number, secret key and the message. Secret key must be 16 characters!",
                        Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * @param recNumString
     * @param encryptedMsg
     */
    public static void sendSMS(String recNumString, String encryptedMsg) {

        try {
            // get a SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            // Message may exceed 160 characters
            // need to divide the message into multiples
            ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);
            smsManager.sendMultipartTextMessage(recNumString, null, parts, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // utility function

    /**
     * @param b
     * @return hs.toUpperCase()
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }

    // encryption function

    /**
     * This function used to encrypt SMS
     *
     * @param secretKeyString
     * @param msgContentString
     * @return returnArray
     */
    public static byte[] encryptSMS(String secretKeyString, String msgContentString) {
        try {
            byte[] returnArray;
            // generate AES secret key from user input
            Key key = generateKey(secretKeyString);

            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("AES");

            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);

            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());
            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }
    }

    /**
     * This function used to generate key
     *
     * @param secretKeyString
     * @return key
     * @throws Exception
     */
    private static Key generateKey(String secretKeyString) throws Exception {
        // generate secret key from string
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }

}
