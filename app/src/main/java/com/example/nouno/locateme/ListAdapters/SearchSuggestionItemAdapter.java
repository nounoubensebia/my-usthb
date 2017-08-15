package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nouno.locateme.Activities.GlideApp;
import com.example.nouno.locateme.Data.SearchSuggestion;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nouno on 15/08/2017.
 */

public class SearchSuggestionItemAdapter extends RecyclerView.Adapter<SearchSuggestionItemAdapter.ViewHolder> {
    private ArrayList<SearchSuggestion> searchSuggestions;
    int itemLayout;
    private Context context;
    public SearchSuggestionItemAdapter (Context context,ArrayList<SearchSuggestion> searchSuggestions, int itemLayout)
    {
        this.searchSuggestions = searchSuggestions;
        this.itemLayout = itemLayout;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return searchSuggestions.get(position).getId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void updateItems(ArrayList<SearchSuggestion> newItems) {
        searchSuggestions.clear();
        searchSuggestions.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchSuggestion searchSuggestion = searchSuggestions.get(position);
        if (searchSuggestion.isSpecial())
        {
            holder.centerOfInterestLayout.setVisibility(View.VISIBLE);
            holder.structureLayout.setVisibility(View.GONE);
        }
        else
        {
            holder.centerOfInterestLayout.setVisibility(View.GONE);
            holder.structureLayout.setVisibility(View.VISIBLE);
            holder.bigLabel.setText(searchSuggestion.getBlocName());
            holder.smallLabel.setText(searchSuggestion.getClassroomName());

            //Resources resources = item.getResources();

            try {

                String encodedImage = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    encodedImage = FileUtils.readFile(context.getAssets().open("filename.txt"));
                }
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                GlideApp.with(context).asBitmap().load(decodedString).dontAnimate().skipMemoryCache(true).into(holder.imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return searchSuggestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bigLabel;
        private TextView smallLabel;
        private ImageView imageView;
        private View centerOfInterestLayout;
        private View structureLayout;
        private TextView centerOfInterestsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            bigLabel = (TextView) itemView.findViewById(R.id.biglabel);
            smallLabel = (TextView)itemView.findViewById(R.id.smallLabel);
            imageView = (ImageView)itemView.findViewById(R.id.image_view);
            centerOfInterestLayout = itemView.findViewById(R.id.layout_center_of_interest);
            structureLayout = itemView.findViewById(R.id.layout_structure);
            centerOfInterestsTextView = (TextView)itemView.findViewById(R.id.text_center_of_interest_label);
        }
    }
}
