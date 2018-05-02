package com.mta.gpspunchcard;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mta.gpspunchcard.gf.Misc;
import com.mta.gpspunchcard.model.GeoFencingHelper;

import java.util.Arrays;
import java.util.List;

/**
 * code from https://developer.android.com/training/location/geofencing#java
 */
public class GeofenceTransitionsIntentService extends IntentService {


    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    // AndroidManifest complained this was missing..
    public GeofenceTransitionsIntentService() {
        this("stam");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
        Log.i(TAG, "GeofenceTransitionsIntentService created: " + name);
    }


    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = Misc.getErrorString(
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            Log.i(TAG, "onHandleIntent: got geofence event: " + Arrays.deepToString(triggeringGeofences.toArray()));


            // note: since the requirement is for one geo fence, I don't check which is it
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                GeoFencingHelper.insertEvent(this, 1);
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                GeoFencingHelper.insertEvent(this, -1);
            }


        } else {

            Log.e(TAG, "geofence_transition_invalid_type " +
                    geofenceTransition);
        }
    }
}