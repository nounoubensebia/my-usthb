package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private Coordinate selectedPlace;
    public static final int RESULT_OK = 1;
    private TextView myPositionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        myPositionText = (TextView) findViewById(R.id.text_my_position);
        createMap(savedInstanceState);
        myPositionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coordinate myLocation;
                if (mCustomMapView.getMapboxMap().getMyLocation()!=null)
                {
                myLocation = new Coordinate(mCustomMapView.getMapboxMap().getMyLocation().getLatitude(),
                        mCustomMapView.getMapboxMap().getMyLocation().getLongitude());

                    if (myLocation.isInsideCampus())
                    {
                        mCustomMapView.animateCamera(myLocation,18);
                    }
                    else
                    {
                        Toast.makeText(SetMarkerActivity.this,"Vous etes en dehors du campus",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void createMap(Bundle savedInstanceState) {

        final MapView mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        final Bundle bundle = getIntent().getExtras();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_EAST_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_WEST_BOUND.getMapBoxLatLng());
                MapboxMap mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
                final int requestCode = bundle.getInt("requestCode");
                if (bundle.containsKey("departure"))
                {
                    Place departure = Place.fromJson(bundle.getString("departure"));
                    //if (!departure.isMyLocation())
                    mCustomMapView.drawMarker(departure.getCoordinate(),"Lieu de départ",R.drawable.ic_marker_blue_24dp);
                    mCustomMapView.moveCamera(departure.getCoordinate());
                }
                if (bundle.containsKey("destination"))
                {

                    Place departure = Place.fromJson(bundle.getString("destination"));
                    if (!departure.isUserLocation())
                    mCustomMapView.drawMarker(departure.getCoordinate(),"Lieu de départ",R.drawable.ic_marker_red_24dp);
                    mCustomMapView.moveCamera(departure.getCoordinate());
                }
                mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull final LatLng point) {
                        mConfirmButton.setText("Confirmer");
                        if (selectedPlace!=null)
                        mCustomMapView.removeMarker(selectedPlace);
                        if (requestCode == SearchQueryTwoActivity.REQUEST_DEPARTURE_CODE)
                        mCustomMapView.drawMarker(new Coordinate(point),"Position sélectionnée",R.drawable.ic_marker_blue_24dp);
                        else
                        mCustomMapView.drawMarker(new Coordinate(point),"Position sélectionnée",R.drawable.ic_marker_red_24dp);
                        mCustomMapView.animateCamera(new Coordinate(point),16);
                        selectedPlace = new Coordinate(point);
                        mConfirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startAskingActivity(new Coordinate(point));
                            }
                        });
                    }
                });
                mapboxMap.setMyLocationEnabled(true);

            }
        });
    }

    private void startAskingActivity (Coordinate coordinate)
    {
        Place place = new Place("Mon marqueur",coordinate,false);
        Intent date = new Intent();
        date.putExtra("place",place.toJson());
        setResult(RESULT_OK,date);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onPause();
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onSaveInstanceState(outState);
    }
}
