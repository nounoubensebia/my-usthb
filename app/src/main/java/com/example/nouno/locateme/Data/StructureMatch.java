package com.example.nouno.locateme.Data;

import android.support.annotation.NonNull;

import com.company.Structure;

/**
 * Created by nouno on 20/08/2017.
 */

public class StructureMatch implements Comparable {
    private Structure structure;
    private int match;

    public StructureMatch(Structure structure, int match) {
        this.structure = structure;
        this.match = match;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setMatch(int match) {
        this.match = match;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        StructureMatch structureMatch = (StructureMatch)o;
        return (structureMatch.match-match);

    }

    public int getMatch() {
        return match;
    }
}
