package com.example.nouno.locateme.Fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Activities.SearchQueryActivity;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public static final String USER_LOCATION_JSON = "userLocation";


    private static final String LICENSE = "XTUN3Q0ZGVzQ1Vmpxc01yRnRuaFFzY043c1BvWDBSOHRBaFJVVFd2bmQ3ODdYY2lkVHpuekcyeHZCWmRQY2c9PQoKYXBwVG9rZW49MGY4YmFjMTYtN2ZlOS00ZjhlLWEyOTItYTAyYjM4Nzg5ZGQwCnBhY2thZ2VOYW1lPWNvbS5leGFtcGxlLm5vdW5vLmxvY2F0ZW1lCm9ubGluZUxpY2Vuc2U9MQpwcm9kdWN0cz1zZGstYW5kcm9pZC00LioKd2F0ZXJtYXJrPWNhcnRvZGIK";
    private TextView mAppBarTitleText;
    private TextView mDepartureText;
    private TextView mDestinationText;
    private Button mCalculateButton;
    private Graph mGraph;
    private LinearLayout appBarLinearLayout;
    private Path mPath = new Path();
    private MapboxMap mMapboxMap;
    private ImageView collapseButton;
    private boolean pathLayoutAdded = false;
    private ProgressBar mProgressBar;
    private ViewGroup sceneRoot;
    private MapView mMapView;
    private LinearLayout mPathLayout;
    private int state;
    private Place mUserLocation;
    private static final int STATE_PATH_NOT_SET = 0;
    private static final int STATE_PATH_SET = 1;
    private static final int STATE_PATH_CALCULATED = 2;

    public SearchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        super.onCreate(savedInstanceState);
        state = STATE_PATH_NOT_SET;
        appBarLinearLayout = (LinearLayout)view.findViewById(R.id.app_bar_layout);
        mDepartureText = (TextView) view.findViewById(R.id.departure_text);
        mDestinationText = (TextView) view.findViewById(R.id.destination_text);
        mCalculateButton = (Button) view.findViewById(R.id.calculate_button);
        mAppBarTitleText = (TextView) view.findViewById(R.id.app_bar_title_text);
        mDepartureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity();
            }
        });
        mDestinationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchActivity();
            }
        });
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        collapseButton = (ImageView)view.findViewById(R.id.collapse_button);
        collapseButton.setVisibility(View.INVISIBLE);
        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (pathLayoutAdded)
                        removePathFoundLayout();
                    else
                        addPathFoundLayout();
                }
            }
        });
        //sceneRoot = (ViewGroup) view.findViewById(R.id.app_bar_root);
        mCalculateButton.setVisibility(View.GONE);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (mPath !=null&& mPath.getSource()!=null&& mPath.getDestination()!=null)
                {
                    getPath();
                    //addPathFoundLayout();
                }
            }
        });
        createMap(savedInstanceState, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mMapView.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    private void createMap(Bundle savedInstanceState, View view) {

        mMapView = (MapView) view.findViewById(R.id.mapView);

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
                getGraph();
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        createChooseMarkerTypeDialog(new Coordinate(point));
                    }
                });
                mapboxMap.setMyLocationEnabled(true);
            }
        });
    }

    private void startSearchActivity() {
        Intent i = new Intent(getActivity(), SearchQueryActivity.class);
        if (mMapboxMap.getMyLocation()!=null)
        {
            double latitude = mMapboxMap.getMyLocation().getLatitude();
            double longitude = mMapboxMap.getMyLocation().getLongitude();
            Coordinate c = new Coordinate(latitude,longitude);
            mUserLocation = new Place("label",c);
        }


        if (mUserLocation!=null)
        i.putExtra(USER_LOCATION_JSON,mUserLocation.toJson());
        startActivity(i);
    }

    private void drawPolyline(List<Coordinate> coordinates) {
        ArrayList<LatLng> points = new ArrayList<>();
        for (Coordinate c : coordinates) {
            points.add(new LatLng(c.getLatitude(), c.getLongitude()));
        }
        mMapboxMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#0078d7"))
                .width(5));
    }

    private void drawAllPolylinesFromGraph(Graph graph) {
        for (Edge e : graph.getEdges()) {
            ArrayList<Coordinate> coordinates = e.getCoordinates();
            drawPolyline(coordinates);
        }
    }

    private void drawMarker(Coordinate coordinate, String title, int iconResource) {
        IconFactory iconFactory = IconFactory.getInstance(getActivity());

        Icon icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(iconResource));

        mMapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()))
                .title(title)
                .icon(icon)
        );
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getGraph() {
        try {
            String json = FileUtils.readFile(getActivity().getAssets().open("GraphJson.txt"));
            mGraph = new Graph(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Context context = getActivity();
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    private void createChooseMarkerTypeDialog(final Coordinate coordinate){

        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setTitle("Choisir comme :");
        final CharSequence[] charSequences = new CharSequence[2];
        charSequences[0]="Point de départ";
        charSequences[1]="Destination";

        alt_bld.setSingleChoiceItems(charSequences, 0, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

            }
        });
        alt_bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog)dialog).getListView();
                CharSequence checkedItem = (CharSequence)lw.getAdapter().getItem(lw.getCheckedItemPosition());
                if (mMapboxMap.getPolylines().size() > 0)
                mMapboxMap.removeAnnotations(mMapboxMap.getPolylines());
                if (!checkedItem.equals("Destination"))
                {
                    if (mPath.getSource()!=null)
                    {
                        removeMarker(mPath.getSource().getCoordinate());
                    }
                    drawMarker(coordinate,"Point de départ",R.drawable.ic_marker_blue_24dp);
                    if (mPath.getDestination()!=null)
                    {
                        mCalculateButton.setVisibility(View.VISIBLE);
                        changeState(STATE_PATH_SET);
                    }
                    mPath.setSource(new Place("Prés de",coordinate));
                    mDepartureText.setText("Prés de");
                }
                else
                {
                    if (mPath.getDestination()!=null)
                    {
                        removeMarker(mPath.getDestination().getCoordinate());
                    }
                    mPath.setDestination(new Place("Prés de",coordinate));
                    mDestinationText.setText("Prés de");
                    drawMarker(coordinate,"Destination",R.drawable.ic_marker_red_24dp);
                    if (mPath.getSource()!=null)
                    {
                        mCalculateButton.setVisibility(View.VISIBLE);
                        changeState(STATE_PATH_SET);
                    }
                }
            }
        });

        alt_bld.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
   }

   private void getPath ()
   {
       mProgressBar.setVisibility(View.VISIBLE);
       mCalculateButton.setVisibility(View.GONE);
       if (mPath.getSource().getClass().getName().equals("com.example.nouno.locateme.Data.Place")&&mPath.getDestination().getClass().getName().equals("com.example.nouno.locateme.Data.Place"))
       {

           mGraph.getShortestPath(mPath.getSource().getCoordinate(), mPath.getDestination().getCoordinate(), mMapboxMap.getProjection(),
                   new OnSearchFinishListener() {
                       @Override
                       public void OnSearchFinish(Graph graph) {
                           drawAllPolylinesFromGraph(graph);
                           mPath.setDistance((float)graph.getWeight());
                           mProgressBar.setVisibility(View.GONE);
                           changeState(STATE_PATH_CALCULATED);
                       }
                   });
       }

   }

   private void removeMarker (Coordinate coordinate)
   {
       for (Marker marker:mMapboxMap.getMarkers())
       {
           Coordinate markerCoordinate = new Coordinate(marker.getPosition());
           if (markerCoordinate.equals(coordinate))
           {
               mMapboxMap.removeAnnotation(marker);
               return;
           }
       }
   }

    private void addPathFoundLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mPathLayout = (LinearLayout) inflater.inflate(R.layout.path_found_app_bar, null, false);
        appBarLinearLayout.addView(mPathLayout);
        if (state == STATE_PATH_CALCULATED)
        {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotation_from_0_to_180);
            collapseButton.startAnimation(animation);
            animation.setFillAfter(true);
        }
        pathLayoutAdded = true;
        TextView distancedurationText = (TextView)mPathLayout.findViewById(R.id.duration_distance_text);
        distancedurationText.setText(mPath.getDurationString()+" "+mPath.getDistanceString());
        TextView timeOfArrivalText = (TextView)mPathLayout.findViewById(R.id.time_of_arrival_text);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));

        Date currentLocalTime = cal.getTime();
        long time = currentLocalTime.getTime();
        time+=mPath.getDuration()*1000;
        currentLocalTime = new Time(time);
        DateFormat date = new SimpleDateFormat("HH:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        String localTime = date.format(currentLocalTime);
        timeOfArrivalText.setText("Arrivée à "+localTime);
    }


    private void removePathFoundLayout()
    {
        appBarLinearLayout.removeView(mPathLayout);
        if (state==STATE_PATH_CALCULATED)
        {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotation_from_180_to_0);
            collapseButton.startAnimation(animation);
            animation.setFillAfter(true);

        }

        pathLayoutAdded = false;
    }
    private void changeState (int state)
    {
        switch (state) {
            case STATE_PATH_NOT_SET : collapseButton.setVisibility(View.INVISIBLE);
                this.state = STATE_PATH_NOT_SET;
                if (pathLayoutAdded)
                {
                    removePathFoundLayout();
                }
                mCalculateButton.setVisibility(View.GONE);
                mAppBarTitleText.setText("Où aller ?");
                break;
            case STATE_PATH_SET :
                collapseButton.clearAnimation();
                collapseButton.setVisibility(View.INVISIBLE);
                this.state = STATE_PATH_SET;
                if (pathLayoutAdded)
                {
                    removePathFoundLayout();
                }
                mCalculateButton.setVisibility(View.VISIBLE);
                mAppBarTitleText.setText("Où aller ?");
                break;
            case  STATE_PATH_CALCULATED : collapseButton.setVisibility(View.VISIBLE);
                collapseButton.clearAnimation();
                this.state=STATE_PATH_CALCULATED;
                addPathFoundLayout();
                mCalculateButton.setVisibility(View.GONE);
                mAppBarTitleText.setText("Itinéraire trouvé");
        }
    }


}
