package com.example.nouno.locateme.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class SearchQueryTwoActivity extends AppCompatActivity {
    private TextView mSetPositionOnMapTextView;
    private EditText departureEditText;
    private EditText destinationEditText;
    private LinearLayout pathLayout;
    private Path mPath = new Path();
    private View appBarLayout;
    private View pathCalculProgress;
    public static int REQUEST_DEPARTURE_CODE = 0;
    public static int REQUEST_DESTINATION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query_two);
        getSupportActionBar().setElevation(0);
        getViews();
        createMap(savedInstanceState);
        mSetPositionOnMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(SearchQueryTwoActivity.this);
                Intent i = new Intent(SearchQueryTwoActivity.this,SetMarkerActivity.class);
                if (departureEditText.hasFocus())
                    startActivityForResult(i,REQUEST_DEPARTURE_CODE);
                else
                    startActivityForResult(i,REQUEST_DESTINATION_CODE);
            }
        });
        departureEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false)
                {
                    if (mPath.getSource()!=null)
                    {
                        departureEditText.setText(mPath.getSource().getLabel());
                    }
                }
                else
                {
                    departureEditText.setText("");
                }
            }
        });
        destinationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    if (mPath.getDestination()!=null)
                    {
                        destinationEditText.setText(mPath.getDestination().getLabel());
                    }
                }
                if (hasFocus)
                {
                    destinationEditText.setText("");
                }
            }
        });

        pathLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathCalculProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getViews ()
    {
        departureEditText = (EditText)findViewById(R.id.departure_text);
        destinationEditText = (EditText)findViewById(R.id.destination_text);
        mSetPositionOnMapTextView = (TextView)findViewById(R.id.set_position_on_map);
        appBarLayout = findViewById(R.id.app_bar_layout);
        pathLayout = (LinearLayout)findViewById(R.id.path_layout);
        pathCalculProgress = (ProgressBar)findViewById(R.id.path_progress_bar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_DEPARTURE_CODE)
        {
            Place departure = Place.fromJson(data.getStringExtra("place"));
            setDeparture(departure);
        }
        else
        {
            Place destination = Place.fromJson(data.getStringExtra("place"));
            setDestination(destination);
        }
    }

    private void setDeparture (Place departure)
    {
        departure.setLabel("Prés de faculté de chimie");
        mPath.setSource(departure);
        departureEditText.setText(departure.getLabel());
        departureEditText.clearFocus();
        departureEditText.setFocusableInTouchMode(false);
        departureEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                departureEditText.setText("");
                departureEditText.setFocusableInTouchMode(true);
                departureEditText.requestFocus();
                departureEditText.requestFocusFromTouch();
                showKeyboard(departureEditText,0);
            }
        });

        if (mPath.getDestination()==null)
        {
            //destinationEditText.requestFocusFromTouch();
            destinationEditText.requestFocus();
            showKeyboard(destinationEditText,1);
        }
        else
        {
            destinationEditText.clearFocus();
            destinationEditText.setFocusableInTouchMode(false);
            hideKeyboard(this);
            appBarLayout.clearFocus();
        }
    }

    private void setDestination (Place destination)
    {
        destination.setLabel("Prés de faculté de mathématiques");
        mPath.setDestination(destination);
        destinationEditText.clearFocus();
        destinationEditText.setFocusableInTouchMode(false);
        destinationEditText.setText(destination.getLabel());
        destinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationEditText.setText("");
                destinationEditText.setFocusableInTouchMode(true);
                destinationEditText.requestFocus();
                destinationEditText.requestFocusFromTouch();
                showKeyboard(departureEditText,1);
            }
        });
        if (mPath.getSource()==null)
        {
            departureEditText.requestFocus();
            showKeyboard(departureEditText,0);
        }
        else
        {
            departureEditText.clearFocus();
            departureEditText.setFocusableInTouchMode(false);
            appBarLayout.clearFocus();
            hideKeyboard(this);
        }
    }
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showKeyboard (EditText editText,int x)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void createMap(Bundle savedInstanceState) {

        MapView mMapView = (MapView) findViewById(R.id.mapView);

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
