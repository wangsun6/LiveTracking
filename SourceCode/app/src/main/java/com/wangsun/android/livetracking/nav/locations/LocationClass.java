package com.wangsun.android.livetracking.nav.locations;


/**
 * Created by WANGSUN on 24-July-18.
 */

public class LocationClass {

    int id;
    double longitude,latitude;
    long time;

    public LocationClass(int id, double longitude, double latitude, long time) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getTime() {
        return time;
    }
}
