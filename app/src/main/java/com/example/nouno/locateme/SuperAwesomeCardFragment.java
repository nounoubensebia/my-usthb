package com.example.nouno.locateme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.nouno.locateme.Data.Creneau;
import com.example.nouno.locateme.Data.DoneJour;
import com.example.nouno.locateme.Fragments.AgendaFragment;
import com.example.nouno.locateme.ListAdapters.CreneauAdapter;
import com.example.nouno.locateme.Utils.Parseur;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link SuperAwesomeCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuperAwesomeCardFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    static ArrayList<DoneJour> doneJours;
    private TextView emptyDayText;

    private int position;

    public static SuperAwesomeCardFragment newInstance(int position,ArrayList<DoneJour> doneJours) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        SuperAwesomeCardFragment.doneJours = doneJours;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        emptyDayText = (TextView) rootView.findViewById(R.id.text_empty_day);
        ArrayList<DoneJour> doneJours = null;
        String s = readFile();
        try {
             doneJours = Parseur.getJours(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doneJours!=null&&doneJours.size()>0)
        {   ListView listView = (ListView)rootView.findViewById(R.id.list);
            if (!doneJours.get(position).isEmptyDay())
            {
                listView.setVisibility(View.VISIBLE);
                CreneauAdapter creneauAdapter = new CreneauAdapter(getContext(),doneJours.get(position).getCreneaux());
                listView.setAdapter(creneauAdapter);
                listView.setDividerHeight(0);
                emptyDayText.setVisibility(View.GONE);
            }
            else
            {
                listView.setVisibility(View.GONE);
                emptyDayText.setVisibility(View.VISIBLE);
            }
        }


        /*if(AgendaFragment.wait==1) {

            table = (TableLayout) rootView.findViewById(R.id.table);

            TableRow row; // création d'un élément : ligne
            TextView jourView,horaireView,seanceView; // création des cellules
            String chaine1,chaine2;
            for(int i = 0; i< Parseur.jours.get(position).creneaux.size(); i++) {
                row = new TableRow(getActivity().getApplicationContext()); // création d'une nouvelle ligne

                jourView = new TextView(getContext()); // création cellule
                //chaine=Parseur.horaire.get(i);
                jourView.setText(Parseur.jours.get(position).creneaux.get(i).horaire); // ajout du texte
                jourView.setGravity(Gravity.CENTER); // centrage dans la cellule
                // adaptation de la largeur de colonne à l'écran :

                jourView.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                jourView.setTypeface(null, Typeface.BOLD);
                jourView.setTextColor(Color.parseColor("#FFFFFF"));
                jourView.setBackgroundColor(Color.parseColor("#0000FF"));
                row.addView(jourView);
                // ajout de la ligne au tableau
                table.addView(row);

                // idem 2ème cellule

                TableRow Saut=new TableRow(getContext());
                TextView Blanc=new TextView(getContext());
                Blanc.setText(" ");
                Saut.addView(Blanc);
                table.addView(Saut);
                for(int j=0;j<Parseur.jours.get(position).creneaux.get(i).seance.size();j++)
                {

                    horaireView = new TextView(getContext());
                    TableRow row1 = new TableRow(getContext());

                    chaine1="\t\t";

                    if(Parseur.jours.get(position).creneaux.get(i).seance.get(j).type.equals("cours"))
                        chaine1="\t\tCours \t"+Parseur.jours.get(position).creneaux.get(i).seance.get(j).module+" ("+Parseur.jours.get(position).creneaux.get(i).seance.get(j).local+")\n";
                    else
                        chaine1 = chaine1+ "G"+Parseur.jours.get(position).creneaux.get(i).seance.get(j).groupe+" "+Parseur.jours.get(position).creneaux.get(i).seance.get(j).type+" "+Parseur.jours.get(position).creneaux.get(i).seance.get(j).module+" ("+Parseur.jours.get(position).creneaux.get(i).seance.get(j).local+")\n";


                    seanceView = new TextView(getContext());
                    seanceView.setText(chaine1);
                    seanceView.setTypeface(null, Typeface.BOLD);

                    seanceView.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                    row1.addView(seanceView);
                    table.addView(row1);
                }



            }


        }*/
        return rootView;
    }

    private String readFile ()
    {
        FileInputStream fis = null;
        try {
            fis =getActivity().openFileInput("timing");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(EmploiDuTemps.this,"Lecture à partir du fichier", Toast.LENGTH_SHORT).show();
        String resultString=sb.toString();
        return resultString;
    }
}
