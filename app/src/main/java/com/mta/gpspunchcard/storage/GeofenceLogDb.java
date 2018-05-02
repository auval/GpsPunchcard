package com.mta.gpspunchcard.storage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mta.gpspunchcard.model.GeofenceEvent;

import java.util.List;

/**
 * Created by amir on 4/16/18.
 */

@Database(entities = {GeofenceEvent.class}, version = 1, exportSchema = false)
public abstract class GeofenceLogDb extends RoomDatabase {
    private static final String TAG = GeofenceLogDb.class.getSimpleName();
    private static GeofenceLogDb INSTANCE;

    public static GeofenceLogDb getInstance(Context context) {
        synchronized (GeofenceLogDb.class) {
            if (INSTANCE == null) {
                // notice getApplicationContext
                // -- it prevents the memory leak that would happen if the activity was passed
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        GeofenceLogDb.class, "gflog.db")
//                        .addMigrations(MIGRATION_1_2) // placeholder for future db versions
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract GeofenceLogDao getGeofenceLogDao();

    public LiveData<List<GeofenceEvent>> readGeofenceLog() {
        LiveData<List<GeofenceEvent>> GeofenceLogEntities = getGeofenceLogDao().loadGeofenceLog();
        return GeofenceLogEntities;
    }

    public void writeToGeofenceLog(final GeofenceEvent logEntity) {
        // there are better ways than creating a new thread and a new Runnable every time.
        // we will cover these ways later (-au)
        new Thread(new Runnable() {
            @Override
            public void run() {
                getGeofenceLogDao().insertToLog(logEntity);
            }
        }).start();
    }
}
