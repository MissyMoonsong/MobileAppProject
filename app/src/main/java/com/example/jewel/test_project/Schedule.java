package com.example.jewel.test_project;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jewel on 3/16/2017.
 * This class represents a person or group's "Schedule"
 * A Schedule is made up of many Events, plus some other
 * attributes like a name, etc.
 */

public class Schedule {
    private List<ScheduleEvent> events;
    private String name;
    //Add more...?

    public Schedule(String name){
        this.name = name;
        events = new ArrayList<>();
    }

    public void addEvent(ScheduleEvent event){
        events.add(event);
    }

    public void clearEvents(){
        events.clear();
    }

    public void removeEvent(ScheduleEvent event){
        //TODO: Make sure this implementation actually works the way we need it to
        events.remove(event);
    }

    /***
     * Returnsa list of CalendarDays, with each day being populated with blocks
     * created from the events in this schedule at the time this method is called
     * @param windowStart
     * @param windowEnd
     * @return
     */
    private List<CalendarDay> fillDaysInWindow(Calendar windowStart, Calendar windowEnd){
        //Create a collection of calendar days, from the start to end, INCLUSIVE
        List<CalendarDay> daysInWindow = new ArrayList<>();

        Calendar temp = (Calendar)windowStart.clone();

        while (temp.before(windowEnd)){
            CalendarDay day = new CalendarDay(temp);
            //Fill the day with event blocks
            for(ScheduleEvent e : events){
                e.generateBlockOnDay(day);
            }
            //Add day to the collection
            daysInWindow.add(day);
            //Make another Calendar to represent the next day
            temp = (Calendar)temp.clone();
            //Increment day
            temp.add(Calendar.DAY_OF_MONTH, 1);
        }

        return daysInWindow;
    }

    //Note: eventDuration might be better represented as a different data type
    public ScheduleEvent findTimeInSchedule(Calendar windowStart, Calendar windowEnd, int eventDuration){
        List<CalendarDay> daysInWindow = fillDaysInWindow(windowStart, windowEnd);

        ScheduleEvent open = null;
        for (CalendarDay d : daysInWindow){
            //Look for an open slot
            open = d.findTimeInDay(eventDuration);

            if(open != null){
                //Suitable event was found
                //TODO: Consider collecting more possible slots?
                break;
            }
        }

        //Note: This value might be null if no such event was found
        return open;
    }

    //Note: Be careful not to make accidental changes! This is intended to be for drawing events.
    public List<ScheduleEvent> getAllEvents(){
        return events;
    }

    @Override
    public String toString(){
        String str = name + ": " + "\n";

        for(ScheduleEvent e : events){
            str += e.toString() + "\n";
        }

        return str;
    }
    //Add more methods...?
}
