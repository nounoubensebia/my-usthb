package com.example.nouno.locateme.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.locateme.Data.Bloc;
import com.example.nouno.locateme.Data.Classroom;
import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.Djikstra.GraphCreator;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Utils.MapGeometryUtils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SearchQueryTwoActivity extends AppCompatActivity {
    private TextView mSetPositionOnMapTextView;
    private EditText departureEditText;
    private EditText destinationEditText;
    private TextView arrivalTimeText;
    private TextView distancedurationText;
    private LinearLayout pathLayout;
    private Path mPath = new Path();
    private View appBarLayout;
    private View pathCalculProgress;
    private CustomMapView mCustomMapView;
    private View pathNotFoundLayout;
    private View pathFoundLayout;
    private View coordinateLayout;
    private boolean backPressed = false;
    private boolean targetFixed = false;
    private Path mTarget;
    private View fab;
    private View scrollView;
    public static final int REQUEST_DEPARTURE_CODE = 0;
    public static final int REQUEST_DESTINATION_CODE = 1;
    private int state;
    private Graph mGraph;
    public static final int STATE_NO_PATH = 0;
    public static final int STATE_PATH_INITIALIZED = 1;
    public static final int STATE_PATH_CALCULATED = 2;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private boolean keyboardShown;
    private TextView useCurrentLocationText;
    private ArrayList<Bloc> blocs;

    private void testBlocks ()
    {
        blocs = new ArrayList<>();
        ArrayList<Classroom> classrooms = new ArrayList<>();
        classrooms.add(new Classroom("Salle 228","Desc"));
        blocs.add(new Bloc(null,"Faculté d'informatique",classrooms));
        blocs.add(new Bloc(null,"Faculté d'életronique",classrooms));
        blocs.add(new Bloc(null,"Faculté de biologie",classrooms));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        testBlocks();

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
                           //updateUiState(state,true,true);
                           keyboardShown = false;
                           //if (state > STATE_NO_PATH)
                               //updateUiState(state,false,true);
                       }
                       else
                       {
                           keyboardShown = true;
                           //updateUiState(state,false,false);
                           //if (state>STATE_NO_PATH)
                           //updateUiState(state,true,true);
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
                {

                    if (mPath.getDestination()!=null)
                    {
                        i.putExtra("destination",mPath.getDestination().toJson());
                    }
                    i.putExtra("requestCode",REQUEST_DEPARTURE_CODE);
                    startActivityForResult(i,REQUEST_DEPARTURE_CODE);
                }
                else {

                    if (mPath.getSource()!=null)
                    {
                        i.putExtra("departure",mPath.getSource().toJson());
                    }
                    i.putExtra("requestCode",REQUEST_DESTINATION_CODE);
                    startActivityForResult(i, REQUEST_DESTINATION_CODE);
                }
            }
        });
        departureEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    if (mPath.getSource()!=null)
                    departureEditText.setText(mPath.getSource().getLabel());
                    if (state>STATE_NO_PATH)
                    {
                        departureEditText.setText(mPath.getSource().getLabel());
                        if (mPath.getDestination()!=null)
                        {
                            destinationEditText.setFocusableInTouchMode(false);
                        }
                    }
                }
                if (hasFocus)
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
        departureEditText.requestFocus();
        useCurrentLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomMapView.getMapboxMap().getMyLocation()!=null)
                    {
                    Coordinate coordinate = new Coordinate(mCustomMapView.getMapboxMap().getMyLocation().getLatitude(),
                            mCustomMapView.getMapboxMap().getMyLocation().getLongitude());
                    if (coordinate.isInsideCampus())
                    {
                        if (departureEditText.hasFocus())
                        {
                            setDeparture(new Place("Ma position",coordinate,true));
                        }
                        else {
                            if (destinationEditText.hasFocus()) {
                                setDestination(new Place("Ma position", coordinate,true));
                            }
                        }
                        updateUiState(state,true,true);
                    }
                    else
                    {
                        Toast.makeText(SearchQueryTwoActivity.this,"Vous ne pouvez pas utiliser votre position" +
                                "vous etes en dehors du campus",Toast.LENGTH_LONG).show();
                    }
                }
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
        distancedurationText = (TextView)findViewById(R.id.text_duration_distance);
        arrivalTimeText = (TextView)findViewById(R.id.text_arrival_time);
        useCurrentLocationText = (TextView)findViewById(R.id.text_use_current_location);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SetMarkerActivity.RESULT_OK)
        {
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
            updateUiState(state,false,false);
            if (state > STATE_NO_PATH)
            {
                reInitMap();
            }
        }
    }

    private void setDeparture (Place departure)
    {
        if (mPath.getDestination()!=null)
        {
            state = STATE_PATH_INITIALIZED;
        }
        //departure.setLabel("Prés de la faculté de chimie");
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
                updateUiState(state,true,true);
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
            state = STATE_PATH_INITIALIZED;
        }
    }

    private void setDestination (Place destination)
    {
        if (mPath.getSource()!=null)
        {
            state = STATE_PATH_INITIALIZED;
        }
        //destination.setLabel("Prés de la faculté de mathématiques");
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
                updateUiState(state,true,true);
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
            state = STATE_PATH_INITIALIZED;
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

        final MapView mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());
                mapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
                mapboxMap.getUiSettings().setAllGesturesEnabled(true);
                mapboxMap.setMyLocationEnabled(true);

            }
        });
    }

    private void reInitMap ()
    {
        //mCustomMapView.moveCamera(new Coordinate(36.712126,3.178768),18);

    }



    private void getPath()
    {
        pathCalculProgress.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGraph==null)
                {
                    getGraph();
                }
                //getGraph();
                mGraph.getShortestPath(mPath, mCustomMapView.getMapboxMap().getProjection(), new OnSearchFinishListener() {
                    @Override
                    public void OnSearchFinish(Graph graph) {
                        updateUiState(STATE_PATH_CALCULATED,false,true);
                        state = STATE_PATH_CALCULATED;
                        mPath.setDistance((float) graph.getWeight());
                        distancedurationText.setText(mPath.getDurationString()+" "+mPath.getDistanceString());
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                        Date currentLocalTime = cal.getTime();
                        long time = currentLocalTime.getTime();
                        time+=mPath.getDuration()*1000;
                        mPath.setGraph(graph);
                        currentLocalTime = new Time(time);
                        DateFormat date = new SimpleDateFormat("HH:mm");
                        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
                        String localTime = date.format(currentLocalTime);
                        arrivalTimeText.setText("Arrivée à "+localTime);
                        createMap();
                        mCustomMapView.drawPolyline(graph);

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Gson gson = new Gson();
                                String navigationInstructionsJson = gson.toJson(navigationInstructions);
                                Intent i = new Intent(SearchQueryTwoActivity.this,NavigationActivity.class);

                                i.putExtra("path",mPath.toJson());
                                startActivity(i);
                            }
                        });
                    }
                });
                /*mGraph.getShortestPath(mGraph.getVertexes().get(5), mGraph.getVertexes().get(10), new OnSearchFinishListener() {
                    @Override
                    public void OnSearchFinish(Graph graph) {
                        updateUiState(STATE_PATH_CALCULATED,false,true);
                        state = STATE_PATH_CALCULATED;
                        mPath.setDistance((float) graph.getWeight());
                        distancedurationText.setText(mPath.getDurationString()+" "+mPath.getDistanceString());
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                        Date currentLocalTime = cal.getTime();
                        long time = currentLocalTime.getTime();
                        time+=mPath.getDuration()*1000;
                        mPath.setGraph(graph);
                        currentLocalTime = new Time(time);
                        DateFormat date = new SimpleDateFormat("HH:mm");
                        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
                        String localTime = date.format(currentLocalTime);
                        arrivalTimeText.setText("Arrivée à "+localTime);
                        createMap();
                        mCustomMapView.drawPolyline(graph);

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Gson gson = new Gson();
                                String navigationInstructionsJson = gson.toJson(navigationInstructions);
                                Intent i = new Intent(SearchQueryTwoActivity.this,NavigationActivity.class);

                                i.putExtra("path",mPath.toJson());
                                startActivity(i);
                            }
                        });
                    }
                });*/

            }
        },0);
    }

    private void animateFab()
    {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fab_animation);
        animation.setRepeatCount(Animation.INFINITE);
        fab.startAnimation(animation);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getGraph() {
        try {
            String graphJson = FileUtils.readFile(SearchQueryTwoActivity.this.getAssets().open("GraphJson.txt"));
            //Graph graph = GraphCreator.createGraph(graphJson);
            mGraph = new Graph(graphJson);

            //String json = FileUtils.readFile(this.getAssets().open("testGraph.txt"));
            //mGraph = GraphCreator.createGraph(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUiState (int newState,boolean keyboardShown,boolean animate)
    {
        switch (newState)
        {
            case STATE_NO_PATH :
                state = STATE_NO_PATH;
                if (!animate)
                {
                    coordinateLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
                else
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
                break;
            case STATE_PATH_INITIALIZED :
                state = STATE_PATH_INITIALIZED;
                if (!animate)
                {
                    if (!keyboardShown)
                    {
                        coordinateLayout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        pathFoundLayout.setVisibility(View.GONE);
                        pathNotFoundLayout.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                        pathCalculProgress.setVisibility(View.GONE);
                    }
                    else
                    {
                        coordinateLayout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                    }
                    createMap();
                }
                else
                {
                    if (!keyboardShown)
                    {
                        fab.setVisibility(View.GONE);
                        pathCalculProgress.setVisibility(View.GONE);
                        scrollView.setVisibility(View.GONE);
                        pathFoundLayout.setVisibility(View.GONE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                coordinateLayout.setVisibility(View.VISIBLE);
                            }
                        },250);

                    }
                    else
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
                    createMap();
                }
                //createMap();
                break;
            case STATE_PATH_CALCULATED :
                state = STATE_PATH_CALCULATED;
                if (animate)
                {
                    if (!keyboardShown)
                    {
                        coordinateLayout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        pathNotFoundLayout.setVisibility(View.GONE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pathFoundLayout.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.VISIBLE);
                                animateFab();
                            }
                        },250);

                    }
                    else
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

                }
                else
                {
                    if (!keyboardShown)
                    {
                        coordinateLayout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        pathNotFoundLayout.setVisibility(View.GONE);
                        pathFoundLayout.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        animateFab();
                        createMap();
                    }
                    else
                    {
                        coordinateLayout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    private void createMap ()
    {
            targetFixed = false;
            mCustomMapView.getMapboxMap().removeAnnotations(mCustomMapView.getMapboxMap().getAnnotations());
            mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Lieu de départ",R.drawable.ic_marker_blue_24dp);

                mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"destination",R.drawable.ic_marker_red_24dp);
            ArrayList<Coordinate> boundsCoordinates = new ArrayList<>();

            final Coordinate middlePoint = MapGeometryUtils.getMiddle(mPath.getSource().getCoordinate(),mPath.getDestination().getCoordinate());
            if (mPath.getGraph()!=null)
                mCustomMapView.animateCamera(mPath.getGraph(),350);
                else
                {
                    boundsCoordinates.add(mPath.getSource().getCoordinate());
                    boundsCoordinates.add(mPath.getDestination().getCoordinate());
                    mCustomMapView.moveCamera(boundsCoordinates,350);
                }
    }

    @Override
    public void onBackPressed() {
        if (state == STATE_NO_PATH||keyboardShown)
            super.onBackPressed();
        else
        {
            if (coordinateLayout.getVisibility()==View.GONE)
            {
                updateUiState(state,false,true);
                if (departureEditText.hasFocus())
                {

                    departureEditText.setText(mPath.getSource().getLabel());
                    departureEditText.setFocusableInTouchMode(false);
                    departureEditText.clearFocus();

                }
                if (destinationEditText.hasFocus())
                {

                    destinationEditText.setText(mPath.getDestination().getLabel());
                    destinationEditText.setFocusableInTouchMode(false);
                    destinationEditText.clearFocus();

                }
            }
            else
            {
                super.onBackPressed();
            }

        }
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
}
