package com.example.nouno.locateme;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;


import com.mapbox.mapboxsdk.Mapbox;

/**
 * Created by nouno on 10/09/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
