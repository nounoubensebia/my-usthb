package com.example.nouno.locateme.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.Djikstra.Graph;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.VisibleRegion;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Projection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouno on 03/07/2017.
 */

public class CustomMapView  {
    private MapView mapView;
    private MapboxMap mapboxMap;
    public CustomMapView (MapboxMap mapboxMap,MapView mapView)
    {
        this.mapboxMap = mapboxMap;
        this.mapView = mapView;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public void setMapboxMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    public void drawPolyline(List<Coordinate> coordinates) {
        ArrayList<LatLng> points = new ArrayList<>();
        for (Coordinate c : coordinates) {
            points.add(new LatLng(c.getLatitude(), c.getLongitude()));
        }
        mapboxMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#37AB30"))
                .width(5));
    }

    public void drawPolyline (Graph graph)
    {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Coordinate endCoord = null;
        /*for (Edge e :graph.getEdges())
        {
            /*if (endCoord==null)
                coordinates.add(e.getCoordinates().get(0));
            for (int i=1;i<e.getCoordinates().size();i++)
            {
                Coordinate c = e.getCoordinates().get(i);
                coordinates.add(c);
            }
            endCoord = coordinates.get(coordinates.size()-1);
        }
        drawPolyline(coordinates);*/
        for (Edge e :graph.getEdges())
        {
            drawPolyline(e.getCoordinates());
        }
    }

    public void drawMarker(Coordinate coordinate, String title, int iconResource) {
        IconFactory iconFactory = IconFactory.getInstance(mapView.getContext());
        Icon icon = iconFactory.fromBitmap(getBitmapFromVectorDrawable(iconResource));
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()))

                .icon(icon)
        );
    }
    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Context context = mapView.getContext();
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

    public void removeMarker (Coordinate coordinate)
    {
        for (Marker marker:mapboxMap.getMarkers())
        {
            Coordinate markerCoordinate = new Coordinate(marker.getPosition());
            if (markerCoordinate.equals(coordinate))
            {
                mapboxMap.removeAnnotation(marker);
                return;
            }
        }
    }

    public void animateCamera(final Coordinate coordinate, final int zoom)
    {
        mapboxMap.animateCamera(new CameraUpdate() {
            @Override
            public CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap) {
                CameraPosition.Builder builder1 = new CameraPosition.Builder().target(coordinate.getMapBoxLatLng()).zoom(zoom);
                return builder1.build();
            }
        });
    }

    public void moveCamera (final Coordinate coordinate, final double zoom)
    {
        //mapboxMap.setCameraPosition(new CameraPosition.Builder().target(coordinate.getMapBoxLatLng()).zoom(zoom).build());
        mapboxMap.moveCamera(new CameraUpdate() {
            @Override
            public CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap) {
                CameraPosition.Builder builder = new CameraPosition.Builder().target(coordinate.getMapBoxLatLng()).zoom(zoom);
                return builder.build();
            }
        });
    }

    public void  moveCamera (final Coordinate coordinate)
    {
        mapboxMap.moveCamera(new CameraUpdate() {
            @Override
            public CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap) {
                CameraPosition.Builder builder = new CameraPosition.Builder().target(coordinate.getMapBoxLatLng());
                return builder.build();
            }
        });
    }

    public boolean isPointVisible (Coordinate coordinate)
    {
        Projection projection = mapboxMap.getProjection();
        VisibleRegion visibleRegion = projection.getVisibleRegion();
        return  (coordinate.getLatitude()<=visibleRegion.latLngBounds.getNorthWest().getLatitude()&&
                coordinate.getLatitude()>=visibleRegion.latLngBounds.getSouthEast().getLatitude()&&
                coordinate.getLongitude()>=visibleRegion.latLngBounds.getNorthWest().getLongitude()&&
                coordinate.getLongitude()<=visibleRegion.latLngBounds.getSouthEast().getLongitude());
    }
}
