package com.example.nouno.locateme.Data;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import static com.example.nouno.locateme.Data.Place.NORTH_WEST_CAMPUS_BOUND;
import static com.example.nouno.locateme.Data.Place.SOUTH_EAST_CAMPUS_BOUND;

/**
 * Created by nouno on 26/06/2017.
 */

public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate (LatLng latLng)
    {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public Coordinate (com.mapbox.mapboxsdk.geometry.LatLng latLng)
    {
        latitude = latLng.getLatitude();
        longitude = latLng.getLongitude();
    }

    public double getLatitude() {
        return latitude;

    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getGoogleLatLng ()
    {
        return (new LatLng(latitude,longitude));
    }

    public com.mapbox.mapboxsdk.geometry.LatLng getMapBoxLatLng ()
    {
        return (new com.mapbox.mapboxsdk.geometry.LatLng(latitude,longitude));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate)
        {
            Coordinate c = (Coordinate) obj;
            return (c.longitude==longitude&&c.latitude==latitude);
        }
        return false;
    }

    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static Coordinate fromJson (String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json,Coordinate.class);
    }
    public boolean isInsideCampus ()
    {
        //TODO handle too far from road

        return  (this.getLatitude()<=NORTH_WEST_CAMPUS_BOUND.getLatitude()&&
                this.getLatitude()>=SOUTH_EAST_CAMPUS_BOUND.getLatitude()&&
                this.getLongitude()>=NORTH_WEST_CAMPUS_BOUND.getLongitude()&&
                this.getLongitude()<=SOUTH_EAST_CAMPUS_BOUND.getLongitude());
    }
}
