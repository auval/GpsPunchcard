package com.mta.gpspunchcard.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.mta.gpspunchcard.storage.GeofenceLogDb;

import java.util.ArrayList;
import java.util.List;

/**
 * ref: https://developer.android.com/training/location/geofencing#java
 *
 */
public class GeoFencingHelper {

    // we may want to customize this, so it can be moved to SharedPreferences
    private static final float GEOFENCE_RADIUS_IN_METERS = 50f;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000 * 60 * 5; // 5 min
    private static MutableLiveData<SimpleLocation> workLocation = new MutableLiveData<>();
    private List<Geofence> mGeofenceList = new ArrayList<>();

    public static LiveData<SimpleLocation> getLiveWorkLocation() {
        return workLocation;
    }

    public static void setWorkLocation(String address, double lat, double lon) {
        workLocation.postValue(new SimpleLocation("work", address ,lat, lon));
    }

    private static final String TAG = GeoFencingHelper.class.getSimpleName();
    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofence(SimpleLocation entry) {
        Log.i(TAG, "addGeofence: "+entry.getId());
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(entry.getId())

                .setCircularRegion(
                        entry.getLocation().latitude,
                        entry.getLocation().longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    public void deleteGeofence(SimpleLocation workLocationValue) {
        // currently we're geofencing only one location, so clear will do for now...
        mGeofenceList.clear();
    }

    /**
     * @param type -1 is exit, 1 = enter
     */
    public static void insertEvent(Context c, int type) {
        GeofenceEvent logEntity = new GeofenceEvent();
        logEntity.setEventType(type);
        logEntity.setEventTime(System.currentTimeMillis());

        GeofenceLogDb.getInstance(c).writeToGeofenceLog(logEntity);
    }
}
