package com.example.nouno.locateme.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.nouno.locateme.R;

public class AuthorizationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    Button authorizeAccess;
    TextView authorizationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        authorizeAccess = (Button) findViewById(R.id.button_authorize);
        authorizationText = (TextView) findViewById(R.id.text_authorization);
        authorizeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AuthorizationActivity.this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        3);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==3)
        {
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Intent i = new Intent(this,PreparationActivity.class);
                startActivity(i);
                finish();
            }
        }
        else
        {
            authorizeAccess.setText("Réessayer");
            authorizationText.setText("L'application ne peut pas fonctionner si vous ne l'autoriser pas à accèder à votre position géographique veuillez réesayer");
        }


    }
}
