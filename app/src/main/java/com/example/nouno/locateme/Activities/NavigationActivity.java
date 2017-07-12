package com.example.nouno.locateme.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.logging.Handler;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private ArrayList<NavigationInstructionItem> navigationInstructionItems;
    private ArrayList<Polyline> selectedPolylines = null;
    private CustomMapView mCustomMapView;
    private Path mPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getViews();
        selectedPolylines = new ArrayList<>();
        createMap(savedInstanceState);
        getIntentInfo();


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
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());

                MapboxMap mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                //getGraph();

                mapboxMap.setMyLocationEnabled(true);
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);

                intiMap();

            }
        });
    }

    private void getViews ()
    {
        listView = (ListView)findViewById(R.id.list);
        
    }

    private void intiMap ()
    {
        navigationInstructions = mPath.getGraph().getNavigationInstructions(mCustomMapView.getMapboxMap().getProjection());
        mCustomMapView.moveCamera(mPath.getGraph(),150);
        mCustomMapView.drawPolyline(mPath.getGraph());

        mCustomMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);

        mCustomMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destination",R.drawable.ic_marker_red_24dp);
        mCustomMapView.getMapboxMap().setMyLocationEnabled(false);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassEnabled(true);
        mCustomMapView.getMapboxMap().getUiSettings().setCompassFadeFacingNorth(false);
        populateListView();
    }

    private void populateListView ()
    {
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
                if (selectedPolylines.size()>0)
                {
                    for (Polyline polyline :selectedPolylines)
                    mCustomMapView.getMapboxMap().removeAnnotation(polyline);
                }
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
                    mCustomMapView.animateCamera(currentPolyline,150);

                }
                else
                {
                    mCustomMapView.animateCamera(mPath.getGraph(),150);
                    navigationInstructionItems.get(selectedPosition).setSelected(false);
                }




                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);
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
