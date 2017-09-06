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

public class FiliereActivity extends AppCompatActivity {

    public static String filiere,code;
    public static Info Temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filiere);
        String json = getIntent().getExtras().getString("INFO");
        Info info = new Gson().fromJson(json,Info.class);
        Temp=info;
        final ArrayList<Filiere> filieres=info.filieres;
        Spinner sp;
        sp = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> arraySpinner=new ArrayList<String>();
        if((Temp.annee==1) && (Temp.cycle.equals("L")))
        {
            for(int i=0;i<filieres.size();i++)
            {
                for(int j=0;j<filieres.get(i).anet.size();j++)
                {
                    if(filieres.get(i).anet.get(j).annee.equals((Temp.annee+""))&&(filieres.get(i).cycle.equals(Temp.cycle))&& filieres.get(i).fac.equals("TC"))
                    {
                        arraySpinner.add(filieres.get(i).designation);
                    }
                }
            }
        }
        else
        {
            for(int i=0;i<filieres.size();i++)
            {
                for(int j=0;j<filieres.get(i).anet.size();j++)
                {
                    if(filieres.get(i).anet.get(j).annee.equals((Temp.annee+""))&&(filieres.get(i).cycle.equals(Temp.cycle))&& filieres.get(i).fac.equals(Temp.fac))
                    {
                        arraySpinner.add(filieres.get(i).designation);
                    }
                }

            }
        }

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

                filiere=parentView.getItemAtPosition(position).toString();
                for(int i=0;i<filieres.size();i++)
                {
                    if(filiere.equals(filieres.get(i).designation))
                    {
                        code=filieres.get(i).code;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button buttonFiliere = (Button) findViewById(R.id.boutonFiliere);

        buttonFiliere.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(FiliereActivity.this,SectionActivity.class) ;
                Info info = new Info(Temp.filieres,Temp.annee,filiere,code,Temp.cycle,Temp.fac);
                String json = new Gson().toJson(info);
                i.putExtra("INFO",json);
                startActivity(i);
            }
        });
    }
}
