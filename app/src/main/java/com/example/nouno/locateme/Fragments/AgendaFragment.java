package com.example.nouno.locateme.Fragments;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.nouno.locateme.ConnexionNet;
import com.example.nouno.locateme.Data.DoneJour;
import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.Data.WebResponse;
import com.example.nouno.locateme.PagerSlidingTabStrip;
import com.example.nouno.locateme.QueryUtils;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.example.nouno.locateme.SuperAwesomeCardFragment;
import com.example.nouno.locateme.Utils.Parseur;
import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    public static String affiche;

    public static String url;
    public static String sum;




    public boolean fileExistance(String fname){
        File file = getActivity().getFileStreamPath(fname);
        return file.exists();
    }



    public AgendaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_agenda, container, false);



        ButterKnife.bind(view);
        pager = (ViewPager)view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this.getActivity());
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        //doneJours = new ArrayList<>();

        adapter = new MyPagerAdapter(getChildFragmentManager(),null);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                pager.setCurrentItem(1);

            case Calendar.MONDAY:
                pager.setCurrentItem(2);

            case Calendar.TUESDAY:
                pager.setCurrentItem(3);

            case Calendar.WEDNESDAY:
                pager.setCurrentItem(4);


            case Calendar.THURSDAY:
                pager.setCurrentItem(5);


            case Calendar.FRIDAY:
                pager.setCurrentItem(0);

            case Calendar.SATURDAY:
                pager.setCurrentItem(0);
        }


        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(getActivity(), "Tab reselected: " + position, Toast.LENGTH_SHORT).show();

            }
        });
        changeColor(ContextCompat.getColor(getActivity(), R.color.backgroundColor));
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        toolbar.setTitle("Emploi du temps");
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(),android.R.color.white));
        return view;
    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        tabs.setTextColor(ContextCompat.getColor(getActivity(),android.R.color.white));
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            //getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            //getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        ArrayList<DoneJour> doneJours;
        private final String[] TITLES = {"Samedi", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi"};

        MyPagerAdapter(FragmentManager fm,ArrayList<DoneJour> doneJours) {
            super(fm);
            this.doneJours = doneJours;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SuperAwesomeCardFragment.newInstance(position,doneJours);
        }
    }

}
