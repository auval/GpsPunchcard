package com.mta.gpspunchcard;

import android.Manifest;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mta.gpspunchcard.databinding.ActivityMainBinding;
import com.mta.gpspunchcard.model.GeoFencingHelper;
import com.mta.gpspunchcard.model.SimpleLocation;

/**
 * initial geo-fencing tracking service should start here, but
 * if should also start from boot listener, according to the tracking state
 */
public class MainActivity extends AppCompatActivity {

    final static int PERMISSIONS_RESPONSE_CODE = 222;
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    boolean tracking;
    PendingIntent mGeofencePendingIntent;
    GeoFencingHelper gfHelper = new GeoFencingHelper();
    private GeofencingClient mGeofencingClient = null;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // hide the button until we check it
        binding.setIsWorkDefined(true);

        setSupportActionBar(binding.toolbar);

        setObservers();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    private void setObservers() {
        GeoFencingHelper.getLiveWorkLocation().observe(this, new Observer<SimpleLocation>() {
            @Override
            public void onChanged(@Nullable SimpleLocation simpleLocation) {
                if (simpleLocation != null) {
                    binding.setIsWorkDefined(true);
                    binding.setWork(simpleLocation);
                }
            }
        });
    }

    private void asyncStateCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // check if work is defined
                initWorkButton();

                initTrackingState();
            }
        }).start();
    }

    /**
     * updates state and UI binding
     */
    private void initTrackingState() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        tracking = sharedPref.getBoolean("tracking", false);

        binding.setIsTracking(tracking);
    }

    @Override
    protected void onResume() {
        super.onResume();
        asyncStateCheck();

    }

    private void initWorkButton() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String wLat = sharedPref.getString("work-lat", null);
        String wLong = sharedPref.getString("work-long", null);
        String wAddr = sharedPref.getString("work-address", null);

        if (wLat == null) {
            // enable the add work button
            binding.setIsWorkDefined(false);
        } else {
//            binding.setIsWorkDefined(true);
            GeoFencingHelper.setWorkLocation(wAddr, Double.parseDouble(wLat), Double.parseDouble(wLong));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onAddWork(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddWork(View view) {

        startActivity(new Intent(this, SearchActivity.class));

    }

    public void onTrackingClicked(View view) {
       asyncSetTrackingState(!tracking);
    }

    private void asyncSetTrackingState(final boolean newState) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // todo: move logic to Logic, observe model view

                // here we should start and stop the tracking service
                LiveData<SimpleLocation> liveWorkLocation = GeoFencingHelper.getLiveWorkLocation();
                SimpleLocation workLocationValue = liveWorkLocation.getValue();
                if (workLocationValue != null) {
                    if (newState == true) {

                        startTracking(workLocationValue);

                    } else {

                        stopTracking(workLocationValue);
                    }
                }

                // toggle state
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("tracking", newState);

                editor.apply();

                // this will change the tracking flag, as well as other state related UI stuff
                initTrackingState();

            }
        }).start();
    }


    private void stopTracking(SimpleLocation workLocationValue) {
        gfHelper.deleteGeofence(workLocationValue);

        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...

                    }
                });
    }

    private void startTracking(final SimpleLocation workLocationValue) {
        gfHelper.addGeofence(workLocationValue);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_RESPONSE_CODE);

            return;
        }


        mGeofencingClient.addGeofences(gfHelper.getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: startTracking");
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: startTracking", e);
                        // Failed to add geofences
                        // on emulator I get:     com.google.android.gms.common.api.ApiException: 1000:
                        // meaning GEOFENCE_NOT_AVAILABLE

                        Snackbar.make(binding.button4, getString(R.string.problem), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        asyncSetTrackingState(false);

                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode=" + requestCode + " grantResults=" + grantResults);
        if (requestCode == PERMISSIONS_RESPONSE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // requestLocation();
            if (tracking) {
                LiveData<SimpleLocation> liveWorkLocation = GeoFencingHelper.getLiveWorkLocation();
                SimpleLocation workLocationValue = liveWorkLocation.getValue();
                if (workLocationValue != null) {
                    startTracking(workLocationValue);
                }
            }
        } else {
            // notify the user the app cannot work without this permission so we're closing the app.
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void openStats(View view) {
        startActivity(new Intent(this, StatsActivity.class));
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


}
