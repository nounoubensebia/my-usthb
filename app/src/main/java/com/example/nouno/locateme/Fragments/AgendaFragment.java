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
    public static int wait = 0 ;
    public static String url;
    public static String sum;
    public static ArrayList<DoneJour> doneJours;

    private class ConnexionTask extends AsyncTask<Map<String,String>,Void,String> {



        protected String doInBackground(Map<String, String>... params) {
            WebResponse webResponse = QueryUtils.makeHttpGetRequest(SharedPreference.loadString("URL",getActivity()),
                    new LinkedHashMap<String, String>());
            if (webResponse.isError()) {
                return null;//erreur
            } else
                return webResponse.getResponseString();
        }

        protected void onPostExecute(String s) {

            if (!fileExistance("timing")) {
                try {
                    affiche = s;
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();
                    //Création du fichier contenant l'emploi du temps
                    String filename = "timing";
                    FileOutputStream outputStream;

                    try {
                        outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(resultString.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Parseur.parseXml(resultString);
                    SharedPreference.saveString("SUM",sum, getActivity());
                    wait = 1;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                affiche = s;
                StringBuilder sb = new StringBuilder(s);
                sb.deleteCharAt(0);
                String resultString = sb.toString();

                try {

                    doneJours = Parseur.getJours(resultString);
                    //Toast.makeText(EmploiDuTemps.this,EmploiDuTemps.sum,Toast.LENGTH_LONG).show();

                    if(!sum.equals(SharedPreference.loadString("SUM",getActivity())))
                    {
                        Toast.makeText(getActivity(),"Synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                        SharedPreference.saveString("SUM",sum,getActivity());
                        //Remplissage du fichier
                        String filename = "timing";
                        FileOutputStream outputStream;
                        try {
                            outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(resultString.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {

                        //Toast.makeText(getActivity(),"Pas de synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                wait = 1;
            }
        }
    }

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

        if(!fileExistance("timing"))
        {
            //String json = getActivity().getIntent().getExtras().getString("INFO");
            //URL info = new Gson().fromJson(json,URL.class);
            //url=info.toString();
            url = SharedPreference.loadString("INFO",getActivity());
            SharedPreference.saveString("URL",url,getActivity());
            ConnexionTask connexionTask = new ConnexionTask();
            connexionTask.execute(new LinkedHashMap<String, String>());
        }
        else
        {
            //le fichier existe, on teste s'il y a la connexion
            ConnexionNet cn=new ConnexionNet(getActivity());
            if(cn.isConnected())
            {
                //Toast.makeText(getActivity(),"Connexion établie", Toast.LENGTH_SHORT).show();
                //effectuer la synchronisation dans le cas où l'emploi du temps est modifié
                ConnexionTask connexionTask = new ConnexionTask();
                connexionTask.execute(new LinkedHashMap<String, String>());
            }
            else
            {
                Toast.makeText(getActivity(),"Connexion non établie", Toast.LENGTH_SHORT).show();
                FileInputStream fis = null;
                try {
                    fis =getActivity().openFileInput("timing");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(EmploiDuTemps.this,"Lecture à partir du fichier", Toast.LENGTH_SHORT).show();
                String resultString=sb.toString();
                try {

                    Parseur.parseXml(resultString);
                    SharedPreference.saveString("SUM",sum,getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wait=1;
            }

        }
        ButterKnife.bind(view);
        pager = (ViewPager)view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this.getActivity());
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        //doneJours = new ArrayList<>();
        if (doneJours == null)
            doneJours = new ArrayList<>();
        adapter = new MyPagerAdapter(getChildFragmentManager(),doneJours);
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
