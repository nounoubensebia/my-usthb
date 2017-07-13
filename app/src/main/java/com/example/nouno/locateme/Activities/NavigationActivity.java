package com.example.nouno.locateme.Activities;

import android.location.Location;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.mapbox.mapboxsdk.maps.TrackingSettings;

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

                    changeState(!showPathInstructionsList);
                    showPathInstructionsList = !showPathInstructionsList;

            }
        });
        //initNavigationMap();

    }

    private void getIntentInfo ()
    {
        Bundle extras = getIntent().getExtras();
        mPath = Path.fromJson(extras.getString("path"));

    }

    private void initNavigationMap ()
    {

        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {
            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getStartOrder(),navigationInstruction.getEndOrder(),false));
        }
        mNavigator = new Navigator(navigationInstructionItems,mPath.getGraph());
        mCustomMapView.drawPolyline(mPath.getGraph());
        mCustomMapView.drawPolyline(mNavigator.getCurrentPolyline(),"#37AB30");

        double bearing = mNavigator.getCurrentPolyline().get(0).bearingTo(mNavigator.getCurrentPolyline().get(1));
        mCustomMapView.animateCamera(mNavigator.getCurrentPolyline().get(0),18,bearing);
        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);
        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
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

                    initNavigationMap();
                    //ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
                    //coordinates.add(getUserLocation());
                    //coordinates.add(mNavigator.getCurrentPolyline().get(0));
                    //mCustomMapView.animateCamera(coordinates,18);
                }
                else
                {
                    intiMapPathInstructionsList();
                    changeState(true);
                    Toast.makeText(NavigationActivity.this,"Vous êtes dehors du campus navigation GPS impossible",Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void trackUser ()
    {

    }

    private Coordinate getUserLocation ()
    {
        if (mCustomMapView.getMapboxMap().getMyLocation()!=null)
        {
        double latitude = mCustomMapView.getMapboxMap().getMyLocation().getLatitude();
        double longitude = mCustomMapView.getMapboxMap().getMyLocation().getLongitude();
        return new Coordinate(latitude,longitude);}
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

    private void changeState (boolean showPathInstructionsList) {
        Handler handler = new Handler();
        changeInstructionsLayoutSettings(showPathInstructionsList);
        showPathInstructionsListButton.clearAnimation();
        pathDuraitonDistanceLayout.setVisibility(View.GONE);
        directionLayout.setVisibility(View.GONE);
        Animation animation;
        if (showPathInstructionsList)
        {
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
        }
        else
        {
            pathTitleText.setVisibility(View.GONE);
            clearSelectedPolylines();
            animation = AnimationUtils.loadAnimation(NavigationActivity.this,R.anim.rotation_from_180_to_0);
            listView.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pathDuraitonDistanceLayout.setVisibility(View.VISIBLE);
                    directionLayout.setVisibility(View.VISIBLE);

                }
            },250);
        }

        animation.setFillAfter(true);
        showPathInstructionsListButton.startAnimation(animation);
    }



    private void intiMapPathInstructionsList()
    {
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        mCustomMapView.moveCamera(mPath.getGraph(),150);
        mCustomMapView.drawPolyline(mPath.getGraph());

        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);

        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
       // mCustomMapView.getMapboxMap().setMyLocationEnabled(false);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassEnabled(true);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassFadeFacingNorth(false);
        //populateListView();
    }

    private void populateListView ()
    {
        listView.setVisibility(View.VISIBLE);
        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {

            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getStartOrder(),navigationInstruction.getEndOrder(),false));
        }
        //navigationInstructionItems.get(0).setSelected(true);
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
                    //if (previousPolyline.size()>0)
                    //selectedPolylines.add(mCustomMapView.drawPolyline(previousPolyline,"#0078d7"));
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
