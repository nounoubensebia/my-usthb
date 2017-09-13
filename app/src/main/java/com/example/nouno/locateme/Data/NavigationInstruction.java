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
    private int startOrder;
    private int endOrder;


    public NavigationInstruction(int direction, double distance, int startOrder,int endOrder) {
        this.direction = direction;
        this.distance = distance;
        this.startOrder = startOrder;
        this.endOrder = endOrder;
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
        String s = "Aprés "+(int)(distance*1000)+ " mètres de marche ";
        switch (direction)
        {
            case DIRECTION_LEFT :
                s+="prendre à gauche";
                break;
            case DIRECTION_RIGHT :
                s+="prendre à droite";
                break;
            default :
                s="Votre destination se trouve à "+(int)(distance*1000)+ " mètres";

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

    public int getStartOrder() {
        return startOrder;
    }

    public void setStartOrder(int startOrder) {
        this.startOrder = startOrder;
    }

    public int getEndOrder() {
        return endOrder;
    }

    public void setEndOrder(int endOrder) {
        this.endOrder = endOrder;
    }
}
