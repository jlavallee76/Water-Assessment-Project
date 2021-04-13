package com.example.wap.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.wap.AboutFragment1;
import com.example.wap.AboutFragment2;
import com.example.wap.AboutFragment3;
import com.example.wap.MainActivity;
import com.example.wap.R;

public class SplashScreen extends AppCompatActivity {

    ImageView logo, splashBackground;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.wap_logo);
        splashBackground = findViewById(R.id.splash_bg);
        lottieAnimationView = findViewById(R.id.sea_waves_animation);

        splashBackground.animate().setDuration(1000).setStartDelay(4000);
        logo.animate().setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().setDuration(1000).setStartDelay(4000);

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