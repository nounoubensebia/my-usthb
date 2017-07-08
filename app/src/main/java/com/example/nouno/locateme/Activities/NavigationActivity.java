package com.example.nouno.locateme.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;
import com.google.gson.Gson;
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
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        listView = (ListView)findViewById(R.id.list);
        Bundle extras = getIntent().getExtras();

        navigationInstructions = new ArrayList<>();
        Gson gson = new Gson();
        navigationInstructions = gson.fromJson(extras.getString("navigationInstructions"),ArrayList.class);
        //navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_LEFT,100));
        //navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_RIGHT,250));
        parseIntent();
        populateListView();
        createMap(savedInstanceState);
    }

    private void createMap(Bundle savedInstanceState) {

        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());

                mMapboxMap = mapboxMap;
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

    private void populateListView ()
    {
        navigationInstructionItems = new ArrayList<NavigationInstructionItem>();
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {

            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getPolyline(),false));
        }
        navigationInstructionItems.get(0).setSelected(true);
        final NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,navigationInstructionItems);
        listView.setAdapter(navigationItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (NavigationInstructionItem navigationInstruction:navigationInstructionItems)
                {
                    navigationInstruction.setSelected(false);
                }
                navigationInstructionItems.get(position).setSelected(true);
                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);
    }

    private void parseIntent ()
    {
        Bundle extras = getIntent().getExtras();
        String json = extras.getString("navigationInstructions");
        navigationInstructions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String js = jsonObject.toString();
                Gson gson = new Gson();
                NavigationInstruction navigationInstruction = gson.fromJson(js,NavigationInstruction.class);
                navigationInstructions.add(navigationInstruction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
