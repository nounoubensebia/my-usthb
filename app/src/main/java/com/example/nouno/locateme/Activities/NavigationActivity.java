package com.example.nouno.locateme.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.ListAdapters.NavigationItemAdapter;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<NavigationInstruction> navigationInstructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        listView = (ListView)findViewById(R.id.list);
        navigationInstructions = new ArrayList<>();
        navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_LEFT,100));
        navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_RIGHT,250));
        populateListView();
    }

    private void populateListView ()
    {
        final NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,navigationInstructions);
        listView.setAdapter(navigationItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (NavigationInstruction navigationInstruction:navigationInstructions)
                {
                    navigationInstruction.setSelected(false);
                }
                navigationInstructions.get(position).setSelected(true);
                navigationItemAdapter.notifyDataSetChanged();
            }
        });
        listView.setDividerHeight(0);

    }
}
