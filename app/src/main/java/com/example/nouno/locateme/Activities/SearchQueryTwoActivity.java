package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.nouno.locateme.R;

public class SearchQueryTwoActivity extends AppCompatActivity {
    TextView setPositionOnMapTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query_two);
        getSupportActionBar().setElevation(0);
        setPositionOnMapTextView = (TextView)findViewById(R.id.set_position_on_map);
        setPositionOnMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchQueryTwoActivity.this,SetMarkerActivity.class);
                startActivity(i);
            }
        });
    }
}
