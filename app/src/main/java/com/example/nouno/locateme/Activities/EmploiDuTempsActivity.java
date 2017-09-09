package com.example.nouno.locateme.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nouno.locateme.ConnexionNet;
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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmploiDuTempsActivity extends AppCompatActivity {

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


    private class ConnexionTask extends AsyncTask<Map<String,String>,Void,String> {



        protected String doInBackground(Map<String, String>... params) {
            WebResponse webResponse = QueryUtils.makeHttpGetRequest(SharedPreference.loadString("URL",EmploiDuTempsActivity.this),
                    new LinkedHashMap<String, String>());
            if (webResponse.isError()) {
                return null;//erreur
            } else
                return webResponse.getResponseString();
        }

        protected void onPostExecute(String s) {

            /*if (!fileExistance("timing")) {
                try {
                    affiche = s;
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();
                    //Création du fichier contenant l'emploi du temps
                    String filename = "timing";
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(resultString.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Parseur.parseXml(resultString);
                    SharedPreference.saveString("SUM", EmploiDuTempsActivity.sum, EmploiDuTempsActivity.this);
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
             /*       if(!EmploiDuTemps.sum.equals(SharedPreference.loadString("SUM",EmploiDuTemps.this)))
                    {
                        Toast.makeText(EmploiDuTemps.this,"Synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(EmploiDuTemps.this,"Pas de Synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                    } *
                try {
                    Parseur.parseXml(resultString);
                    //Toast.makeText(EmploiDuTemps.this,EmploiDuTemps.sum,Toast.LENGTH_LONG).show();

                    if(!EmploiDuTempsActivity.sum.equals(SharedPreference.loadString("SUM",EmploiDuTempsActivity.this)))
                    {
                        Toast.makeText(EmploiDuTempsActivity.this,"Synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                        SharedPreference.saveString("SUM",EmploiDuTempsActivity.sum,EmploiDuTempsActivity.this);
                        //Remplissage du fichier
                        String filename = "timing";
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(resultString.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(EmploiDuTempsActivity.this,"Pas de synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                wait = 1;
            }*/
        }
    }

    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emploi_du_temps);
        if(!fileExistance("timing"))
        {
            String json = getIntent().getExtras().getString("INFO");
            URL info = new Gson().fromJson(json,URL.class);
            url=info.toString();
            SharedPreference.saveString("URL",url,EmploiDuTempsActivity.this);
            ConnexionTask connexionTask = new ConnexionTask();
            connexionTask.execute(new LinkedHashMap<String, String>());
        }
        else
        {
            //le fichier existe, on teste s'il y a la connexion
            ConnexionNet cn=new ConnexionNet(EmploiDuTempsActivity.this);
            if(cn.isConnected())
            {
                Toast.makeText(EmploiDuTempsActivity.this,"Connexion établie", Toast.LENGTH_SHORT).show();
                //effectuer la synchronisation dans le cas où l'emploi du temps est modifié
                ConnexionTask connexionTask = new ConnexionTask();
                connexionTask.execute(new LinkedHashMap<String, String>());
            }
            else
            {
                Toast.makeText(EmploiDuTempsActivity.this,"Connexion non établie", Toast.LENGTH_SHORT).show();
                FileInputStream fis = null;
                try {
                    fis =EmploiDuTempsActivity.this.openFileInput("timing");
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
                    //SharedPreference.saveString("SUM",EmploiDuTempsActivity.sum,EmploiDuTempsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wait=1;
            }

        }
        ButterKnife.bind(this);
        pager = (ViewPager)findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
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
        changeColor(ContextCompat.getColor(getBaseContext(), R.color.backgroundColor));

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(EmploiDuTempsActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                Toast.makeText(EmploiDuTempsActivity.this,"COUCOU",Toast.LENGTH_LONG).show();

        }
        Intent i = new Intent(EmploiDuTempsActivity.this, Menu.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);

    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }
/*
    public void onColorClicked(View v) {
        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }
    */

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Samedi", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
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
            return SuperAwesomeCardFragment.newInstance(position,null);
        }
    }
}
