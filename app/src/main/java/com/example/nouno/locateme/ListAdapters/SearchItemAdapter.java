package com.example.nouno.locateme.ListAdapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.example.nouno.locateme.Activities.GlideApp;
import com.example.nouno.locateme.Activities.MyAppGlideModule;
import com.example.nouno.locateme.Data.SearchSuggestion;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.Utils.FileUtils;
import com.google.android.gms.wearable.Asset;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nouno on 28/06/2017.
 */

public class SearchItemAdapter extends ArrayAdapter<SearchSuggestion> {
    public SearchItemAdapter (Context context, ArrayList<SearchSuggestion> searchSuggestions)
    {
        super (context,0,searchSuggestions);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SearchSuggestion searchSuggestion = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.item_place_suggestion ,parent, false);
        }
        TextView bigLabel = (TextView) item.findViewById(R.id.biglabel);
        TextView smallLabel = (TextView)item.findViewById(R.id.smallLabel);
        ImageView imageView = (ImageView)item.findViewById(R.id.image_view);
        bigLabel.setText(searchSuggestion.getBlocName());
        smallLabel.setText(searchSuggestion.getClassroomName());

            //Resources resources = item.getResources();

        try {

            String encodedImage = FileUtils.readFile(getContext().getAssets().open("filename.txt"));
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //imageView.setImageBitmap(decodedByte);
            //Glide.with(getContext()).asBitmap().load(decodedString).into(imageView);
            GlideApp.with(getContext()).asBitmap().load(decodedString).dontAnimate().skipMemoryCache(true).into(imageView);
            //MyAppGlideModule.with(getContext()).load(decodedString).asBitmap().placeholder(R.drawable.ic_location_green_24dp).into(imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return item;
    }
}
