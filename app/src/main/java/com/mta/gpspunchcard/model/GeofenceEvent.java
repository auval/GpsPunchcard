package com.mta.gpspunchcard.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mta.gpspunchcard.gf.Misc;

@Entity
public class GeofenceEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 1 = int
     * -1 = out
     */
    private int eventType;
    private long eventTime;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getInTimeString() {
        if (eventType > 0) {
            // in
            return Misc.formatTime(eventTime);
        }
        return "";
    }

    public String getOutTimeString() {
        if (eventType < 0) {
            // out
            return Misc.formatTime(eventTime);
        }
        return "";
    }

    public int getId() {
        return id;
    }
}
