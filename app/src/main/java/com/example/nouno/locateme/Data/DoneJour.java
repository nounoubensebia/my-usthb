package com.example.nouno.locateme.Data;

import java.util.ArrayList;

/**
 * Created by nouno on 09/09/2017.
 */

public class DoneJour {
    private ArrayList<DoneCreneau> creneaux;
    private String name;

    public DoneJour(ArrayList<DoneCreneau> creneaux, String name) {
        this.creneaux = creneaux;
        this.name = name;
    }

    public ArrayList<DoneCreneau> getCreneaux() {
        return creneaux;
    }

    public String getName() {
        return name;
    }

    public boolean isEmptyDay ()
    {
        for (DoneCreneau creneau:creneaux)
        {
            if (creneau.getDoneSeances().size()>0)
                return false;

        }
        return true;
    }
}
