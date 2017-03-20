package com.example.jewel.test_project;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Jewel on 2/21/2017.
 * This class represents a single event in a schedule
 */

public class ScheduleEvent {
    private Calendar windowStartDate, windowEndDate;
    private Time dailyStartTime, dailyEndTime;
    private boolean isRecurring;
    private boolean[] activeWeekdays = new boolean[7]; //Note: SUNDAY is the first day
    private String eventName;

    /***
     * This is the constructor used for a one-time event.
     * A single one-time event is constrained to taking place within a single day.
     */
    public  ScheduleEvent(String eventName, Calendar date,
                          Time startTime, Time endTime){
        this.eventName = eventName;

        windowStartDate = date;
        windowEndDate = date;
        dailyStartTime = startTime;
        dailyEndTime = endTime;
        isRecurring = false;
    }

    /***
     * This is the constructor used for a recurring event.
     * The weekdays[] array should have a length of 7, each entry indicating whether the event
     * applies to the i-th day of the week(with SUNDAY as the first day)
     */
    public  ScheduleEvent(String eventName, Calendar windowStart, Calendar windowEnd,
                          Time startTime, Time endTime, boolean[] weekdays){
        this.eventName = eventName;

        windowStartDate = windowStart;
        windowEndDate = windowEnd;
        dailyStartTime = startTime;
        dailyEndTime = endTime;
        isRecurring = true;
        activeWeekdays = weekdays;
    }

    public boolean isDayInWindow(CalendarDay targetDay){
        return !(targetDay.getDate().before(windowStartDate)
                || targetDay.getDate().after((windowEndDate)));
    }

    /***
     * This will create a schedule block and add it to the given day
     * if and only if the day falls within the active window of the event
     * AND for recurring events, is on an active weekday
     */
    public void generateBlockOnDay(CalendarDay targetDay){
        //Day is outside the window of this event
        if(isDayInWindow(targetDay)){
            boolean isValidDay = !isRecurring;

            if(isRecurring){
                //TODO: Make sure the numbering system here matches...
                int day = targetDay.getDate().DAY_OF_WEEK;
                isValidDay = activeWeekdays[day];
            }

            if(isValidDay) {
                ScheduleBlock block = new ScheduleBlock(this, this.eventName,
                        dailyStartTime, dailyEndTime);
                targetDay.addBlock(block);
            }
        }
    }

    public void changeName(String newName){
        this.eventName = newName;
    }

    @Override
    public String toString(){
        return eventName + " from " + dailyStartTime.toString() + " to " + dailyEndTime.toString();
    }
}
