package com.example.nouno.locateme;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nouno on 06/09/2017.
 */

public class ConnexionNet extends Activity {

    Context context;
    public ConnexionNet(Context context)
    {
        this.context=context;
    }

    public boolean isConnected()
    {
        ConnectivityManager connectivity= (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity!=null)
        {
            NetworkInfo info=connectivity.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState()== NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean haveInternetConnection() {
        // Fonction haveInternetConnection : return true si connecté, return false dans le cas contraire
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (network==null || !network.isConnected())
        {
            // Le périphérique n'est pas connecté à Internet
            return false;
        }
//        Le périphérique est connecté à Internet
        return true;
    }
}
