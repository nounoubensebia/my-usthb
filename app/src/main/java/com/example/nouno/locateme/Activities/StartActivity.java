package com.example.nouno.locateme.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.locateme.Fragments.AgendaFragment;
import com.example.nouno.locateme.Fragments.MapFragment;
import com.example.nouno.locateme.Fragments.SettingsFragment;
import com.example.nouno.locateme.Fragments.TimeTableFragment;
import com.example.nouno.locateme.R;

public class StartActivity extends AppCompatActivity {

    private TextView mTextMessage;
    MapFragment mapFragment;
    AgendaFragment timeTableFragment;
    SettingsFragment settingsFragment;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_agenda:
                    loadTimeFragment();
                    return true;
                case R.id.navigation_search:
                    loadMapFragment();

                    return true;
                case R.id.navigation_settings:
                    loadSettingsFragment();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadMapFragment();
    }

    void loadMapFragment ()
    {
        if (mapFragment == null)
        {
            mapFragment = new MapFragment();

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mapFragment).commit();
        if (timeTableFragment != null)
            getSupportFragmentManager().beginTransaction().remove(timeTableFragment).commit();
        if (settingsFragment != null)
            getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();

    }

    void loadTimeFragment ()
    {
        if (timeTableFragment == null)
        {
            timeTableFragment = new AgendaFragment();

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, timeTableFragment).commit();
        if (mapFragment != null)
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
        if (settingsFragment != null)
            getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();

    }

    void loadSettingsFragment ()
    {
        if (settingsFragment == null)
        {
            settingsFragment = new SettingsFragment();

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, settingsFragment).commit();
        if (timeTableFragment != null)
            getSupportFragmentManager().beginTransaction().remove(timeTableFragment).commit();
        if (mapFragment != null)
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

    public void hideBottomBar ()
    {
        navigation.setVisibility(View.GONE);
    }

    public void showBottomBar ()
    {
        navigation.setVisibility(View.VISIBLE);
    }

}
