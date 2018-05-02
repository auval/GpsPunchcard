package com.mta.gpspunchcard.storage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mta.gpspunchcard.model.GeofenceEvent;

import java.util.List;

/**
 * Created by amir on 4/16/18.
 */
@Dao
public interface GeofenceLogDao {
    @Query("select id, eventType, eventTime from GeofenceEvent;")
    LiveData<List<GeofenceEvent>> loadGeofenceLog();

    @Insert
    void insertToLog(GeofenceEvent logRow);
}
