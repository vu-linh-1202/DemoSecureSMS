package com.example.demosecuresms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Create by Vu Trong Linh
 * Date: 9/23/2022
 */
public class DisplaySMSActivity extends Activity {
    EditText secretKey;
    TextView senderNumber, encryptedMsg, decryptedMsg;
    Button submit, cancel;
    String originNumber = "";
    String msgContent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onreceive);
        senderNumber = findViewById(R.id.senderNumber);

        encryptedMsg = findViewById(R.id.encryptedMsg);

        decryptedMsg = findViewById(R.id.decryptedMsg);

        secretKey = findViewById(R.id.secretKey);

        submit = findViewById(R.id.submit);

        cancel = findViewById(R.id.cancel);

        //get the Intent extra
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // get the sender phone number from extra
            originNumber = extras.getString("originNumber");

            //get the encrypted message body from extra
            msgContent = extras.getString("msgContent");

            // set the text fields in the UI
            senderNumber.setText(originNumber);
            encryptedMsg.setText(msgContent);
        } else {
            // if the Intent is null, there should be something wrong
            Toast.makeText(getBaseContext(), "Error Occurs", Toast.LENGTH_SHORT).show();
            finish();
        }
        // when click on the cancel button, return
        cancel.setOnClickListener(view -> finish());

        // when click on the submit button decrypt the message body
        submit.setOnClickListener(view -> {
            // user input the AES secret key
            String secretKeyString = secretKey.getText().toString();

            // key length should be 16 characters as defined by AES-128-bit
            if (secretKeyString.length() > 0 && secretKeyString.length() == 16) {
                try {
                    //convert the encrypt string message body to a byte
                    //array
                    byte[] msg = hex2byte(msgContent.getBytes());

                    // decrypt the by array
                    byte[] result = decryptSMS(secretKey.getText().toString(), msg);

                    //set the text view for the decrypted message
                    decryptedMsg.setText(new String(result));

                } catch (Exception e) {
                    // in the case of massage corrupted or invalid key
                    // decryption cannot be carried out
                    decryptedMsg.setText("Message cannot be decrypted!");
                }
            } else {
                Toast.makeText(getBaseContext(), "You must provide a 16-character secret key!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * convert hex array to byte array
     *
     * @param b
     * @return b2
     */
    public static byte[] hex2byte(byte[] b) {
        if (b.length % 2 != 0) throw new IllegalArgumentException("hello");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * Decrypt short message service SMS
     *
     * @param secretKeyString
     * @param encryptedMsg
     * @return decValue
     * @throws Exception
     */
    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg) throws Exception {
        // generate AES secret key from the user input secret key
        Key key = generateKey(secretKeyString);

        // get the cipher algorithm for AES
        Cipher c = Cipher.getInstance("AES");

        // specify the decryption mode
        c.init(Cipher.DECRYPT_MODE, key);

        // decrypt the message
        byte[] decValue = c.doFinal(encryptedMsg);
        return decValue;
    }

    /**
     * this function used to generate key
     *
     * @param secretKeyString
     * @return key
     * @throws Exception
     */
    private static Key generateKey(String secretKeyString) throws Exception {
        // generate AES secret key from a String
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }
}
