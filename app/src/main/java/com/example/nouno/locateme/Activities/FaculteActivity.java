package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.nouno.locateme.Data.Filiere;
import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FaculteActivity extends AppCompatActivity {

    public static String fac;
    public static Info Temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculte);
        String json = getIntent().getExtras().getString("INFO");
        Info info = new Gson().fromJson(json,Info.class);
        Temp=info;
        final ArrayList<Filiere> filieres=info.filieres;
        Spinner sp;
        sp = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> arraySpinner=new ArrayList<String>();
        for(int i=0;i<filieres.size();i++)
        {
            if(!filieres.get(i).fac.equals("TC"))
            {
                arraySpinner.add(filieres.get(i).fac);
            }

        }

// add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(arraySpinner);
        arraySpinner.clear();
        arraySpinner.addAll(hs);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner); */

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.
                R.layout.simple_spinner_dropdown_item ,arraySpinner);

        sp.setAdapter(adapter);

        //Toast.makeText(this,text,Toast.LENGTH_LONG).show();

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                fac=parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Button faculte = (Button) findViewById(R.id.faculte);

        faculte.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(FaculteActivity.this,FiliereActivity.class) ;
                Info info = new Info(Temp.filieres,Temp.annee,null,null,Temp.cycle,fac);
                String json = new Gson().toJson(info);
                i.putExtra("INFO",json);
                startActivity(i);
            }
        });
    }


}