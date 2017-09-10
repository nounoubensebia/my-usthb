package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {
    private View downloadingLayout;
    private Button startDownloadingButton;
    private TextView downloadCompleteTextView;
    private View welcomeLayout;
    private ProgressBar progressBar;
    private Button laterButton;
    private View downloadCompleteLayout;
    private Button nextButton;
    private OfflineRegion offlineRegion;
    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


    private void rootToNextActivity ()
    {
        Intent intent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            intent = new Intent(this,AuthorizationActivity.class);

        }
        else
        {
            intent = new Intent(WelcomeActivity.this, PreparationActivity.class);
        }
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!fileExistance("timing")&&!SharedPreference.verifyKey("INFO",this)&&!SharedPreference.verifyKey("TEMP",this)) {
            if (SharedPreference.verifyKey("map_downloaded",this))
            {
                rootToNextActivity();
            }
            //Toast.makeText(MainActivity.this, "Le fichier timing existe déjà", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Intent i = new Intent(WelcomeActivity.this, StartActivity.class);
            startActivity(i);
            finish();
        }


        Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        laterButton = (Button) findViewById(R.id.button_later);
        downloadingLayout = findViewById(R.id.layout_progress);
        startDownloadingButton = (Button) findViewById(R.id.button_download);
        downloadCompleteTextView = (TextView) findViewById(R.id.text_download_complete);
        welcomeLayout = findViewById(R.id.layout_welcome);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nextButton = (Button) findViewById(R.id.button_next);
        downloadCompleteLayout = findViewById(R.id.layout_download_finished);
        startDownloadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloadingButton.setVisibility(View.GONE);
                downloadingLayout.setVisibility(View.VISIBLE);
                downloadRegion();
            }
        });
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offlineRegion!=null)
                {
                    offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                }
                rootToNextActivity();

            }
        });




    }

    private void downloadRegion ()
    {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(36.718693915273185+0.005,3.191249370574951)) // Northeast
                .include(new LatLng(36.7054788373048-0.005,3.1696924567222595))
                //.include(new LatLng(36.705311107749274,3.16880464553833)) // Southwest
                //.include(new LatLng(37.9097, -119.5073)) // Northeast
                //.include(new LatLng(37.6744, -119.6815))
                .build();
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                "mapbox://styles/mapbox/streets-v9",
                latLngBounds,
                14,
                20,
                WelcomeActivity.this.getResources().getDisplayMetrics().density);



        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name23", "USTHB2589");
            String json = jsonObject.toString();
            metadata = json.getBytes();
        } catch (Exception exception) {
            metadata = null;
        }






        OfflineManager offlineManager = OfflineManager.getInstance(this);
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                WelcomeActivity.this.offlineRegion = offlineRegion;
                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                    @Override
                    public void onStatusChanged(OfflineRegionStatus status) {
                        //downloadCompleteTextView.setVisibility(View.VISIBLE);
                        //downloadCompleteTextView.setText(status.getRequiredResourceCount()+"");
                        double percentage = status.getRequiredResourceCount() >= 0
                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                0.0;
                        progressBar.setProgress((int)percentage);
                        if (status.isComplete())
                        {
                            welcomeLayout.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    downloadCompleteLayout.setVisibility(View.VISIBLE);
                                    SharedPreference.saveString("map_downloaded","true",WelcomeActivity.this);
                                }
                            },250);
                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {

                    }

                    @Override
                    public void mapboxTileCountLimitExceeded(long limit) {

                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootToNextActivity();
            }
        });
    }
}
