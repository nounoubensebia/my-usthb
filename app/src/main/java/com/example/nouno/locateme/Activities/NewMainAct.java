package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nouno.locateme.ConnexionNet;
import com.example.nouno.locateme.R;

import java.io.File;

public class NewMainAct extends AppCompatActivity {

    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Avant de faire les configurations il faut tester si le fichier contenant l'emploi du temps existe


        if(fileExistance("timing")) {
            //Toast.makeText(MainActivity.this, "Le fichier timing existe déjà", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(NewMainAct.this, StartActivity.class);
            startActivity(i);
            finish();
        }
        else {


            setContentView(R.layout.activity_new_main);


            Button buttonLicence = (Button) findViewById(R.id.buttonLicence);
            Button buttonMaster = (Button) findViewById(R.id.buttonMaster);

            buttonLicence.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConnexionNet cn= new ConnexionNet(NewMainAct.this);
                    if(cn.isConnected()) {
                        Intent i = new Intent(NewMainAct.this, AnneeLicense.class);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(NewMainAct.this,"Vous devez être connecté",Toast.LENGTH_LONG).show();
                    }
                }
            });

            buttonMaster.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConnexionNet cn= new ConnexionNet(NewMainAct.this);
                    if(cn.isConnected()) {
                        Intent i = new Intent(NewMainAct.this,AnneMasterActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(NewMainAct.this,"Vous devez être connecté",Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }
}
