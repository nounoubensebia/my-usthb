package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nouno.locateme.Data.DoneCreneau;
import com.example.nouno.locateme.Data.DoneSeance;
import com.example.nouno.locateme.Data.Seance;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

/**
 * Created by nouno on 09/09/2017.
 */

public class SeanceAdapter extends ArrayAdapter<Seance> {

    public SeanceAdapter (Context context, ArrayList<Seance> doneSeances)
    {
        super(context,0,doneSeances);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        Seance seance = getItem(position);
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.item_seance,parent, false);

        }
        TextView groupText = (TextView) item.findViewById(R.id.text_group);
        TextView moduleText = (TextView) item.findViewById(R.id.text_module);
        TextView localText = (TextView) item.findViewById(R.id.text_local);
        if (seance.groupe!=null&&!seance.groupe.equals(""))
        groupText.setText("Groupe "+seance.groupe+" "+seance.type);
        else
        {
            groupText.setText(seance.type);
        }
        moduleText.setText(seance.module);
        localText.setText(seance.local);
        return item;
    }
}
