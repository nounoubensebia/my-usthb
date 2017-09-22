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
        groupText.setVisibility(View.VISIBLE);
        localText.setVisibility(View.VISIBLE);
        if (seance.groupe!=null&&!seance.groupe.equals("")&&!seance.groupe.equals("txt"))
        groupText.setText("Groupe "+seance.groupe+" "+seance.type);
        else
        {
            if (seance.groupe==null||seance.groupe.equals(""))
            groupText.setText(seance.type);
            else
                groupText.setVisibility(View.GONE);
        }

        if (seance.type!=null&&seance.type.equals("txt"))
        {
            groupText.setVisibility(View.GONE);
            localText.setVisibility(View.GONE);
        }
        moduleText.setText(seance.module);
        if (seance.local!=null&&!seance.local.equals(""))
        if (seance.local.charAt(seance.local.length()-1)==')')
        {
            StringBuilder sb = new StringBuilder(seance.local);
            sb.deleteCharAt(seance.local.length()-1);
            localText.setText(sb.toString());
        }
        else
        {
            localText.setText(seance.local);
        }
        return item;
    }
}
