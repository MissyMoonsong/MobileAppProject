package com.example.jewel.test_project;

import java.sql.Time;

/**
 * Created by Jewel on 2/21/2017.
 * This class is a time block that exists on only one day.
 * Used for making calculations and drawing the schedule.
 */

public class ScheduleBlock {
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
}
