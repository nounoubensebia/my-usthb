package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONObject;

public class DownloadActivity extends AppCompatActivity {

    private View downloadlingLayout;
    private View errorLayout;
    private View dowloadCompleteLayout;

    private Button downloadingButton;
    private Button errorButton;
    private Button downloadCompleteButton;
    private Button cancelButton;
    OfflineRegion offlineRegion;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadlingLayout = findViewById(R.id.layout_downloading);
        errorLayout = findViewById(R.id.layout_error);
        dowloadCompleteLayout = findViewById(R.id.layout_download_finished);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadingButton = (Button) findViewById(R.id.button_later);
        errorButton = (Button) findViewById(R.id.button_error);
        downloadCompleteButton = (Button) findViewById(R.id.button_download_finished);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        downloadingButton = (Button) findViewById(R.id.button_later);
        downloadRegion();
        downloadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStartActivity();
                if (offlineRegion!=null)
                {
                    offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
                }
            }
        });

        downloadCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStartActivity();
            }
        });
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadlingLayout.setVisibility(View.VISIBLE);
                        SharedPreference.saveString("map_downloaded","true",DownloadActivity.this);
                    }
                },250);
                downloadRegion();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStartActivity();
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
                "mapbox://styles/mapbox/outdoors-v10",
                latLngBounds,
                14,
                20,
                DownloadActivity.this.getResources().getDisplayMetrics().density);



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
            public void onCreate(final OfflineRegion offlineRegion) {
                DownloadActivity.this.offlineRegion = offlineRegion;
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
                            downloadlingLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dowloadCompleteLayout.setVisibility(View.VISIBLE);
                                    SharedPreference.saveString("map_downloaded","true",DownloadActivity.this);
                                }
                            },250);
                        }
                    }

                    @Override
                    public void onError(OfflineRegionError error) {
                        downloadlingLayout.setVisibility(View.GONE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                errorLayout.setVisibility(View.VISIBLE);

                            }
                        },250);
                        offlineRegion.setDownloadState(OfflineRegion.STATE_INACTIVE);
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

    }
    void startStartActivity ()
    {
        Intent intent = new Intent(this,StartActivity.class);
        startActivity(intent);
        finish();
    }
}
