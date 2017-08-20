package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 28/06/2017.
 */

public class SearchSuggestion {
    private long id;
    private Structure structure;
    private boolean isSpecial;

    public static final long ID_MY_POSITION = 0;
    public static final long ID_SET_ON_MAP = 1;
    public static final long ID_BUVETTE = 2;
    public static final long ID_SORTIE = 3;
    public static final long ID_SANNITAIRE = 4;
    public static final long ID_KIOSQUE = 5;
    public static final long ID_MOSQUE = 6;

    public SearchSuggestion(long id, Structure structure, boolean isSpecial) {
        this.id = id;
        this.structure = structure;
        this.isSpecial = isSpecial;
    }

    public long getId() {
        return id;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public Structure getStructure() {
        return structure;
    }
}
