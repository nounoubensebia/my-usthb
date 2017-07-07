package com.example.nouno.locateme.Data;

import java.util.ArrayList;

/**
 * Created by nouno on 07/07/2017.
 */

public class NavigationInstructionItem extends NavigationInstruction {
    private boolean isSelected;
    public NavigationInstructionItem(int direction, double distance, ArrayList<Coordinate> polyline,boolean isSelected) {
        super(direction, distance,polyline);
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
