package com.company;

import android.location.Location;


import com.example.nouno.locateme.Data.Place;
import com.google.gson.Gson;

import java.io.Serializable;


/**
 * Created by nouno on 26/06/2017.
 */

public class Coordinate implements Serializable {
    private double latitude;
    private double longitude;
    private static final long serialVersionUID = 13;
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

        return  (this.getLatitude()<= Place.NORTH_EAST_BOUND.getLatitude()&&
                this.getLatitude()>=Place.SOUTH_WEST_BOUND.getLatitude()&&
                this.getLongitude()>=Place.SOUTH_WEST_BOUND.getLongitude()&&
                this.getLongitude()<=Place.NORTH_EAST_BOUND.getLongitude());
    }

    public float bearingTo(Coordinate coordinate)
    {
        Location loc1 = new Location("");
        Location loc2 = new Location("");
        loc1.setLatitude(this.latitude);
        loc1.setLongitude(this.longitude);
        loc2.setLongitude(coordinate.longitude);
        loc2.setLatitude(coordinate.latitude);
        return loc1.bearingTo(loc2);
    }

    public float getBearing ()
    {
        Location loc1 = new Location("");
        loc1.setLatitude(this.latitude);
        loc1.setLongitude(this.longitude);
        return loc1.getBearing();
    }
}
