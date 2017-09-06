package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 06/09/2017.
 */

import java.util.ArrayList;

/**
 * Created by boukala on 22/08/2017.
 */

public class Info {
    public ArrayList<Filiere> filieres;
    public int annee;
    public String filiere;
    public String code;
    public String cycle;
    public String fac;

    public Info(ArrayList<Filiere> filieres, int annee, String filiere, String code,String cycle,String fac) {
        this.filieres = filieres;
        this.annee = annee;
        this.filiere=filiere;
        this.code=code;
        this.cycle=cycle;
        this.fac=fac;
    }
}

