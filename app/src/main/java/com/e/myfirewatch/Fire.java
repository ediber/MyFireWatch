package com.e.myfirewatch;

import com.google.android.gms.maps.model.LatLng;

public class Fire {
    private String id;
    private String reporter;
    private int severity;
    private LatLng latLng;
    private boolean active;

    public Fire(String reporter, int severity, LatLng latLng, String id, boolean active) {
        this.reporter = reporter;
        this.severity = severity;
        this.latLng = latLng;
        this.id = id;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getReporter() {
        return reporter;
    }

    public int getSeverity() {
        return severity;
    }

    public LatLng getLatLng() {
        return latLng;
    }


    public boolean isActive() {
        return active;
    }

}
