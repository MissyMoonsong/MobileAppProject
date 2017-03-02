package com.example.jewel.test_project;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jewel on 2/21/2017.
 * This class represents one single day on the calendar.
 * By adding schedule blocks to it, the day can help calculate when a time slot is open
 * by narrowing the comparisons to just blocks on the same day.
 * Calendar days only need to be generated when either drawing the day or searching for
 * open time on that day.
 */

public class CalendarDay {
    private Calendar date;
    private List<ScheduleBlock> blocksInDay;

    public CalendarDay(Calendar date){
        blocksInDay = new ArrayList<>();
        this.date = date;
    }

    //NOTE: This does not attempt to sort the blocks into any order
    public void addBlock(ScheduleBlock b){
        blocksInDay.add(b);
    }

    public void clearBlocks(){
        blocksInDay.clear();
    }

    public Calendar getDate(){
        return date;
    }

    public ScheduleEvent findEventOnday(int duration){
        /*TODO: This method should find an open block of time on the current day
        *and create an event from that block.
        * If no such time slot is available, find a way to report failure.
        * (possibly just return null for failure)
        */
        return null;
    }
}
