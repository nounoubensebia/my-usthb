package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nouno.locateme.Data.DoneCreneau;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

/**
 * Created by nouno on 09/09/2017.
 */

public class CreneauAdapter extends ArrayAdapter<DoneCreneau> {

    public CreneauAdapter (Context context, ArrayList<DoneCreneau> doneCreneaus)
    {
        super(context,0,doneCreneaus);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;

        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.item_creneau,parent, false);

        }
        TextView horaireTextView =(TextView) item.findViewById(R.id.text_horaire);
        TextView emptyTextView = (TextView) item.findViewById(R.id.text_empty);

        DoneCreneau doneCreneau = getItem(position);
        horaireTextView.setText(doneCreneau.getHoraire());
        ListView seanceList = (ListView) item.findViewById(R.id.list);
        if (doneCreneau.getDoneSeances().size()>0)
        {
            emptyTextView.setVisibility(View.GONE);
            seanceList.setVisibility(View.VISIBLE);
            SeanceAdapter seanceAdapter = new SeanceAdapter(getContext(),doneCreneau.getDoneSeances());
            seanceList.setAdapter(seanceAdapter);
            seanceList.setDividerHeight(0);
            justifyListViewHeightBasedOnChildren(seanceList);
        }
        else
        {
            emptyTextView.setVisibility(View.VISIBLE);
            seanceList.setVisibility(View.GONE);
        }
        return item;
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}
