package com.example.nouno.locateme.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.R;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class SearchQueryTwoActivity extends AppCompatActivity {
    private TextView mSetPositionOnMapTextView;
    private EditText departureEditText;
    private EditText destinationEditText;
    private LinearLayout pathLayout;
    private Path mPath = new Path();
    private View appBarLayout;
    private View pathCalculProgress;
    private View pathNotFoundLayout;
    private View pathFoundLayout;
    private View coordinateLayout;
    private View fab;
    private View scrollView;
    public static int REQUEST_DEPARTURE_CODE = 0;
    public static int REQUEST_DESTINATION_CODE = 1;
    private int state;
    public static int STATE_NO_PATH = 0;
    public static int STATE_PATH_INITIALIZED = 1;
    public static int STATE_PATH_CALCULATED = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query_two);
        getSupportActionBar().setElevation(0);
        getViews();
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                       if (!isOpen)
                       {
                           if (state > STATE_NO_PATH)
                           {
                               addMapLayout();
                           }
                       }
                    }
                });
        createMap(savedInstanceState);
        mSetPositionOnMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
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
                if (!hasFocus)
                {
                    if (mPath.getSource()!=null)
                    {
                        departureEditText.setText(mPath.getSource().getLabel());
                        if (state > STATE_NO_PATH)
                        {
                            destinationEditText.setFocusableInTouchMode(false);
                            addMapLayout();
                        }

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
                    if (state > STATE_NO_PATH)
                    {
                        destinationEditText.setText(mPath.getDestination().getLabel());
                        if (mPath.getSource()!=null)
                        {
                            departureEditText.setFocusableInTouchMode(false);
                            addMapLayout();
                        }
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
                getPath();
            }
        });
    }



    private void getViews ()
    {
        departureEditText = (EditText)findViewById(R.id.departure_text);
        destinationEditText = (EditText)findViewById(R.id.destination_text);
        mSetPositionOnMapTextView = (TextView)findViewById(R.id.set_position_on_map);
        appBarLayout = findViewById(R.id.app_bar_layout);
        pathLayout = (LinearLayout)findViewById(R.id.path_info_layout);
        pathCalculProgress = (ProgressBar)findViewById(R.id.path_progress_bar);
        pathFoundLayout = findViewById(R.id.path_calculated_layout);
        pathNotFoundLayout = findViewById(R.id.path_initialized_layout);
        fab = findViewById(R.id.floating);
        coordinateLayout = findViewById(R.id.coordinate_layout);
        scrollView = findViewById(R.id.scrollView);
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
        initLayout();
    }

    private void setDeparture (Place departure)
    {
        if (mPath.getDestination()!=null)
        {
            state = STATE_PATH_INITIALIZED;
        }
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
                if (state>STATE_NO_PATH)
                removeMapLayout();
                showKeyboard(departureEditText,0);
            }
        });

        if (state == STATE_NO_PATH)
        {
            destinationEditText.requestFocus();
            showKeyboard(destinationEditText,1);
        }
        else
        {
            destinationEditText.clearFocus();
            destinationEditText.setFocusableInTouchMode(false);
            hideKeyboard();
            appBarLayout.clearFocus();
            addMapLayout();
        }
    }

    private void setDestination (Place destination)
    {
        if (mPath.getSource()!=null)
        {
            state = STATE_PATH_INITIALIZED;
        }
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
                if (mPath.getSource()!=null)
                    removeMapLayout();
                showKeyboard(departureEditText,1);
            }
        });
        if (state == STATE_NO_PATH)
        {
            departureEditText.requestFocus();
            showKeyboard(departureEditText,0);
        }
        else
        {
            departureEditText.clearFocus();
            departureEditText.setFocusableInTouchMode(false);
            appBarLayout.clearFocus();
            hideKeyboard();
            addMapLayout();
        }
    }
    public void hideKeyboard() {
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
                mapboxMap.setLatLngBoundsForCameraTarget(builder.build());

            }
        });
    }
    private void addMapLayout ()
    {
        scrollView.setVisibility(View.GONE);
        pathNotFoundLayout.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coordinateLayout.setVisibility(View.VISIBLE);
            }
        },250);


    }
    private void addPathFoundLayout ()
    {
        pathNotFoundLayout.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pathFoundLayout.setTag(pathFoundLayout.getVisibility());
                pathFoundLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                animateFab();
            }
        },300);

    }

    private void removePathCalculatedLayout ()
    {
        fab.setVisibility(View.GONE);
        pathCalculProgress.setVisibility(View.GONE);
        pathFoundLayout.setVisibility(View.GONE);
        pathNotFoundLayout.setVisibility(View.VISIBLE);
    }

    private void removeMapLayout()
    {
        coordinateLayout.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.setVisibility(View.VISIBLE);
            }
        },250);
    }

    private void getPath()
    {
        pathCalculProgress.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addPathFoundLayout();state = STATE_PATH_CALCULATED;
            }
        },2000);
    }
    private void initLayout ()
    {
        if (state == STATE_NO_PATH)
        {
            coordinateLayout.setVisibility(View.GONE);
            pathLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
        if (state == STATE_PATH_INITIALIZED)
        {
            //animateFab();
            coordinateLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            pathLayout.setVisibility(View.VISIBLE);
            pathFoundLayout.setVisibility(View.GONE);
            pathNotFoundLayout.setVisibility(View.VISIBLE);
            pathCalculProgress.setVisibility(View.GONE);
        }
        if (state == STATE_PATH_CALCULATED)
        {

            fab.setVisibility(View.VISIBLE);
            coordinateLayout.setVisibility(View.VISIBLE);
            pathLayout.setVisibility(View.VISIBLE);
            pathNotFoundLayout.setVisibility(View.GONE);
            pathFoundLayout.setVisibility(View.VISIBLE);
        }
    }

    private void animateFab()
    {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fab_animation);
        animation.setRepeatCount(Animation.INFINITE);
        fab.startAnimation(animation);
    }

}
