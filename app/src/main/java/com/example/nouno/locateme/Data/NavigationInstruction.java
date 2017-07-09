package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.Utils.MapGeometryUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 07/07/2017.
 */

public class NavigationInstruction {
    public static final int DIRECTION_FRONT = 0;
    public static final int DIRECTION_BACK = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;

    private int direction;
    private double distance;

    private ArrayList<Coordinate> polyline;

    public NavigationInstruction(int direction, double distance,ArrayList<Coordinate> polyline) {
        this.direction = direction;
        this.distance = distance;

        this.polyline = polyline;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getInstructionString ()
    {
        String s = "Marcher "+(int)(distance*1000)+ " métres ";
        switch (direction)
        {
            case DIRECTION_LEFT :
                s+="puis tourner à gauche";
                break;
            case DIRECTION_RIGHT :
                s+="puis tourner à droite";
                break;
            default :
                s+="Pour atteindre votre destination";

        }
        return s;
    }
    public float getDuration()
    {
        return MapGeometryUtils.getDuration(distance);
    }

    public String getDurationString ()
    {
        NumberFormat nf = new DecimalFormat("0.#");
        String s = nf.format(getDuration()/60);
        return s+" min";
    }



    public ArrayList<Coordinate> getPolyline() {
        return polyline;
    }

    public void setPolyline(ArrayList<Coordinate> polyline) {
        this.polyline = polyline;
    }
}
