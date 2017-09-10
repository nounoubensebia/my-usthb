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
    private Info info;
    ArrayList<Filiere> filieres;
    private void getInfo ()
    {
        String inf = getIntent().getExtras().getString("INFO");
        info = new Gson().fromJson(inf,Info.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annee_license);
        // ConnexionTask connexionTask = new ConnexionTask();
        //connexionTask.execute(new LinkedHashMap<String, String>());

        getInfo();
        Button buttonL1 = (Button) findViewById(R.id.l1);
        Button buttonL2 = (Button) findViewById(R.id.l2);
        Button buttonL3 = (Button) findViewById(R.id.l3);


        buttonL1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=1;
                Intent i = new Intent(AnneeLicense.this, FiliereActivity.class);
                info.cycle = "L";
                info.annee = annee;
                i.putExtra("INFO",new Gson().toJson(info));
                startActivity(i);
                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });


        buttonL2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=2;
                Intent i = new Intent(AnneeLicense.this, FaculteActivity.class);
                info.cycle = "L";
                info.annee = annee;
                i.putExtra("INFO",new Gson().toJson(info));
                startActivity(i);
                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });
        buttonL3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=3;
                Intent i = new Intent(AnneeLicense.this, FaculteActivity.class);
                info.cycle = "L";
                info.annee = annee;
                i.putExtra("INFO",new Gson().toJson(info));
                startActivity(i);

                //Intent i = new Intent(AnneeLicence.this, AnneeLicence.class);
                //startActivity(i);
            }
        });

    }
}
