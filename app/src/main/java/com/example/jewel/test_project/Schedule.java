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

    private void fillCalendar(){
        //TODO: This method creates CalendarDay(s) as needed to perform calculations
    }

    //Note: eventDuration might be better represented as a different data type
    public ScheduleEvent findTimeInSchedule(Calendar windowStart, Calendar windowEnd, int eventDuration){
        //TODO: Calculate free time in THIS schedule from the given input
        return null;
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
