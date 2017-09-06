package com.example.nouno.locateme.Data;

import java.util.ArrayList;

/**
 * Created by nouno on 06/09/2017.
 */

public class Creneau {
    public String horaire;
    public ArrayList<Seance> seance;
    public Creneau()
    {
        seance=new ArrayList<Seance>();
    }
    public void setHoraire(String horaire)
    {
        this.horaire=horaire;
    }
    public void setSeance(Seance seance)
    {
        this.seance.add(seance);
    }
}

