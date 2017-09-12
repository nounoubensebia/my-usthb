package com.example.nouno.locateme.Activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
import com.example.nouno.locateme.DialogUtils;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Utils.Haversine;
import com.example.nouno.locateme.Utils.MapGeometryUtils;
import com.example.nouno.locateme.Utils.UiUtils;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.internal.Utils;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private ArrayList<NavigationInstructionItem> navigationInstructionItems;
    private ArrayList<Polyline> selectedPolylines = null;
    private CustomMapView mCustomMapView;
    private View pathInstructionsLayout;

    private TextView pathTitleText;
    private Coordinate userLocation;
    private Path mPath;
    private Navigator mNavigator;
    private boolean followUser = true;
    private View showPathInstructionsListButton;
    private boolean showPathInstructionsList = false;
    private int layoutHeight = -1;

    private TextView myPositionText;

    private Graph entireGraph;
    private boolean calculateNewPath = false;


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
                changeState(!showPathInstructionsList,true);
                showPathInstructionsList = !showPathInstructionsList;

            }
        });

    }

    private void getIntentInfo ()
    {
        Bundle extras = getIntent().getExtras();
        mPath = Path.fromJson(extras.getString("path"));

    }


    private void createMap(Bundle savedInstanceState) {

        final MapView mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_EAST_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_WEST_BOUND.getMapBoxLatLng());
                MapboxMap mMapboxMap = mapboxMap;
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                mapboxMap.setMyLocationEnabled(true);
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);

                    showPathInstructionsList = true;
                    intiMapPathInstructionsList(false,true);
                    changeState(true,false);
                    intiMapPathInstructionsList(false,true);
                myPositionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isUserInsideCampus())
                        {
                            mCustomMapView.animateCamera(getUserLocation(),18,mCustomMapView.getMapboxMap().getMyLocation().getBearing());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCustomMapView.getMapboxMap().getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                                    mCustomMapView.getMapboxMap().getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                                }
                            },1000);

                        }
                        else
                        {
                            Toast.makeText(NavigationActivity.this,"Vous etes en dehors du campus",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
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

        pathTitleText = (TextView) findViewById(R.id.text_path);

        showPathInstructionsListButton = findViewById(R.id.button_show_path_instructions_list);


        myPositionText = (TextView)findViewById(R.id.text_my_position);

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
        Animation animation;
        if (showPathInstructionsList)
        {
            myPositionText.setVisibility(View.INVISIBLE);
            if (!isUserInsideCampus())
            {
                //showPathInstructionsListButton.setVisibility(View.GONE);
            }
            animation = AnimationUtils.loadAnimation(NavigationActivity.this,R.anim.rotation_from_0_to_180);
            //mCustomMapView.animateCamera(mPath.getGraph(),150,150,150,1000);
            listView.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateListView();
                }
            },250);

            intiMapPathInstructionsList(animate,false);
        }
        else
        {

            myPositionText.setVisibility(View.VISIBLE);
            clearSelectedPolylines();
            animation = AnimationUtils.loadAnimation(NavigationActivity.this,R.anim.rotation_from_180_to_0);
            listView.setVisibility(View.GONE);
            //initNavigationMap(animate);

        }
        animation.setFillAfter(true);
        showPathInstructionsListButton.startAnimation(animation);

    }



    private void intiMapPathInstructionsList(boolean animate,boolean initCamera)
    {
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        if (initCamera)
        {


            if (!animate)
            {
                mCustomMapView.moveCamera(mPath.getGraph(),
                        (int)UiUtils.transformDpToPx(this,50),
                        (int)UiUtils.transformDpToPx(this,50),
                        (int)UiUtils.transformDpToPx(this,50),
                        getBottomPixels()
                        );

            }
            else

                mCustomMapView.animateCamera(mPath.getGraph(),
                        (int)UiUtils.transformDpToPx(this,50),
                        (int)UiUtils.transformDpToPx(this,50),
                        (int)UiUtils.transformDpToPx(this,50),
                        getBottomPixels());
        }
        mCustomMapView.drawPolyline(mPath.getGraph());

        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);

        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassEnabled(true);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassFadeFacingNorth(false);
    }

    private int getBottomPixels ()
    {
        Log.i("PIXEL",UiUtils.convertPixelsToDp(350,this)+"");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (layoutHeight!=-1)
        layoutHeight = pathInstructionsLayout.getHeight();
        int bottomPixels =(int)UiUtils.transformDpToPx(this,334);
        if (bottomPixels>1000)
            bottomPixels = 1000;
        return bottomPixels;
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
                    selectedPolylines.add(mCustomMapView.drawPolyline(currentPolyline,"#0078d7"));
                    mCustomMapView.animateCamera(currentPolyline,(int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            (int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            (int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            getBottomPixels());

                }
                else
                {
                    mCustomMapView.animateCamera(mPath.getGraph(),(int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            (int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            (int) UiUtils.transformDpToPx(NavigationActivity.this,50),
                            getBottomPixels());

                    //mCustomMapView.animateCamera(mPath.getGraph(),150,150,150,1000);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCustomMapView!=null)
            mCustomMapView.getMapView().onSaveInstanceState(outState);
    }
}
