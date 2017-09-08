package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.google.gson.Gson;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MenuActivity extends AppCompatActivity {
    public static String url;


    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


    protected void onCreate(Bundle savedInstanceState) {
        //Avant de faire les configurations il faut tester si le fichier contenant l'emploi du temps existe
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
       /* String json = getIntent().getExtras().getString("INFO");
        URL info = new Gson().fromJson(json,URL.class); */
        url= SharedPreference.loadString("INFO",MenuActivity.this);

        Button timing = (Button) findViewById(R.id.timing);
        Button map = (Button) findViewById(R.id.map);
        Button reg = (Button) findViewById(R.id.reglage);

        timing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(MenuActivity.this,EmploiDuTempsActivity.class);
                URL info = null;
                try {
                    info = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String json = new Gson().toJson(info);
                i.putExtra("INFO",json);
                startActivity(i);
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(MenuActivity.this,NewMainAct.class);
                if(fileExistance("timing"))
                {
                    MenuActivity.this.deleteFile("timing");
                }
                startActivity(i);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,StartActivity.class);
                startActivity(i);
            }
        });
    }
}
