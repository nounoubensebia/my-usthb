package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class SetMarkerActivity extends AppCompatActivity {
    private CustomMapView mCustomMapView;
    private Button mConfirmButton;

    public static final int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        createMap(savedInstanceState);

    }

    private void createMap(Bundle savedInstanceState) {

        final MapView mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());
                MapboxMap mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull final LatLng point) {

                    }
                });
                mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull final LatLng point) {
                        mConfirmButton.setText("Confirmer");
                        mCustomMapView.getMapboxMap().removeAnnotations();
                        mCustomMapView.drawMarker(new Coordinate(point),"Position sélectionnée",R.drawable.ic_marker_red_24dp);

                        mCustomMapView.animateCamera(new Coordinate(point),16);
                        mConfirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startAskingActivity(new Coordinate(point));
                            }
                        });
                    }
                });
                mapboxMap.setMyLocationEnabled(true);
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
            }
        });
    }

    private void startAskingActivity (Coordinate coordinate)
    {
        Place place = new Place("Prés de la faculté de chimie",coordinate);
        Intent date = new Intent();
        date.putExtra("place",place.toJson());
        setResult(RESULT_OK,date);
        finish();
    }
}
