package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nouno.locateme.Data.Filiere;
import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SectionActivity extends AppCompatActivity {
    String section;
    public static Info Temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        String json = getIntent().getExtras().getString("INFO");
        Info info = new Gson().fromJson(json,Info.class);
        Temp=info;
        ArrayList<Filiere> filieres=info.filieres;
        Spinner sp;
        sp = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> arraySpinner=new ArrayList<String>();
        for(int i=0;i<filieres.size();i++)
        {
            if(filieres.get(i).designation.equals(info.filiere))
            {
                for(int j=0;j<filieres.get(i).anet.size();j++)
                {
                    for(int k=0;k<filieres.get(i).anet.get(j).section.size();k++)
                    {
                        if(filieres.get(i).anet.get(j).annee.equals(Temp.annee+""))
                            arraySpinner.add(filieres.get(i).anet.get(j).section.get(k));
                    }
                }
            }
        }



        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.
                R.layout.simple_spinner_dropdown_item ,arraySpinner);

        sp.setAdapter(adapter);

        //Toast.makeText(this,text,Toast.LENGTH_LONG).show();

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                section=parentView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button buttonFiliere = (Button) findViewById(R.id.boutonSection);

        buttonFiliere.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String text="https://ent.usthb.dz/index.php?/Emp/xml/"+Temp.code+"/"+Temp.annee+"/"+section+"/1";
                Toast.makeText(SectionActivity.this,text, Toast.LENGTH_LONG).show();
                Intent i = new Intent(SectionActivity.this,StartActivity.class);
                URL info = null;
                try {
                    info = new URL(text);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                SharedPreference.saveString("INFO",info.toString(),SectionActivity.this);
                String json = new Gson().toJson(info);
                i.putExtra("INFO",json);
                startActivity(i);

            }
        });


    }


}
