package com.example.nouno.locateme.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

/**
 * Created by nouno on 07/07/2017.
 */

public class NavigationItemAdapter extends ArrayAdapter<NavigationInstruction> {
    public NavigationItemAdapter (Context context, ArrayList<NavigationInstruction> navigationInstructions)
    {
        super(context,0,navigationInstructions);
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
            separationView.setVisibility(View.GONE);
        }
        else
        {
            separationView.setVisibility(View.VISIBLE);
        }
        return item;
    }

}
