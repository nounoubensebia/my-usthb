package com.example.nouno.locateme.Data;

import com.google.android.gms.maps.model.LatLng;

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
}
