package com.example.nouno.locateme.Fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Djikstra.DijkstraAlgorithm;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.Djikstra.Vertex;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Activities.SearchQueryActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String LICENSE = "XTUN3Q0ZGVzQ1Vmpxc01yRnRuaFFzY043c1BvWDBSOHRBaFJVVFd2bmQ3ODdYY2lkVHpuekcyeHZCWmRQY2c9PQoKYXBwVG9rZW49MGY4YmFjMTYtN2ZlOS00ZjhlLWEyOTItYTAyYjM4Nzg5ZGQwCnBhY2thZ2VOYW1lPWNvbS5leGFtcGxlLm5vdW5vLmxvY2F0ZW1lCm9ubGluZUxpY2Vuc2U9MQpwcm9kdWN0cz1zZGstYW5kcm9pZC00LioKd2F0ZXJtYXJrPWNhcnRvZGIK";

    private TextView mMyLocationText;
    private TextView mDestinationText;
    private Button mCalculateButton;
    private Graph mGraph;
    private Object mDeparture;
    private Object mDestination;
    private MapboxMap mMapboxMap;
    private ProgressBar mProgressBar;
    private static final LatLngBounds ICELAND_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(66.852863, -25.985652))
            .include(new LatLng(62.985661, -12.626277))
            .build();
    private MapView mapView;

    public SearchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        super.onCreate(savedInstanceState);

        mMyLocationText = (TextView) view.findViewById(R.id.my_location_text);
        mDestinationText = (TextView) view.findViewById(R.id.destination_text);
        mCalculateButton = (Button) view.findViewById(R.id.calculate_button);
        mMyLocationText.setOnClickListener(new View.OnClickListener() {
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
        mCalculateButton.setVisibility(View.GONE);
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeparture!=null&&mDestination!=null)
                {
                    getPath();
                }
            }
        });
        createMap(savedInstanceState, view);
        return view;
    }

    private void createMap(Bundle savedInstanceState, View view) {

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(36.717562, 3.192844));
                builder.include(new LatLng(36.705176, 3.167439));
                mMapboxMap = mapboxMap;
                getGraph();
                mMapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        createChooseMarkerTypeDialog(new Coordinate(point));
                    }
                });
            }
        });
    }

    private void startSearchActivity() {
        Intent i = new Intent(getActivity(), SearchQueryActivity.class);
        startActivity(i);
    }

    private void drawPolyline(List<Coordinate> coordinates) {
        ArrayList<LatLng> points = new ArrayList<>();
        for (Coordinate c : coordinates) {
            points.add(new LatLng(c.getLatitude(), c.getLongitude()));
        }
        mMapboxMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#222222"))
                .width(3));

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
        //alt_bld.setIcon(R.drawable.icon);
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
                    if (mDeparture!=null && mDeparture instanceof Coordinate)
                    {
                        removeMarker((Coordinate)mDeparture);
                    }
                    drawMarker(coordinate,"Point de départ",R.drawable.ic_marker_blue_24dp);
                    if (mDestination!=null)
                    {
                        mCalculateButton.setVisibility(View.VISIBLE);
                    }
                    mDeparture = coordinate;
                }
                else
                {
                    if (mDestination!=null && mDestination instanceof Coordinate)
                    {
                        removeMarker((Coordinate)mDestination);
                    }
                    mDestination = coordinate;
                    drawMarker(coordinate,"Destination",R.drawable.ic_marker_red_24dp);
                    if (mDeparture!=null)
                    {
                        mCalculateButton.setVisibility(View.VISIBLE);
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
       if (mDeparture instanceof Coordinate && mDestination instanceof Coordinate)
       {

           mGraph.getShortestPath((Coordinate) mDeparture, (Coordinate) mDestination, mMapboxMap.getProjection(),
                   new OnSearchFinishListener() {
                       @Override
                       public void OnSearchFinish(Graph graph) {
                           drawAllPolylinesFromGraph(graph);
                           mProgressBar.setVisibility(View.GONE);
                       }
                   });
       }
       if (mDeparture instanceof Coordinate && mDestination instanceof Vertex)
       {
           mGraph.getShortestPath((Coordinate) mDeparture, (Vertex) mDestination, mMapboxMap.getProjection(),
                   new OnSearchFinishListener() {
                       @Override
                       public void OnSearchFinish(Graph graph) {
                           drawAllPolylinesFromGraph(graph);
                           mProgressBar.setVisibility(View.GONE);
                       }
                   });
       }

       if (mDeparture instanceof Vertex && mDestination instanceof Coordinate)
       {
           mGraph.getShortestPath((Vertex) mDeparture, (Coordinate) mDestination, mMapboxMap.getProjection(),
                   new OnSearchFinishListener() {
                       @Override
                       public void OnSearchFinish(Graph graph) {
                           drawAllPolylinesFromGraph(graph);
                           mProgressBar.setVisibility(View.GONE);
                       }
                   });
       }
       if (mDeparture instanceof Vertex && mDestination instanceof Vertex)
       {
           mGraph.getShortestPath((Vertex) mDeparture, (Vertex) mDestination
                   , new OnSearchFinishListener() {
                       @Override
                       public void OnSearchFinish(Graph graph) {
                           drawAllPolylinesFromGraph(graph);
                           mProgressBar.setVisibility(View.GONE);
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




}
