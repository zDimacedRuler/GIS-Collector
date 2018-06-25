package com.disarm.surakshit.collectgis;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

/**
 * Created by AmanKumar on 6/22/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoiemRpbWFjZWRydWxlciIsImEiOiJjamlvaXE1M2wwcWI0M3FwOGI0czRlcGw4In0.8RyffsgJ0Bg-pbZdF7T_WA");
    }
}
