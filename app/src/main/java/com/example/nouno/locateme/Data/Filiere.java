package com.example.nouno.locateme.Data;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nouno on 06/09/2017.
 */

public class Filiere {
    public String code;
    public String designation;
    public String cycle;
    public String fac;
    public ArrayList<Anet> anet;

    public Filiere(String code, String designation, String cycle, String fac, ArrayList<Anet> anet) {
        this.code = code;
        this.designation = designation;
        this.cycle = cycle;
        this.fac = fac;
        this.anet = anet;
    }

    public void setDesignation(String designation)
    {
        this.designation=designation;
    }

    public static ArrayList<Filiere> parseJsonFiliere (String jsonString)
    {
        ArrayList<Filiere> filieres = new ArrayList<>();
        try {
            // Récupération de la racine

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0 ;i<jsonArray.length();i++) //parcours du tableau JSON
            {
                //récupération d'un element du tableau
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int code = jsonObject.getInt("code");
                String designation = jsonObject.getString("designation");
                //filieres.add(new Filiere(code,designation));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return filieres;
    }

    public static ArrayList<Filiere> fromJson (String json)
    {
        ArrayList<Filiere> filieres = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                String json1 = jsonArray.get(i).toString();
                Filiere filiere = new Gson().fromJson(json1,Filiere.class);
                filieres.add(filiere);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return filieres;
    }

}
