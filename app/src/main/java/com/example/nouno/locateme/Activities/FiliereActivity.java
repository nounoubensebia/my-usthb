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
import com.example.nouno.locateme.SharedPreference;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
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
                        Temp.code = code;
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
                if (oneSection(filieres,info)!=null)
                {
                    i = new Intent(FiliereActivity.this,WaitActivity.class);
                    String text="https://ent.usthb.dz/index.php?/Emp/xml/"+Temp.code+"/"+Temp.annee+"/"+oneSection(filieres,info)+"/1";
                    URL infoUrl = null;
                    try {
                        infoUrl = new URL(text);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Temp.filiere =filiere;
                    Temp.section = oneSection(filieres,info);
                    info.section = oneSection(filieres,info);
                    SharedPreference.saveString("INFO",infoUrl.toString(),FiliereActivity.this);
                    SharedPreference.saveString("TEMP",new Gson().toJson(Temp),FiliereActivity.this);
                    SharedPreference.saveString("URL",infoUrl.toString(),FiliereActivity.this);
                    String json = new Gson().toJson(infoUrl);
                    i.putExtra("INFO",json);
                    startActivity(i);
                }
                else
                {
                    String json = new Gson().toJson(info);
                    i.putExtra("INFO",json);
                    startActivity(i);
                }
            }
        });
    }

    private String oneSection (ArrayList<Filiere> filieres,Info info)
    {

        for(int i=0;i<filieres.size();i++)
        {
            if(filieres.get(i).designation.equals(info.filiere))
            {
                for(int j=0;j<filieres.get(i).anet.size();j++)
                {

                    if (filieres.get(i).anet.get(j).section.size()==1)

                    return filieres.get(i).anet.get(j).section.get(0);
                }
            }
        }
        return null;
    }
}
