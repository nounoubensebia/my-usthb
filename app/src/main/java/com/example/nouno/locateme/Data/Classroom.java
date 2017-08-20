package com.example.nouno.locateme.Data;

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
}
