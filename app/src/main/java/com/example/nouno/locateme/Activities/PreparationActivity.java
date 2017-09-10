package com.example.nouno.locateme.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.locateme.ConnexionNet;
import com.example.nouno.locateme.Data.Filiere;
import com.example.nouno.locateme.Data.Info;
import com.example.nouno.locateme.Data.WebResponse;
import com.example.nouno.locateme.QueryUtils;
import com.example.nouno.locateme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PreparationActivity extends AppCompatActivity {
    Button retryButton;
    TextView errorText;
    ProgressBar progressBar;
    public static int annee;
    TextView mainText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String URL="https://ent.usthb.dz/index.php?/Emp/filieres";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation);
        retryButton = (Button) findViewById(R.id.button_retry);
        errorText = (TextView) findViewById(R.id.text_error);
        mainText = (TextView) findViewById(R.id.text_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final ConnexionNet connexionNet = new ConnexionNet(this);
        if (!connexionNet.isConnected())
        {
            mainText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connexionNet.isConnected())
                {
                    errorText.setVisibility(View.GONE);
                    retryButton.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainText.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    },250);
                    ConnexionTask connexionTask = new ConnexionTask();
                    connexionTask.execute(URL);
                }
            }
        });
        ConnexionTask connexionTask = new ConnexionTask();
        connexionTask.execute(URL);
    }


    ArrayList<Filiere> filieres;
    private class ConnexionTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... params) {
            WebResponse webResponse = QueryUtils.makeHttpGetRequest(params[0],
                    new LinkedHashMap<String, String>());
            if (webResponse.isError()) {
                //Toast.makeText(MainActivity.this,"Veuillez vous connecter d'abord !",Toast.LENGTH_LONG).show();

                return null;//erreur
            } else {
                //Toast.makeText(MainActivity.this,"Connexion établie !",Toast.LENGTH_LONG).show();
                return webResponse.getResponseString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s==null)
            {
                errorText.setText("Une erreur s'est produite veuillez réessayer");
                mainText.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        errorText.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);
                    }
                },250);

            }
            else
            {
                try {
                    StringBuilder sb = new StringBuilder(s);
                    sb.deleteCharAt(0);
                    String resultString = sb.toString();
                    filieres=Filiere.fromJson(resultString);
                    Intent i = new Intent(PreparationActivity.this,NewMainAct.class);
                    Info info = new Info(filieres,annee,null,null,"M",null);
                    String json = new Gson().toJson(info);
                    if (getIntent().getExtras()!=null)
                    if (getIntent().getExtras().containsKey("reinit"))
                    {
                        i.putExtra("reinit","true");
                    }
                    i.putExtra("INFO",json);
                    startActivity(i);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
