package com.mta.gpspunchcard.gf;

import com.google.android.gms.location.GeofenceStatusCodes;

import java.text.SimpleDateFormat;

public class Misc {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    /**
     * missing code in google's tutorial
     * http://book2s.com/java/src/package/com/google/android/gms/location/sample/geofencing/geofenceerrormessages.html#4e24568c13775b39a95ce0f5cb32cb92
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return ("geofence_not_available");
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return ("geofence_too_many_geofences");
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return ("geofence_too_many_pending_intents");
            default:
                return ("unknown_geofence_error");
        }
    }

    public static String formatTime(long timeInLillis) {
        return formatter.format(timeInLillis);
    }
}
