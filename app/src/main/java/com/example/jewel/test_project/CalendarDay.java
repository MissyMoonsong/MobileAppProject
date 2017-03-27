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

    //NOTE: This puts each block into place using the comparisons in the block class
    public void addBlock(ScheduleBlock b){
        if(blocksInDay.size() == 0){
            blocksInDay.add(b);
        } else{
            int i = 0;
            while (i < blocksInDay.size() && b.compareTo(blocksInDay.get(i)) > 0){
                i++;
            }
            //This block comes later than everything in the list
            if(i == blocksInDay.size()){
                blocksInDay.add(b);
            }
            //This block goes into position i
            else{
                blocksInDay.add(i, b);
            }
        }
    }

    public List<ScheduleBlock> getBlocksInDay(){
        return  blocksInDay;
    }

    public Calendar getDate(){
        return date;
    }

    //Note: eventDuration might be better represented as a different data type
    public ScheduleEvent findTimeInDay(int duration){
        if(blocksInDay.size() == 0) {
            //TODO: TAKE THIS CODE TO THE MAIN VERSION, GENIUS
            //This entire day is free
            //Create two times, representing the start and end of this event
            Calendar start = (Calendar)date.clone();
            Calendar end = (Calendar)date.clone();

            end.add(Calendar.MINUTE, duration);

            //Assemble the start and end time into an Event
            return new ScheduleEvent("Generated Event", start, end);
        }

        //Create two times, representing the start and end of this event
        Calendar start = (Calendar)date.clone();
        Calendar end = (Calendar)date.clone();

        end.add(Calendar.MINUTE, duration);

        //Recall that the list of blocks is SORTED
        //Slide the window, looking for a free time slot
        boolean done = false;
        while(!done){
            //Validate window -- see if End rolled over to the next day
            if(end.get(Calendar.DAY_OF_MONTH) != date.get(Calendar.DAY_OF_MONTH)){
                return null;
            }

            boolean collision = false;
            for(ScheduleBlock b : blocksInDay){
                if(b.intersectsWith(start, end)){
                    collision = true;
                    break;
                }
            }

            if(collision){ //Advance the window by 15 min
                start.add(Calendar.MINUTE, 15);
                end.add(Calendar.MINUTE, 15);
            }
            else{ //This event window works
                done = true;
            }
        }

        //Assemble the start and end time into an Event
        return new ScheduleEvent("Generated Event", start, end);
    }

    @Override
    public String toString(){
        return "Day of : " + DataManager.DATE_FORMATTER.format(date.getTime());
    }
}
