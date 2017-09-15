package com.example.nouno.locateme.Data;

import com.company.Coordinate;
import com.google.gson.Gson;

/**
 * Created by nouno on 02/07/2017.
 */

public class Place {

    public static final Coordinate NORTH_EAST_BOUND = new Coordinate(36.718693915273185,3.191249370574951);
    public static final Coordinate SOUTH_WEST_BOUND = new Coordinate(36.7054788373048,3.1696924567222595);


    private String label;
    private Coordinate coordinate;
    private boolean isUserLocation;
    public Place(String label, Coordinate coordinate,boolean isUserLocation) {
        this.label = label;
        this.coordinate = coordinate;
        this.isUserLocation = isUserLocation;
    }

    public String getLabel() {
        return label.toLowerCase();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Place fromJson (String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json,Place.class);
    }

    public boolean isUserLocation() {
        return isUserLocation;
    }

    public void setUserLocation(boolean userLocation) {
        isUserLocation = userLocation;
    }
}
