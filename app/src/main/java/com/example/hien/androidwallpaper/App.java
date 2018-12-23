package com.example.hien.androidwallpaper;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Hien on 02/05/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getApplicationContext().getString(R.string.fontPath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
