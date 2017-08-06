package com.example.nouno.locateme.Activities;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.Data.Navigator;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private ArrayList<NavigationInstructionItem> navigationInstructionItems;
    private ArrayList<Polyline> selectedPolylines = null;
    private CustomMapView mCustomMapView;
    private View pathInstructionsLayout;
    private View pathDuraitonDistanceLayout;
    private TextView pathTitleText;
    private View directionLayout;
    private Path mPath;
    private Navigator mNavigator;
    private boolean followUser = true;
    private View showPathInstructionsListButton;
    private boolean showPathInstructionsList = false;
    private TextView instructionsText;
    private TextView distanceText;
    private TextView durationText;
    private TextView myPositionText;
    private ImageView instructionImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getViews();
        selectedPolylines = new ArrayList<>();
        getIntentInfo();
        createMap(savedInstanceState);

        showPathInstructionsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true)
                    //if (mPath.getSource().isUserLocation())
                    {
                        changeState(!showPathInstructionsList,true);
                        showPathInstructionsList = !showPathInstructionsList;
                    }

            }
        });

    }

    private void getIntentInfo ()
    {
        Bundle extras = getIntent().getExtras();
        mPath = Path.fromJson(extras.getString("path"));

    }

    private void initNavigationMap (boolean animate)
    {

        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {
            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getStartOrder(),navigationInstruction.getEndOrder(),false));
        }
        mNavigator = new Navigator(navigationInstructionItems,mPath.getGraph());
        mCustomMapView.drawPolyline(mPath.getGraph());
        //mCustomMapView.drawPolyline(mNavigator.getCurrentPolyline(),"#37AB30");

        double bearing = mNavigator.getCurrentPolyline().get(0).bearingTo(mNavigator.getCurrentPolyline().get(1));
        if (animate)
            mCustomMapView.animateCamera(getUserLocation(),18,mCustomMapView.getMapboxMap().getMyLocation().getBearing());
        else
            mCustomMapView.moveCamera(getUserLocation(),18,mCustomMapView.getMapboxMap().getMyLocation().getBearing());
        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);
        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
        //instructionsText.setText(mNavigator.getNavigationInstructionItem().getInstructionString());
        //durationText.setText(mNavigator.getRemainingDuration()+"");
        //distanceText.setText(mNavigator.getRemainingDistance()+"");
        startTrackingUser();
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
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                mapboxMap.setMyLocationEnabled(true);
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
                if (isUserInsideCampus()) {
                    mCustomMapView.getMapboxMap().getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                    //mCustomMapView.getMapboxMap().getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                    //
                    // if (mPath.getSource().isUserLocation())
                    if (true)
                        initNavigationMap(false);
                    else
                    {
                        changeState(true,false);
                        Toast.makeText(NavigationActivity.this,"Navigation impossible",Toast.LENGTH_LONG).show();
                        intiMapPathInstructionsList(false);
                    }

                }
                else
                {
                    intiMapPathInstructionsList(false);
                    changeState(true,false);

                }
                myPositionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomMapView.getMapboxMap().getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                        mCustomMapView.getMapboxMap().getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                    }
                });


            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startTrackingUser()
    {
        mNavigator.goTo(mNavigator.getItemByUserLocation(getUserLocation()));
        distanceText.setText(mNavigator.getRemainingDistance()+"");
        durationText.setText(mNavigator.getRemainingDuration()+"");
        NavigationInstructionItem navigationInstructionItem = mNavigator.getNavigationInstructionItem();
        instructionsText.setText(navigationInstructionItem.getInstructionString());
        if (navigationInstructionItem.getDirection()==NavigationInstruction.DIRECTION_RIGHT)
            instructionImage.setImageDrawable(getDrawable(R.drawable.ic_subdirectory_arrow_left_white_24dp));
        if (navigationInstructionItem.getDirection()==NavigationInstruction.DIRECTION_LEFT)
            instructionImage.setImageDrawable(getDrawable(R.drawable.ic_subdirectory_arrow_right_white_24dp));
        if (mNavigator.atLastInstruction())
        {
            instructionImage.setRotation(0);
            instructionImage.setImageDrawable(getDrawable(R.drawable.ic_location_white_24dp));
        }
        mCustomMapView.getMapboxMap().setOnMyLocationChangeListener(new MapboxMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@Nullable Location location) {
                mNavigator.goTo(mNavigator.getItemByUserLocation(new Coordinate(location.getLatitude(),
                        location.getLongitude())));
                distanceText.setText(mNavigator.getRemainingDistance()+"");
                durationText.setText(mNavigator.getRemainingDuration()+"");
                NavigationInstructionItem navigationInstructionItem = mNavigator.getNavigationInstructionItem();
                instructionsText.setText(navigationInstructionItem.getInstructionString());
                if (navigationInstructionItem.getDirection()==NavigationInstruction.DIRECTION_RIGHT)
                    instructionImage.setImageDrawable(getDrawable(R.drawable.ic_subdirectory_arrow_left_white_24dp));
                if (navigationInstructionItem.getDirection()==NavigationInstruction.DIRECTION_LEFT)
                    instructionImage.setImageDrawable(getDrawable(R.drawable.ic_subdirectory_arrow_right_white_24dp));
                if (mNavigator.atLastInstruction())
                {
                    instructionImage.setRotation(0);
                    instructionImage.setImageDrawable(getDrawable(R.drawable.ic_location_white_24dp));
                }
            }
        });
    }
    public void trackUserLocation ()
    {
        mCustomMapView.getMapboxMap().getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
    }
    public void stopTrackingUser ()
    {


    }

    private Coordinate getUserLocation ()
    {
        if (mCustomMapView.getMapboxMap().getMyLocation()!=null)
        {
            mCustomMapView.getMapboxMap().setMyLocationEnabled(true);

            double latitude = mCustomMapView.getMapboxMap().getMyLocation().getLatitude();
            double longitude = mCustomMapView.getMapboxMap().getMyLocation().getLongitude();
            return new Coordinate(latitude,longitude);
        }
        return null;
    }

    private boolean isUserInsideCampus ()
    {
        if (mCustomMapView.getMapboxMap().getMyLocation()!=null)
        {
            double latitude = mCustomMapView.getMapboxMap().getMyLocation().getLatitude();
            double longitude = mCustomMapView.getMapboxMap().getMyLocation().getLongitude();
            Coordinate coordinate = new Coordinate(latitude,longitude);
            return coordinate.isInsideCampus();
        }
        return  false;
    }

    private void getViews ()
    {
        listView = (ListView)findViewById(R.id.list);
        pathInstructionsLayout = findViewById(R.id.layout_path_instructions);
        pathDuraitonDistanceLayout = findViewById(R.id.layout_path_duration_distance);
        pathTitleText = (TextView) findViewById(R.id.text_path);
        directionLayout = findViewById(R.id.layout_direction);
        showPathInstructionsListButton = findViewById(R.id.button_show_path_instructions_list);
        distanceText = (TextView)findViewById(R.id.text_distance);
        durationText = (TextView)findViewById(R.id.text_duration);
        instructionsText = (TextView)findViewById(R.id.text_instruction);
        myPositionText = (TextView)findViewById(R.id.text_my_position);
        instructionImage = (ImageView)findViewById(R.id.image_instruction);
    }

    private void changeInstructionsLayoutSettings (boolean showPathList)
    {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pathInstructionsLayout.getLayoutParams();
        if (!showPathList)
        {
            layoutParams.weight = 0;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        else
        {
            layoutParams.weight = 1.2f;
            layoutParams.height = 0;
        }
        pathInstructionsLayout.setLayoutParams(layoutParams);
    }

    private void changeState (boolean showPathInstructionsList,boolean animate) {
        Handler handler = new Handler();
        changeInstructionsLayoutSettings(showPathInstructionsList);
        showPathInstructionsListButton.clearAnimation();
        pathDuraitonDistanceLayout.setVisibility(View.GONE);
        directionLayout.setVisibility(View.GONE);
        Animation animation;
        if (showPathInstructionsList)
        {
            myPositionText.setVisibility(View.INVISIBLE);
            if (!isUserInsideCampus())
            {
                showPathInstructionsListButton.setVisibility(View.GONE);
            }
            animation = AnimationUtils.loadAnimation(NavigationActivity.this,R.anim.rotation_from_0_to_180);
            mCustomMapView.animateCamera(mPath.getGraph(),150,150,150,1000);
            pathTitleText.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    populateListView();
                }
            },250);
            if (!isUserInsideCampus())
            {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NavigationActivity.this,"Vous êtes dehors du campus navigation GPS impossible",Toast.LENGTH_LONG).show();
                    }
                },1000);
            }
            intiMapPathInstructionsList(animate);
        }
        else
        {
            myPositionText.setVisibility(View.VISIBLE);
            pathTitleText.setVisibility(View.GONE);
            pathDuraitonDistanceLayout.setVisibility(View.VISIBLE);
            clearSelectedPolylines();
            animation = AnimationUtils.loadAnimation(NavigationActivity.this,R.anim.rotation_from_180_to_0);
            listView.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    directionLayout.setVisibility(View.VISIBLE);

                }
            },250);
            initNavigationMap(animate);
            //startTrackingUser();
        }

        animation.setFillAfter(true);
        showPathInstructionsListButton.startAnimation(animation);

    }



    private void intiMapPathInstructionsList(boolean animate)
    {
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        if (!animate)
            mCustomMapView.moveCamera(mPath.getGraph(),150);
        else
            mCustomMapView.animateCamera(mPath.getGraph(),150);
        mCustomMapView.drawPolyline(mPath.getGraph());

        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);

        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassEnabled(true);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassFadeFacingNorth(false);
    }

    private void populateListView ()
    {
        listView.setVisibility(View.VISIBLE);
        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {

            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getStartOrder(),navigationInstruction.getEndOrder(),false));
        }
        final NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,navigationInstructionItems);
        listView.setAdapter(navigationItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPosition = -1;
                int pos = 0;
                clearSelectedPolylines();
                for (NavigationInstructionItem navigationInstruction:navigationInstructionItems)
                {

                    if (navigationInstruction.isSelected())
                    {
                        selectedPosition = pos;
                    }
                    navigationInstruction.setSelected(false);
                    pos++;
                }

                if (position != selectedPosition)
                {
                    NavigationInstructionItem navigationInstructionItem = navigationInstructionItems.get(position);
                    navigationInstructionItem.setSelected(true);
                    ArrayList<Edge> previousEdges = mPath.getGraph().getEdges(0,navigationInstructionItem.getStartOrder()-1);
                    ArrayList<Edge> currentEdges = mPath.getGraph().getEdges(navigationInstructionItem.getStartOrder(),navigationInstructionItem.getEndOrder());
                    ArrayList<Coordinate> previousPolyline = Edge.getPolyline(previousEdges);
                    final ArrayList<Coordinate> currentPolyline = Edge.getPolyline(currentEdges);
                    selectedPolylines.add(mCustomMapView.drawPolyline(currentPolyline,"#37AB30"));
                    mCustomMapView.animateCamera(currentPolyline,150,150,150,1000);

                }
                else
                {
                    mCustomMapView.animateCamera(mPath.getGraph(),150,150,150,1000);
                    navigationInstructionItems.get(selectedPosition).setSelected(false);
                }




                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);
    }
    private void clearSelectedPolylines()
    {
        if (selectedPolylines!=null&&selectedPolylines.size()>0)
        {
            for (Polyline polyline :selectedPolylines)
                mCustomMapView.getMapboxMap().removeAnnotation(polyline);
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
