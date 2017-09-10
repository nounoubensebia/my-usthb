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

public class AnneMasterActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_anne_master);
        // ConnexionTask connexionTask = new ConnexionTask();
        //connexionTask.execute(new LinkedHashMap<String, String>());
        getInfo();

        Button buttonM1 = (Button) findViewById(R.id.M1);
        Button buttonM2 = (Button) findViewById(R.id.M2);


        buttonM1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                annee=1;
                Intent i = new Intent(AnneMasterActivity.this, FaculteActivity.class);
                info.cycle = "M";
                info.annee = annee;
                i.putExtra("INFO",new Gson().toJson(info));
                startActivity(i);
            }
        });


        buttonM2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(AnneMasterActivity.this, FaculteActivity.class);
                annee=2;
                info.annee = annee;
                info.cycle = "M";
                i.putExtra("INFO",new Gson().toJson(info));

                startActivity(i);
            }
        });
    }
}

