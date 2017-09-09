package com.example.nouno.locateme.Data;

import java.util.ArrayList;

/**
 * Created by nouno on 09/09/2017.
 */

public class DoneCreneau {
    private String horaire;
    private ArrayList<Seance> doneSeances;

    public DoneCreneau(String horaire, ArrayList<Seance> doneSeances) {
        this.horaire = horaire;
        this.doneSeances = doneSeances;
    }

    public String getHoraire() {
        return horaire;
    }

    public ArrayList<Seance> getDoneSeances() {
        return doneSeances;
    }
}
