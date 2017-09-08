package com.example.nouno.locateme.Fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nouno.locateme.Activities.Main2Activity;
import com.example.nouno.locateme.Activities.SearchQueryTwoActivity;
import com.example.nouno.locateme.Activities.StartActivity;
import com.example.nouno.locateme.Data.CenterOfInterest;
import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Data.StructureList;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.CustomMapView;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Utils.UiMarkerUtils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    MapView mMapView;
    MapboxMap mMapboxMap;
    CustomMapView mCustomMapView;
    ImageView button;
    View fabList;
    View fabClear;
    View bottomChoice;
    TextView mosqueText;
    TextView buvetteText;
    TextView sanitaireText;
    TextView exitText;
    TextView kiosqueText;
    StructureList structureList;
    TextView bigLabel;
    TextView smallLabel;
    TextView typeText;
    View pathLayout;
    View centerOfInterestLayout;
    View searchLayout;

    boolean markerSelected = false;
    boolean mosqueSelected;
    boolean buvetteSelected;
    boolean sannitaireSelected;
    boolean exitSelected;
    boolean kiosqueSelected;
    CenterOfInterest selectedCenterOfInterest = null;
    private TextView whereToGoText;

    public MapFragment() {
        // Required empty public constructor
    }

    private void getStructureList ()
    {
        InputStream inputStream = null;
        try {
            inputStream = this.getResources().getAssets().open("LocalsJson.txt");

            String localsJson = FileUtils.readFile(inputStream);
            structureList = new Gson().fromJson(localsJson,StructureList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(this.getActivity(), "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        View view= inflater.inflate(R.layout.fragment_map, container, false);

        getStructureList();



        button = (ImageView) view.findViewById(R.id.btn);
        whereToGoText = (TextView)view.findViewById(R.id.where_to_go);
        whereToGoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),SearchQueryTwoActivity.class);
                startActivity(i);
            }
        });
        createMap(savedInstanceState,view);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                3);



        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/


        fabList = view.findViewById(R.id.fab_list);
        fabClear = view.findViewById(R.id.fab_clear);
        bottomChoice = view.findViewById(R.id.bottom_choice);
        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerSelected = false;
                mMapboxMap.removeAnnotations();
                exit(fabClear);
                enter(fabList);
                bottomChoice.setVisibility(View.GONE);
                ((StartActivity)getActivity()).showBottomBar();
            }
        });
        fabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerSelected = false;
                updateMap();
                enter(fabClear);
                exit(fabList);
                bottomChoice.setVisibility(View.VISIBLE);
                ((StartActivity)getActivity()).hideBottomBar();

            }
        });

        mosqueText = (TextView) view.findViewById(R.id.text_mosque);
        buvetteText = (TextView) view.findViewById(R.id.text_buvette);
        sanitaireText = (TextView) view.findViewById(R.id.text_sannitaire);
        exitText = (TextView) view.findViewById(R.id.text_exit);
        kiosqueText = (TextView) view.findViewById(R.id.text_kiosque);
        mosqueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mosqueSelected)
                {
                    changeTextViewState(mosqueText,R.drawable.ic_mosque_disabled);
                    mosqueSelected = false;

                }
                else
                {
                    changeTextViewState(mosqueText,R.drawable.ic_mosque);
                    mosqueSelected = true;
                }
                updateMap();
            }
        });

        kiosqueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kiosqueSelected)
                {
                    changeTextViewState(kiosqueText,R.drawable.ic_kiosk_disabled);
                    kiosqueSelected = false;
                }
                else
                {
                    changeTextViewState(kiosqueText,R.drawable.ic_kiosk);
                    kiosqueSelected = true;
                }
                updateMap();
            }
        });
        buvetteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buvetteSelected)
                {
                    buvetteSelected = false;
                    changeTextViewState(buvetteText,R.drawable.ic_food_disabled);
                }
                else
                {
                    buvetteSelected = true;
                    changeTextViewState(buvetteText,R.drawable.ic_food);
                }
                updateMap();
            }
        });
        exitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitSelected)
                {
                    changeTextViewState(exitText,R.drawable.ic_sortie_disabled);
                    exitSelected = false;
                }
                else
                {
                    exitSelected = true;
                    changeTextViewState(exitText,R.drawable.ic_sortie);
                }
                updateMap();
            }
        });
        sanitaireText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sannitaireSelected)
                {
                    sannitaireSelected= false;
                    changeTextViewState(sanitaireText,R.drawable.ic_toilet_disabled);
                }
                else
                {
                    sannitaireSelected= true;
                    changeTextViewState(sanitaireText,R.drawable.ic_toilet);
                }
                updateMap();
            }
        });
        typeText = (TextView)view.findViewById(R.id.typeText);
        centerOfInterestLayout = view.findViewById(R.id.layout_structure);
        pathLayout = view.findViewById(R.id.layout_path);
        searchLayout = view.findViewById(R.id.search_layout);
        bigLabel = (TextView) view.findViewById(R.id.biglabel);
        smallLabel = (TextView) view.findViewById(R.id.smallLabel);
        typeText = (TextView) view.findViewById(R.id.typeText);

        pathLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),SearchQueryTwoActivity.class);
                i.putExtra("centerOfInterest",new Gson().toJson(selectedCenterOfInterest));
                startActivity(i);
            }
        });
        return view;
    }

    private void createMap(Bundle savedInstanceState,View view) {

        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(Place.NORTH_WEST_CAMPUS_BOUND.getMapBoxLatLng());
                builder.include(Place.SOUTH_EAST_CAMPUS_BOUND.getMapBoxLatLng());

                mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                mMapboxMap.setLatLngBoundsForCameraTarget(builder.build());
                //mMapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                //mMapboxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
                //getGraph();
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        //      createChooseMarkerTypeDialog(new Coordinate(point));
                    }
                });
                mCustomMapView = new CustomMapView(mapboxMap,mMapView);
                mapboxMap.setMyLocationEnabled(true);
                try {
                    String graphJson = FileUtils.readFile(getActivity().getAssets().open("GraphJson.txt"));
                    //Graph graph = GraphCreator.createGraph(graphJson);
                    //Graph graph = new Graph(graphJson);
                    //mCustomMapView.drawFromGraph(graph);
                    //mCustomMapView.drawPolyline(graph);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCustomMapView.setOnMarkerClickListener(new CustomMapView.OnMarkerClickListener() {
                    @Override
                    public void onClick(UiMarkerUtils uiMarkerUtils) {
                        markerSelected = true;
                        centerOfInterestLayout.setVisibility(View.VISIBLE);
                        searchLayout.setVisibility(View.GONE);
                        pathLayout.setVisibility(View.VISIBLE);
                        bottomChoice.setVisibility(View.GONE);
                        exit(fabClear);
                        mCustomMapView.animateCamera(new Coordinate(uiMarkerUtils.getMarker().getPosition()),16);
                        bindStructureLayout((CenterOfInterest)uiMarkerUtils.getTag());
                        selectedCenterOfInterest = ((CenterOfInterest)uiMarkerUtils.getTag());
                        ((StartActivity)getActivity()).showBottomBar();
                    }
                });
                mCustomMapView.getMapboxMap().setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        if (markerSelected)
                        {

                            markerSelected = false;
                            centerOfInterestLayout.setVisibility(View.GONE);
                            searchLayout.setVisibility(View.VISIBLE);
                            pathLayout.setVisibility(View.GONE);
                            bottomChoice.setVisibility(View.VISIBLE);
                            enter(fabClear);
                            ((StartActivity)getActivity()).hideBottomBar();
                        }
                    }
                });
            }
        });
    }

    public void bindStructureLayout (CenterOfInterest centerOfInterest)
    {
        bigLabel.setText(centerOfInterest.getLabel()+"");
        smallLabel.setText(structureList.getBlocLabel(centerOfInterest));
        typeText.setText(CenterOfInterest.getTypeString(centerOfInterest.getType()));
    }

    public void enter(final View view) {

        view.setVisibility(View.VISIBLE);
        final Animation fabEnter = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_enter);
        fabEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
                view.clearAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabEnter);

    }


    public void exit(final View view) {
        final Animation fabExit = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_exit);

        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabExit);

    }
    public void changeTextViewState (TextView textView, int drawableResource)
    {
        //textView.setTextColor(ContextCompat.getColor(context,textColorId));
        Drawable drawable = ContextCompat.getDrawable(getActivity(),drawableResource);
        textView.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
    }

    private void updateMap ()
    {
        mMapboxMap.removeAnnotations();
        if (buvetteSelected)
        {
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_BUVETTE)
                {
                    UiMarkerUtils uiMarkerUtils = mCustomMapView.addMarker(centerOfInterest.getCoordinate(),R.drawable.ic_food);
                    uiMarkerUtils.setTag(centerOfInterest);
                }
            }
        }
        if (kiosqueSelected)
        {
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_KIOSQUE)
                {
                    UiMarkerUtils uiMarkerUtils =mCustomMapView.addMarker(centerOfInterest.getCoordinate(),R.drawable.ic_kiosk);
                    uiMarkerUtils.setTag(centerOfInterest);
                }
            }
        }
        if (sannitaireSelected)
        {
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_TOILETTE)
                {
                    UiMarkerUtils uiMarkerUtils =mCustomMapView.addMarker(centerOfInterest.getCoordinate(),R.drawable.ic_toilet);
                    uiMarkerUtils.setTag(centerOfInterest);
                }
            }
        }
        if (mosqueSelected)
        {
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_MOSQUE)
                {
                    UiMarkerUtils uiMarkerUtils = mCustomMapView.addMarker(centerOfInterest.getCoordinate(),R.drawable.ic_mosque);
                    uiMarkerUtils.setTag(centerOfInterest);
                }
            }
        }
        if (exitSelected)
        {
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_SORTIE)
                {
                    UiMarkerUtils uiMarkerUtils = mCustomMapView.addMarker(centerOfInterest.getCoordinate(),R.drawable.ic_sortie);
                    uiMarkerUtils.setTag(centerOfInterest);
                }
            }
        }
    }

}