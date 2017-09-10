package com.example.nouno.locateme.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private TextView anne;
    private TextView section;
    private TextView filiere;
    private TextView formation;
    private Info info;
    private Toolbar toolbar;
    public SettingsFragment() {
        // Required empty public constructor
    }
    private void getInfo ()
    {
        String inf = SharedPreference.loadString("TEMP",getActivity());
        info = new Gson().fromJson(inf,Info.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getInfo();
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Paramètres");
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(),android.R.color.white));
        anne = (TextView)view.findViewById(R.id.year);
        section = (TextView)view.findViewById(R.id.section);
        filiere = (TextView)view.findViewById(R.id.filiere);
        formation = (TextView)view.findViewById(R.id.formation);

        if (info.annee==1)
        {
            anne.setText("Première");
        }

        if (info.annee==2)
        {
            anne.setText("Deuxième");
        }

        if (info.annee==3)
        {
            anne.setText("Troisième");
        }

        section.setText(info.section);
        if (info.cycle.equals("M"))
        {
            formation.setText("Master");
        }
        else
        {
            formation.setText("Licence");
        }
        filiere.setText(info.filiere);

        return view;
    }

}
