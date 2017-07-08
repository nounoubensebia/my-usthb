package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.Utils.MapGeometryUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by nouno on 02/07/2017.
 */

public class Path {
    private Place source;
    private Place destination;
    private float distance;

    public Path() {
    }

    private Path(Place source, Place destination,float distance) {
        this.source = source;
        this.destination = destination;

        this.distance = distance;
    }

    public Place getSource() {
        return source;
    }

    public void setSource(Place source) {
        this.source = source;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public float getDuration() {
        return MapGeometryUtils.getDuration(distance);
    }

    public String getDistanceString ()
    {
       return (int)(distance*1000)+" metres";
    }

    public String getDurationString ()
    {
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(getDuration()/60);
        return s+" min";
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
