package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nouno.locateme.Activities.GlideApp;
import com.example.nouno.locateme.Data.SearchSuggestion;
import com.example.nouno.locateme.OnButtonClickListner;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.FileUtils;
import com.example.nouno.locateme.Utils.LayoutUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nouno on 15/08/2017.
 */

public class SearchSuggestionItemAdapter extends RecyclerView.Adapter<SearchSuggestionItemAdapter.ViewHolder> {
    private ArrayList<SearchSuggestion> searchSuggestions;
    int itemLayout;
    private Context context;
    private OnButtonClickListner.OnButtonClickListener onMyPositionClickListner;
    private OnButtonClickListner.OnButtonClickListener onSetLocationOnMapClickListner;
    public SearchSuggestionItemAdapter (Context context,ArrayList<SearchSuggestion> searchSuggestions, int itemLayout)
    {
        this.searchSuggestions = searchSuggestions;
        this.itemLayout = itemLayout;
        this.context = context;
        setHasStableIds(true);
    }


    public OnButtonClickListner.OnButtonClickListener getOnMyPositionClickListner() {
        return onMyPositionClickListner;
    }

    public void setOnMyPositionClickListner(OnButtonClickListner.OnButtonClickListener onMyPositionClickListner) {
        this.onMyPositionClickListner = onMyPositionClickListner;
    }

    public OnButtonClickListner.OnButtonClickListener getOnSetLocationOnMapClickListner() {
        return onSetLocationOnMapClickListner;
    }

    public void setOnSetLocationOnMapClickListner(OnButtonClickListner.OnButtonClickListener onSetLocationOnMapClickListner) {
        this.onSetLocationOnMapClickListner = onSetLocationOnMapClickListner;
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
            holder.suggestionTextLayout.setVisibility(View.GONE);
            holder.centerOfInterestLayout.setVisibility(View.VISIBLE);
            if (position == 0)
            {


            }

            holder.structureLayout.setVisibility(View.GONE);
            Drawable textDrawable = null;
            if (searchSuggestion.getId()==SearchSuggestion.ID_MY_POSITION)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, (int)LayoutUtils.dpToPixel(context,16),layoutParams.rightMargin,layoutParams.bottomMargin);
                holder.centerOfInterestsTextView.setText("Ma position actuelle");
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_near_me_black_24dp);
                holder.centerOfInterestLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMyPositionClickListner.OnClick(null);
                    }
                });
            }
            if (searchSuggestion.getId()==SearchSuggestion.ID_SET_ON_MAP)
            {
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_nature_black_24dp);
                holder.centerOfInterestsTextView.setText("Choisir une position sur la carte");
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                holder.centerOfInterestLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSetLocationOnMapClickListner.OnClick(null);
                    }
                });
                holder.suggestionTextLayout.setVisibility(View.VISIBLE);
            }
            if (searchSuggestion.getId()==SearchSuggestion.ID_BUVETTE)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_food);
                holder.centerOfInterestsTextView.setText("Buvette");
            }
            if (searchSuggestion.getId()==SearchSuggestion.ID_KIOSQUE)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_kiosk);
                holder.centerOfInterestsTextView.setText("Kiosque");
            }

            if (searchSuggestion.getId()==SearchSuggestion.ID_SANNITAIRE)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_toilet);
                holder.centerOfInterestsTextView.setText("Sanitaire");
            }

            if (searchSuggestion.getId()==SearchSuggestion.ID_SORTIE)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_sortie);
                holder.centerOfInterestsTextView.setText("Sortie");
            }
            if (searchSuggestion.getId() == SearchSuggestion.ID_MOSQUE)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.centerOfInterestLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
                textDrawable = ContextCompat.getDrawable(context,R.drawable.ic_mosque);
                holder.centerOfInterestsTextView.setText("Lieu de prière");
            }

            holder.centerOfInterestsTextView.setCompoundDrawablesWithIntrinsicBounds(textDrawable,null,null,null);
        }
        else
        {
            holder.suggestionTextLayout.setVisibility(View.GONE);
            holder.centerOfInterestLayout.setVisibility(View.GONE);
            holder.structureLayout.setVisibility(View.VISIBLE);
            holder.bigLabel.setText(searchSuggestion.getBlocName());
            holder.smallLabel.setText(searchSuggestion.getClassroomName());
            if (position == 0)
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.structureLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, (int)LayoutUtils.dpToPixel(context,16),layoutParams.rightMargin,layoutParams.bottomMargin);
            }
            else
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.structureLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,0,layoutParams.rightMargin,layoutParams.bottomMargin);
            }
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
        private View suggestionTextLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            bigLabel = (TextView) itemView.findViewById(R.id.biglabel);
            smallLabel = (TextView)itemView.findViewById(R.id.smallLabel);
            imageView = (ImageView)itemView.findViewById(R.id.image_view);
            centerOfInterestLayout = itemView.findViewById(R.id.layout_center_of_interest);
            structureLayout = itemView.findViewById(R.id.layout_structure);
            centerOfInterestsTextView = (TextView)itemView.findViewById(R.id.text_center_of_interest_label);
            suggestionTextLayout = itemView.findViewById(R.id.layout_suggestions_text);
        }
    }


}
