//package com.example.demosecuresms;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//
//import androidx.annotation.Nullable;
//
//public class SplashActivity extends Activity {
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        intViews();
//    }
//
//    private void intViews() {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Intent intent= new Intent(SplashActivity.this, SmsBroadCastReceiver.class);
//            startActivity(intent);
//        }, 1000);
//    }
//}
