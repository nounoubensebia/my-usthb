package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nouno.locateme.Data.SearchSuggestion;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

/**
 * Created by nouno on 28/06/2017.
 */

public class SearchItemAdapter extends ArrayAdapter<SearchSuggestion> {
    public SearchItemAdapter (Context context, ArrayList<SearchSuggestion> searchSuggestions)
    {
        super (context,0,searchSuggestions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchSuggestion searchSuggestion = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.search_list_item ,parent, false);
        }
        TextView bigLabel = (TextView) item.findViewById(R.id.biglabel);
        TextView smallLabel = (TextView)item.findViewById(R.id.smallLabel);
        ImageView imageView = (ImageView)item.findViewById(R.id.image_view);
        bigLabel.setText(searchSuggestion.getBlocName());
        smallLabel.setText(searchSuggestion.getClassroomName());
        if ((position % 2)==0)
            //Resources resources = item.getResources();

        imageView.setImageDrawable(ContextCompat.getDrawable(item.getContext(),R.drawable.test));
        else
            imageView.setImageDrawable(ContextCompat.getDrawable(item.getContext(),R.drawable.test2));
        return item;
    }
}
