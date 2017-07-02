package com.example.nouno.locateme.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.nouno.locateme.Fragments.AgendaFragment;
import com.example.nouno.locateme.Fragments.SearchFragment;
import com.example.nouno.locateme.Fragments.SettingsFragment;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Utils.Parseur;
import com.mapbox.mapboxsdk.Mapbox;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {


    private SearchFragment mSearchFragment;
    private AgendaFragment mAgendaFragment;
    private SettingsFragment mSettingsFragment;
    private BottomNavigationView mNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    loadSearchFragment();
                    return true;
                case R.id.navigation_agenda:
                    loadAgendaFragment();
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
        Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        setContentView(R.layout.activity_main);
        requestLocationPermission();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadSearchFragment();
        //getSupportActionBar().setElevation(0);
        //getSupportActionBar().setTitle("Ou aller ?");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }
    private void loadSearchFragment ()
    {
        //getSupportActionBar().setTitle("Rechercher");
        if (mSearchFragment==null)
        mSearchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mSearchFragment).commit();
        if (mAgendaFragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(mAgendaFragment).commitAllowingStateLoss();
        }
        if (mSettingsFragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(mSettingsFragment).commitAllowingStateLoss();
        }


    }
    private void loadAgendaFragment ()
    {
        //getSupportActionBar().show();
        mAgendaFragment = new AgendaFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mAgendaFragment).commit();
        if (mSearchFragment!=null)
        {
            //getSupportFragmentManager().beginTransaction().remove(mSearchFragment).commitAllowingStateLoss();
        }
        if (mSettingsFragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(mSettingsFragment).commitAllowingStateLoss();
        }
    }
    private void loadSettingsFragment ()
    {
        //getSupportActionBar().show();

        mSettingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mSettingsFragment).commit();
        if (mSearchFragment!=null)
        {
            //getSupportFragmentManager().beginTransaction().remove(mSearchFragment).commitAllowingStateLoss();
        }
        if (mAgendaFragment!=null)
        {
            getSupportFragmentManager().beginTransaction().remove(mAgendaFragment).commitAllowingStateLoss();
        }
    }

    private void requestLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    12);
        }
    }



}
