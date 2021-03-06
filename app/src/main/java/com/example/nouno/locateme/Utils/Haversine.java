package com.example.nouno.locateme.Utils;

import android.location.Location;

import com.company.Coordinate;


/**
 * Created by nouno on 25/06/2017.
 */
public class Haversine {
    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM

    public static double distance(Coordinate source, Coordinate destination) {
        /*double startLat = source.getLatitude();
        double startLong =source.getLongitude();
        double endLat =destination.getLatitude();
        double endLong=destination.getLongitude();
        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;*/ // <-- d

        return source.getMapBoxLatLng().distanceTo(destination.getMapBoxLatLng())/1000;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
