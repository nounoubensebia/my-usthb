package com.example.nouno.locateme.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nouno.locateme.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Toolbar toolbar;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Paramètres");
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(),android.R.color.white));

        return view;
    }

}
