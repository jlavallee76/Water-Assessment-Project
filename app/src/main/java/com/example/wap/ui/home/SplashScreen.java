package com.example.wap.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.wap.MainActivity;
import com.example.wap.R;

public class SplashScreen extends AppCompatActivity {

    ImageView logo, appName, splashBackground;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.wap_logo);
        splashBackground = findViewById(R.id.splash_bg);
        lottieAnimationView = findViewById(R.id.sea_waves_animation);

        splashBackground.animate().translationY(2500).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(1425).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent =new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}