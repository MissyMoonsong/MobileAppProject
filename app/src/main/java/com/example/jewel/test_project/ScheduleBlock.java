package com.example.jewel.test_project;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Jewel on 2/21/2017.
 * This class is a time block that exists on only one day.
 * Used for making calculations and drawing the schedule.
 */

public class ScheduleBlock implements Comparable<ScheduleBlock>{
    private ScheduleEvent parentEvent; //The event used to generate this block
    private Time startTime, endTime;
    private String blockDisplayName;

    public ScheduleBlock(ScheduleEvent parent, String displayName,
                         Time start, Time end){
        this.parentEvent = parent;
        this.blockDisplayName = displayName;
        this.startTime = start;
        this.endTime = end;
    }

    /***
     * Only looks at the Hours/Minutes of the supplied Calendars
     * @param windowStart
     * @param windowEnd
     * @return
     */
    public boolean intersectsWith(Calendar windowStart, Calendar windowEnd){
        boolean eventCompletelyBefore = windowStart.getTime().before(startTime) &&
                windowEnd.getTime().before((startTime));
        boolean eventCompletelyAfter = windowStart.getTime().after(endTime) &&
                windowEnd.getTime().after((endTime));

        return !(eventCompletelyBefore || eventCompletelyAfter);
    }

    public int compareTo(ScheduleBlock other) {
        //This one comes after the other
        if(this.startTime.after(other.startTime)){
            return 1;
        }
        //Start times equal
        else if (!this.startTime.after(other.startTime) && !this.startTime.before(other.startTime)){
            if(this.endTime.before(other.endTime)){
                return -1;
            }
            else{
                return 1;
            }
        }
        else{ //This one comes before the other
            return -1;
        }
    }

}
