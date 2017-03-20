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
    private Calendar startTime, endTime;
    private String blockDisplayName;

    /***
     * Note that only the TIME of the calendars provided will be used. The dates
     * and other information will be ignored.
     * @param parent
     * @param displayName
     * @param start
     * @param end
     */
    public ScheduleBlock(ScheduleEvent parent, String displayName,
                         Calendar start, Calendar end){
        this.parentEvent = parent;
        this.blockDisplayName = displayName;
        this.startTime = start;
        this.endTime = end;
    }

    public ScheduleEvent getParentEvent(){
        return parentEvent;
    }

    public String getName(){
        return blockDisplayName;
    }

    public Calendar getStartTime(){
        return startTime;
    }

    public Calendar getEndTime(){
        return endTime;
    }

    public boolean intersectsWith(Calendar eventStart, Calendar eventEnd){
        //Cloning so that all other information can be held constant
        Calendar tempStart = (Calendar)eventStart.clone();
        tempStart.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
        tempStart.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));

        Calendar tempEnd = (Calendar)eventEnd.clone();
        tempEnd.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
        tempEnd.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));

        boolean completelyBefore = eventStart.before(tempStart) && eventEnd.before(tempStart);
        boolean completelyAfter = eventStart.after(tempEnd) && eventEnd.after(tempEnd);

        return !(completelyBefore || completelyAfter);
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
