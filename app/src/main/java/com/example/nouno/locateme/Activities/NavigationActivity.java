package com.example.nouno.locateme.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private ArrayList<NavigationInstructionItem> navigationInstructionItems;
    private Polyline selectedPolyline = null;
    private CustomMapView customMapView;
    private Path mPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        listView = (ListView)findViewById(R.id.list);
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
                customMapView = new CustomMapView(mapboxMap,mMapView);
                intiMap();

            }
        });
    }

    private void intiMap ()
    {
        navigationInstructions = mPath.getGraph().getNavigationInstructions(customMapView.getMapboxMap().getProjection());
        customMapView.moveCamera(mPath.getGraph(),150);
        customMapView.drawPolyline(mPath.getGraph());
        customMapView.drawMarker(mPath.getSource().getCoordinate(),"Position de départ",R.drawable.ic_marker_blue_24dp);
        customMapView.drawMarker(mPath.getDestination().getCoordinate(),"Destincation",R.drawable.ic_marker_red_24dp);

        populateListView();
    }

    private void populateListView ()
    {
        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {

            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getPolyline(),false));
        }
        //navigationInstructionItems.get(0).setSelected(true);
        final NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,navigationInstructionItems);
        listView.setAdapter(navigationItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPosition = -1;
                int pos = 0;
                if (selectedPolyline!=null)
                {
                    customMapView.getMapboxMap().removeAnnotation(selectedPolyline);
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
                    navigationInstructionItems.get(position).setSelected(true);
                    selectedPolyline = customMapView.drawPolyline(navigationInstructionItems.get(position).getPolyline(),"#37AB30");
                    customMapView.animateCamera(navigationInstructionItems.get(position).getPolyline(),150);
                }
                else
                {
                    customMapView.animateCamera(mPath.getGraph(),150);
                    navigationInstructionItems.get(selectedPosition).setSelected(false);
                }




                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);
    }


}
