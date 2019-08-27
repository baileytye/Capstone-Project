package com.bowtye.decisive.ui;

import android.app.Application;

import com.bowtye.decisive.BuildConfig;

import timber.log.Timber;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
