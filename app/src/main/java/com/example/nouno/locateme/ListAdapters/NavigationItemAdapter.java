package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

/**
 * Created by nouno on 07/07/2017.
 */

public class NavigationItemAdapter extends ArrayAdapter<NavigationInstructionItem> {
    public NavigationItemAdapter (Context context, ArrayList<NavigationInstructionItem> navigationInstructionsItems)
    {
        super(context,0,navigationInstructionsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;

        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.item_navigation_instruction,parent, false);

        }
        TextView instructionDesctiptionText = (TextView) item.findViewById(R.id.text_instruction_description);
        TextView instructionOrderText = (TextView)item.findViewById(R.id.text_instruction_order);
        instructionDesctiptionText.setText(getItem(position).getInstructionString());
        instructionOrderText.setText(position+1+"");
        View button = item.findViewById(R.id.button_instruction);
        View separationView = item.findViewById(R.id.view_separation);
        View root = item.findViewById(R.id.root);
        if (getItem(position).isSelected())
        {
            button.setActivated(true);
            instructionOrderText.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            button.setActivated(false);
            instructionOrderText.setTextColor(getContext().getResources().getColor(R.color.backgroundColor));
        }
        if (position==getCount()-1)
        {
            Resources r = item.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());
            separationView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)root.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin,layoutParams.topMargin,layoutParams.rightMargin,(int)px);
            root.setLayoutParams(layoutParams);
        }
        else
        {
            separationView.setVisibility(View.VISIBLE);
        if (position == 0)
        {
            Resources r = item.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)root.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin,(int)px,layoutParams.rightMargin,layoutParams.bottomMargin);
            root.setLayoutParams(layoutParams);
        }
        else
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)root.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);
            root.setLayoutParams(layoutParams);

        }}

        return item;
    }

}
