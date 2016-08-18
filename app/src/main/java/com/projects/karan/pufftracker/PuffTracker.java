package com.projects.karan.pufftracker;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by ADMIN on 8/16/2016.
 */
public class PuffTracker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
