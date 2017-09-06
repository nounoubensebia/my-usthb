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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.nouno.locateme.Activities.EmploiDuTempsActivity;
import com.example.nouno.locateme.Utils.Parseur;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SuperAwesomeCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SuperAwesomeCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuperAwesomeCardFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    @BindView(R.id.table)
    TableLayout table;

    private int position;

    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
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

        if(EmploiDuTempsActivity.wait==1) {
			/*String ContenuJour="";
			for(int i=0;i<Parseur.jours.get(position).creneaux.size();i++)
			{
				ContenuJour=ContenuJour+Parseur.jours.get(position).creneaux.get(i).horaire;
				for(int j=0;j<Parseur.jours.get(position).creneaux.get(i).seance.size();j++)
				{
					ContenuJour=ContenuJour+"\n\t "+Parseur.jours.get(position).creneaux.get(i).seance.get(j).module+" "
							+Parseur.jours.get(position).creneaux.get(i).seance.get(j).local;
				}
				ContenuJour = ContenuJour + "\n" ;
			}
			textView.setText(ContenuJour); */


            // données du tableau
			/*final String [] col1 = {"col1:ligne1","col1:ligne2","col1:ligne3","col1:ligne4","col1:ligne5"};
			final String [] col2 = {"col2:ligne1","col2:ligne2","col2:ligne3","col2:ligne4","col2:ligne5"}; */
            table = (TableLayout) rootView.findViewById(R.id.table);
            //TableLayout table = (TableLayout) findViewById(R.id.idTable); // on prend le tableau défini dans le layout
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
                    //horaireView.setText(Parseur.jours.get(i).creneaux.get(j).seance.get(j).module);
                    //horaireView.setGravity(Gravity.CENTER);
                    //horaireView.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ));
                    //row1.addView(horaireView);

                    chaine1="\t\t";
                    //for(int k=0;k<Parseur.jours.get(i).creneaux.get(j).seance.size();k++)
                    //{
                    if(Parseur.jours.get(position).creneaux.get(i).seance.get(j).type.equals("cours"))
                        chaine1="\t\tCours \t"+Parseur.jours.get(position).creneaux.get(i).seance.get(j).module+" ("+Parseur.jours.get(position).creneaux.get(i).seance.get(j).local+")\n";
                    else
                        chaine1 = chaine1+ "G"+Parseur.jours.get(position).creneaux.get(i).seance.get(j).groupe+" "+Parseur.jours.get(position).creneaux.get(i).seance.get(j).type+" "+Parseur.jours.get(position).creneaux.get(i).seance.get(j).module+" ("+Parseur.jours.get(position).creneaux.get(i).seance.get(j).local+")\n";

                    //}
                    seanceView = new TextView(getContext());
                    seanceView.setText(chaine1);
                    seanceView.setTypeface(null, Typeface.BOLD);
                    //  seanceView.setGravity(Gravity.CENTER);
                    seanceView.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                    row1.addView(seanceView);
                    table.addView(row1);
                }
                //table.addView(row);


            }


        }
        return rootView;
    }
}
