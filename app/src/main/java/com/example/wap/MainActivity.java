package com.example.wap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liquid Swipe
        ViewPager viewPager = findViewById(R.id.liquid_pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("MH", "Nav item selected.");
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        break;
                    case R.id.navigation_map:

                        intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.navigation_search:
                        intent = new Intent(MainActivity.this, CarouselActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.navigation_settings:

                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        break;
                }
                return false;
            }
        });
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        private static final int NUM_PAGES = 3;

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new AboutFragment1();
                case 1:
                    return new AboutFragment2();
                case 2:
                    return new AboutFragment3();
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}

