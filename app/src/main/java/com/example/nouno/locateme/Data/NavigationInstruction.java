package com.example.nouno.locateme.Data;

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
    private boolean isSelected;
    public NavigationInstruction(int direction, double distance) {
        this.direction = direction;
        this.distance = distance;
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
        String s = "Marcher "+(int)distance+" métres ";
        switch (direction)
        {
            case DIRECTION_LEFT :
                s+="puis tourner a gauche";
                break;
            case DIRECTION_RIGHT :
                s+="puis tourner a droite";
        }
        return s;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
