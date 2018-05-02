package com.mta.gpspunchcard.model;


import com.google.android.gms.maps.model.LatLng;

public class SimpleLocation {
    private final String id;
    private final String address;
    private final LatLng location;

    public SimpleLocation(String id, String address, double latitude, double longitude) {
        this.id = id;
        this.address = address;
        this.location = new LatLng(latitude, longitude);
    }

    public LatLng getLocation() {
        return location;
    }


    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayText() {
        return "Work: " + address;
    }

}
