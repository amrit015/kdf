package com.silptech.kdf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Amrit on 2/9/2016.
 */
public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;  //splash screen timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(main);
                // closes activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}