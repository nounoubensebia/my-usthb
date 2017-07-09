package com.example.nouno.locateme.Djikstra;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Utils.MapGeometryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouno on 25/06/2017.
 */
public class Edge  {
    private final long id;
    private Vertex source;
    private Vertex destination;
    private double weight;
    private ArrayList<Coordinate> coordinates;

    public Edge(long id, Vertex source, Vertex destination, double weight,ArrayList<Coordinate> coordinates) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.coordinates = coordinates;
    }

    public long getId() {
        return id;
    }
    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public double getWeight() {
        return MapGeometryUtils.PolylineDistance(coordinates);
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public static Edge getEdgeById (List<Edge> edges, long id)
    {
        for (Edge e:edges)
        {
            if (e.getId()==id)
            {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public ArrayList<Coordinate> getLastPolyline ()
    {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(this.coordinates.get(this.coordinates.size()-2));
        coordinates.add(this.coordinates.get(this.coordinates.size()-1));
        return coordinates;
    }

    public static  ArrayList<Coordinate> getPolyline (ArrayList<Edge> edges)
    {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Edge e:edges)
        {
            coordinates.addAll(e.getCoordinates());
        }
        return coordinates;
    }
}