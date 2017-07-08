package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.Utils.MapGeometryUtils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by nouno on 02/07/2017.
 */

public class Path {
    private Place source;
    private Place destination;
    private float distance;
    private Graph graph;
    public Path() {
    }

    private Path(Place source, Place destination,float distance,Graph graph) {
        this.source = source;
        this.destination = destination;
        this.graph = graph;
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

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public String toJson ()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static Path fromJson (String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json,Path.class);
    }
}
