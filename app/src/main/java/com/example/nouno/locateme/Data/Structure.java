package com.example.nouno.locateme.Data;


/**
 * Created by nouno on 15/08/2017.
 */
public class Structure {
    private long id;
    private String label;
    private Coordinate coordinate;
    private String tags;

    public static final int STRUCTURE_DEPT_INFO = 1;
    public static final int STRUCTURE_NOUV_BLOC_B = 2;

    public static final int STRUCTURE_SALLE_100_200 = 3;
    public static final int STRUCTURE_FAC_PHYSIQUE = 4;
    public static final int STRUCTURE_CHALET_GENIE_CIVIL = 5;
    public static final int STRUCTURE_AMPHI_A_L = 6;
    public static final int STRUCTURE_SALLE_TD_300_400 = 7;
    public static final int STRUCTURE_FAC_BIO = 8;
    public static final int STRUCTURE_NOUVEAU_BLOC_C = 9;
    public static final int STRUCTURE_NOUVEAU_BLOC_A= 10;
    public static final int STRUCTURE_NOUVEAU_NOUVEAU_BLOC_B = 11;
    public static final int STRUCTURE_NOUVEAU_NOUVEAU_BLOC_C = 12;
    public static final int STRUCTURE_HALL_TECHNO = 13;
    public static final int STRUCTURE_BLOC_E_FAC_MATH = 14;
    public static final int STRUCTURE_VILLAGE_UNIVERSITAIRE= 15;
    public static final int STRUCTURE_BLOC_B_FAC_INFO = 16;
    public static final int STRUCTURE_CENTRE_CALCUL = 17;
    public static final int STRUCTURE_FAC_ELEC_INFO = 18;
    public static final int STRUCTURE_FAC_GEO = 19;
    public static final int STRUCTURE_FAC_GP_GM = 20;
    public static final int STRUCTURE_FAC_GC = 21;
    public static final int STRUCTURE_FAC_CHIMIE = 22;



    public Structure(long id, String label, Coordinate coordinate, String tags) {
        this.id = id;
        this.label = label;
        this.coordinate = coordinate;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getTags() {
        return tags;
    }

}
