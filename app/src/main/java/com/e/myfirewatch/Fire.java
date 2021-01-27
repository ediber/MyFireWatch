package com.e.myfirewatch;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Fire {
    private String reporter;
    private int severity;
    private LatLng latLng;

    public Fire(String reporter, int severity, LatLng latLng) {
        this.reporter = reporter;
        this.severity = severity;
        this.latLng = latLng;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
