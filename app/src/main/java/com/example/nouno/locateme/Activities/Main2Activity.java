package com.example.nouno.locateme.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.example.nouno.locateme.Utils.FileUtils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MapView mMapView;
    MapboxMap mMapboxMap;
    CustomMapView mCustomMapView;
    ImageView button;
    private TextView whereToGoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        setContentView(R.layout.activity_main2);
        button = (ImageView) findViewById(R.id.btn);
        whereToGoText = (TextView)findViewById(R.id.where_to_go);
        whereToGoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main2Activity.this,SearchQueryTwoActivity.class);
                startActivity(i);
            }
        });
        createMap(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                3);



        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





    }

    private void createMap(Bundle savedInstanceState) {

        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());

                mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                //mMapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                //mMapboxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                //getGraph();
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        //      createChooseMarkerTypeDialog(new Coordinate(point));
                    }
                });
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
                mapboxMap.setMyLocationEnabled(true);
                try {
                    String graphJson = FileUtils.readFile(Main2Activity.this.getAssets().open("GraphJson.txt"));
                    //Graph graph = GraphCreator.createGraph(graphJson);
                    //Graph graph = new Graph(graphJson);
                    //mCustomMapView.drawFromGraph(graph);
                    //mCustomMapView.drawPolyline(graph);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.card) {
            // Handle the camera action
        } else if (id == R.id.time_table) {
            Intent i = new Intent(Main2Activity.this,SearchQueryTwoActivity.class);
            startActivity(i);

        } else if (id == R.id.settings) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
