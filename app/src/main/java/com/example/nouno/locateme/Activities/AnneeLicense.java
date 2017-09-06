package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nouno.locateme.Data.Filiere;
import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.Data.WebResponse;
import com.example.nouno.locateme.QueryUtils;
import com.example.nouno.locateme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AnneeLicense extends AppCompatActivity {

    public static int annee;

    ArrayList<Filiere> filieres;
    private class ConnexionTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... params) {
            WebResponse webResponse = QueryUtils.makeHttpGetRequest(params[0],
                    new LinkedHashMap<String, String>());
            if (webResponse.isError()) {
                //Toast.makeText(MainActivity.this,"Veuillez vous connecter d'abord !",Toast.LENGTH_LONG).show();

                return null;//erreur
            } else {
                //Toast.makeText(MainActivity.this,"Connexion établie !",Toast.LENGTH_LONG).show();
                return webResponse.getResponseString();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                if (annee == 1) {
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();
                    filieres = Filiere.fromJson(resultString);
                    Intent i = new Intent(AnneeLicense.this, FiliereActivity.class);
                    Info info = new Info(filieres, annee, null, null, "L",null);
                    String json = new Gson().toJson(info);
                    i.putExtra("INFO", json);
                    startActivity(i);
                }
                else
                {
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();
                    filieres = Filiere.fromJson(resultString);
                    Intent i = new Intent(AnneeLicense.this, FaculteActivity.class);
                    Info info = new Info(filieres, annee, null, null, "L",null);
                    String json = new Gson().toJson(info);
                    i.putExtra("INFO", json);
                    startActivity(i);
                }

            } catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annee_license);
        // ConnexionTask connexionTask = new ConnexionTask();
        //connexionTask.execute(new LinkedHashMap<String, String>());


        Button buttonL1 = (Button) findViewById(R.id.l1);
        Button buttonL2 = (Button) findViewById(R.id.l2);
        Button buttonL3 = (Button) findViewById(R.id.l3);


        buttonL1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=1;
                ConnexionTask connexionTask = new ConnexionTask();
                String URL="https://ent.usthb.dz/index.php?/Emp/filieres";
                connexionTask.execute(URL);
                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });


        buttonL2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=2;
                ConnexionTask connexionTask = new ConnexionTask();
                String URL="https://ent.usthb.dz/index.php?/Emp/filieres";
                connexionTask.execute(URL);
                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });
        buttonL3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=3;
                ConnexionTask connexionTask = new ConnexionTask();
                String URL="https://ent.usthb.dz/index.php?/Emp/filieres";
                connexionTask.execute(URL);
                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });

    }
}
