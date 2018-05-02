package com.mta.gpspunchcard.presenter;

import com.mta.gpspunchcard.model.GeofenceEvent;

import java.util.List;

/**
 * testable pure Java logic
 */
public class Logic {

    /**
     * The calculation should take into account bad data:
     * - enter without exit on the same day
     * - consecutive events of the same type: which one to use?
     *
     * above is product decisions, I won't get into that here...
     * I currently just ignore bad input.
     *
     * @param newLog
     * @return
     */
    public long calculateTotalTime(List<GeofenceEvent> newLog) {
        long total = 0;
        long currentIntervalStart = 0;
        for (GeofenceEvent ge: newLog) {
            if (currentIntervalStart ==0 && ge.getEventType() == 1) {
                // new entrance
                currentIntervalStart = ge.getEventTime();
            } else if (currentIntervalStart > 0 && ge.getEventType() == -1){
                // new exit
                total +=  (ge.getEventTime() - currentIntervalStart);
                currentIntervalStart=0;
            }
        }
        return total;
    }

}
