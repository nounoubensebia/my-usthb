package com.example.nouno.locateme.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity {
    MapView mMapView;
    MapboxMap mMapboxMap;

    private TextView whereToGoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        setContentView(R.layout.activity_map);
        whereToGoText = (TextView)findViewById(R.id.where_to_go);
        whereToGoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this,SearchQueryTwoActivity.class);
                startActivity(i);
            }
        });
        createMap(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                3);
    }
    private void createMap(Bundle savedInstanceState) {

        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());

                mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                //getGraph();
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                  //      createChooseMarkerTypeDialog(new Coordinate(point));
                    }
                });
                mapboxMap.setMyLocationEnabled(true);
            }
        });
    }
}
