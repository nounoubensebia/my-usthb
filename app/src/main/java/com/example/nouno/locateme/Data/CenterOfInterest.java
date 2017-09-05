package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 15/08/2017.
 */
public class CenterOfInterest extends Structure {
    private long structureId;
    private int type;

    public CenterOfInterest(long id, String label, Coordinate coordinate, String tags, long structureId, int type) {
        super(id, label, coordinate, tags);
        this.structureId = structureId;
        this.type = type;
    }

    public long getStructureId() {
        return structureId;
    }

    public int getType() {
        return type;
    }

    public static final int TYPE_MOSQUE = 0;
    public static final int TYPE_TOILETTE = 1;
    public static final int TYPE_CLUB = 2;
    public static final int TYPE_INFIRMERIE = 3;
    public static final int TYPE_BUVETTE = 4;
    public static final int TYPE_KIOSQUE = 5;
    public static final int TYPE_SORTIE = 6;
    public static final int TYPE_BIBLIOTEQUE = 7;
    public static final int TYPE_NOT_KNOWN = 8;

    public static String getTypeString (int type)
    {
        switch (type)
        {
            case TYPE_MOSQUE : return "Mosquée";
            case TYPE_BUVETTE : return "Buvette";
            case TYPE_SORTIE : return "Sortie";
            case TYPE_BIBLIOTEQUE : return "Bibliothèque";
            case TYPE_TOILETTE : return "Sanitaire";
            case TYPE_KIOSQUE : return "Kiosque";
        }
        return null;
    }
}
