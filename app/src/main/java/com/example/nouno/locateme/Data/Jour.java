package com.example.nouno.locateme.Data;

import java.util.ArrayList;

/**
 * Created by nouno on 06/09/2017.
 */

public class Jour {

    public String nom;
    public ArrayList<Creneau> creneaux;

    public Jour()
    {
        this.creneaux=new ArrayList<Creneau>();
    }

    public void setNom(String nom)
    {
        this.nom=nom;
    }
    public void setCreneau(Creneau c)
    {
        this.creneaux.add(c);
    }
}
