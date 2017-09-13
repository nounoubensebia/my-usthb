package com.example.nouno.locateme.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.locateme.ConnexionNet;
import com.example.nouno.locateme.Data.DoneJour;
import com.example.nouno.locateme.Data.WebResponse;
import com.example.nouno.locateme.DataRepo;
import com.example.nouno.locateme.Fragments.AgendaFragment;
import com.example.nouno.locateme.QueryUtils;
import com.example.nouno.locateme.R;
import com.example.nouno.locateme.SharedPreference;
import com.example.nouno.locateme.Utils.Parseur;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WaitActivity extends AppCompatActivity {
    Button retryButton;
    TextView errorText;
    Button cancelButton;
    ProgressBar progressBar;

    View syncCompletedLayout;
    Button syncCompletedButton;
    public  String affiche;
    public  int wait = 0;

    public String sum;
    public static ArrayList<DoneJour> doneJours;

    public boolean fileExistance(String fname) {
        File file = getFileStreamPath(fname);
        return file.exists();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnexionNet connexionNet = new ConnexionNet(this);
        if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("fromWelcomeActivity")&&!connexionNet.isConnected())
        {

                Intent intent = new Intent(WaitActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


        }
        else
        {



        setContentView(R.layout.activity_wait);
        retryButton = (Button) findViewById(R.id.button_retry);
        errorText = (TextView) findViewById(R.id.text_error);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        syncCompletedLayout = findViewById(R.id.layout_sync_completed);
        syncCompletedButton = (Button) findViewById(R.id.button_download_finished);


        ConnexionTask connexionTask = new ConnexionTask();
        connexionTask.execute(new LinkedHashMap<String, String>());
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                ConnexionTask connexionTask = new ConnexionTask();
                connexionTask.execute(new LinkedHashMap<String, String>());
            }
        });

    }
    }

    private class ConnexionTask extends AsyncTask<Map<String, String>, Void, String> {


        protected String doInBackground(Map<String, String>... params) {
            DataRepo.getGraphInstance(WaitActivity.this);
            DataRepo.getStructureListInstance(WaitActivity.this);
            String urla = SharedPreference.loadString("URL", WaitActivity.this);
            WebResponse webResponse = QueryUtils.makeHttpGetRequest(SharedPreference.loadString("URL", WaitActivity.this),
                    new LinkedHashMap<String, String>());
            if (webResponse.isError()) {
                return null;//erreur
            } else
                return webResponse.getResponseString();
        }

        protected void onPostExecute(String s) {
            if (s != null) {
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
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(resultString.getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Parseur.parseXml(resultString);
                        SharedPreference.saveString("SUM", sum, WaitActivity.this);
                        //wait = 1;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    affiche = s;
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();

                    try {

                        doneJours = Parseur.getJours(resultString);
                        //Toast.makeText(EmploiDuTemps.this,EmploiDuTemps.sum,Toast.LENGTH_LONG).show();
                        sum = Parseur.getSum(resultString);
                        if (!sum.equals(SharedPreference.loadString("SUM", WaitActivity.this))) {
                            //Toast.makeText(getActivity(),"Synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                            SharedPreference.saveString("SUM", sum, WaitActivity.this);
                            //Remplissage du fichier
                            String filename = "timing";
                            FileOutputStream outputStream;
                            try {
                                outputStream = WaitActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
                                outputStream.write(resultString.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            //Toast.makeText(getActivity(),"Pas de synchronisation de l'emploi du temps",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    wait = 1;
                }
                if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("fromSettingsActivity"))
                {

                    progressBar.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            syncCompletedLayout.setVisibility(View.VISIBLE);
                            syncCompletedButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(WaitActivity.this, StartActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    },250);
                }
                else
                {
                    Intent intent = new Intent(WaitActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {

                progressBar.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorText.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);
                        if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("fromSettingsActivity"))
                        {
                            cancelButton.setVisibility(View.VISIBLE);
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        }
                    }
                },250);
                if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey("fromWelcomeActivity"))
                {
                    Intent intent = new Intent(WaitActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(WaitActivity.this,"Synchronisation échouée",Toast.LENGTH_LONG).show();
                }
            }


        }
    }
}
