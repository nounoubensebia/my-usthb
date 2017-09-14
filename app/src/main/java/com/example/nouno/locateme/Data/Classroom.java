package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.R;

/**
 * Created by nouno on 15/08/2017.
 */
public class Classroom extends Structure {
    private long structureId;

    public Classroom(long id, String label, Coordinate coordinate, String tags, long structureId) {
        super(id, label, coordinate, tags);
        this.structureId = structureId;
    }

    public long getStructureId() {
        return structureId;
    }

    @Override
    public int getDrawableResource() {
        if (getLabel().contains("Amphi"))
        {
            return R.drawable.amphi;
        }
        return -1;
    }

    @Override
    public int getVectorDrawable() {

        return R.drawable.ic_logo_usthb_blue;
    }
}
