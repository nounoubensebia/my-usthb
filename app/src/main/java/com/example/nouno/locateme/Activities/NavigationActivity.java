package com.example.nouno.locateme.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.NavigationInstructionItem;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    private ArrayList<NavigationInstructionItem> navigationInstructionItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        listView = (ListView)findViewById(R.id.list);
        Bundle extras = getIntent().getExtras();

        navigationInstructions = new ArrayList<>();
        Gson gson = new Gson();
        navigationInstructions = (ArrayList<NavigationInstruction>) gson.fromJson(extras.getString("navigationInstructions"),ArrayList.class);
        //navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_LEFT,100));
        //navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_RIGHT,250));
        populateListView();
    }

    private void populateListView ()
    {
        navigationInstructionItems = new ArrayList<>();
        for (NavigationInstruction navigationInstruction : navigationInstructions)
        {
            navigationInstructionItems.add(new NavigationInstructionItem(navigationInstruction.getDirection(),navigationInstruction.getDistance(),navigationInstruction.getPolyline(),false));
        }
        navigationInstructionItems.get(0).setSelected(true);
        final NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,navigationInstructionItems);
        listView.setAdapter(navigationItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (NavigationInstructionItem navigationInstruction:navigationInstructionItems)
                {
                    navigationInstruction.setSelected(false);
                }
                navigationInstructionItems.get(position).setSelected(true);
                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);

    }
}
