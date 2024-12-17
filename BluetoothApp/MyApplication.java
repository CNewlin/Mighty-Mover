package com.example.gpsapp;

import android.app.Application;
import android.location.Location;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
public class MyApplication extends Application {
    private static MyApplication singleton;
    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return this.myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public MyApplication getInstance() {
        return singleton;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        singleton = this;
        this.myLocations = new ArrayList();
    }
}