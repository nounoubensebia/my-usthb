package com.example.nouno.locateme.Data;


import com.example.nouno.locateme.R;

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

    public int getDrawableResource ()
    {
        if (! (this instanceof Classroom)&&!(this instanceof CenterOfInterest))
        {
            switch ((int) id)
            {
                case 0:return R.drawable.departement_informatique;
                case 1 :return R.drawable.nouveau_bloc;
                case 2:return R.drawable.salle_td_100_200;
                case 3:return R.drawable.faculte_physique;
                case 5:return R.drawable.salle_td_300_400;
                case 6:return R.drawable.faculte_biologie;
                case 7:
                case 8:
                case 9:
                case 10: return R.drawable.nouveau_bloc;
                case 11:return R.drawable.hall_technologie;
                case 13:return R.drawable.village;
                case 15:return R.drawable.centre_de_calcul;
                case 16:return R.drawable.faculte_electronique_et_informatique;
                case 20:return R.drawable.faculte_chimie;
                case 21:return R.drawable.rectorat;
                case 24:return R.drawable.salle_de_sport;
                case 25:return R.drawable.auditorium;
                case 26:return R.drawable.cyber_espace;
            }
        }
        else
        {
            if (this instanceof CenterOfInterest)
            {
                CenterOfInterest centerOfInterest = (CenterOfInterest) this;
                if (centerOfInterest.getType()==CenterOfInterest.TYPE_TOILETTE)
                {
                    return R.drawable.toilet;
                }
                else
                {
                    switch ((int)centerOfInterest.getId())
                    {
                        case 36:return R.drawable.buvette_departement_informatique;
                        case 37:return R.drawable.kiosque_informatique;
                        case 38:return R.drawable.kiosque_faculte_genie_civil;
                        case 63:return R.drawable.kiosque_espace_internet;
                    }
                }

            }
        }
        return -1;
    }

}
