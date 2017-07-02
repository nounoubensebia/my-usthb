package com.example.nouno.locateme.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nouno.locateme.Data.Place;
import com.example.nouno.locateme.Data.SearchSuggestion;
import com.example.nouno.locateme.Fragments.SearchFragment;
import com.example.nouno.locateme.ListAdapters.SearchItemAdapter;
import com.example.nouno.locateme.R;

import java.util.ArrayList;

public class SearchQueryActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<SearchSuggestion> searchSuggestions;
    private Place mUserLocation;
    private TextView userPositionLabel;
    private TextView userPositionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.list);
        userPositionLabel = (TextView) findViewById(R.id.user_position_label);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        searchSuggestions = new ArrayList<>();
        searchSuggestions.add(new SearchSuggestion("Faculté de physique","Salle 123"));
        searchSuggestions.add(new SearchSuggestion("Faculté d'informatique","Salle 124"));
        SearchItemAdapter searchItemAdapter = new SearchItemAdapter(this,searchSuggestions);
        listView.setAdapter(searchItemAdapter);
        listView.setDividerHeight(0);
        if (getIntent().getExtras()!=null)
        mUserLocation = Place.fromJson(getIntent().getExtras().getString(SearchFragment.USER_LOCATION_JSON));
        if (mUserLocation!=null)
        {
            if (mUserLocation.isInsideCampus())
            {
                userPositionLabel.setText("Position actuelle");
                userPositionText.setText("Prés de");
            }
            else
            {
                userPositionLabel.setText("Vous ne pouvez pas utiliser votre positon");
                userPositionText.setText("Vous êtes en dehors du campus");
            }
        }
        else
        {
            userPositionLabel.setText("Vous ne pouvez pas utiliser votre positon");
            userPositionText.setText("Impossible de déterminer votre position");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_searche);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setFocusable(true);
        searchView.setQueryHint("Rechercher un lieu");
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 1) {

                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
