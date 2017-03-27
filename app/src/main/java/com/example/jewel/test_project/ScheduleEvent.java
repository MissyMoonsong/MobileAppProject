package com.example.jewel.test_project;

import java.util.Calendar;

/**
 * Created by Jewel on 2/21/2017.
 * This class represents a single event in a schedule
 */

public class ScheduleEvent {
    private Calendar start, end;
    private boolean isRecurring;
    private boolean[] activeWeekdays = new boolean[7]; //Note: SUNDAY is the first day
    private String eventName;
    private String eventID; //TODO: get this from database

    /***
     * This is the constructor used for a one-time event.
     * A single one-time event is constrained to taking place within a single day.
     * Note that the dates of start and end should match, while the times should reflect
     * the start and end time of the event
     */
    public ScheduleEvent(String eventName, Calendar start, Calendar end) {
        this.eventName = eventName;

        this.start = start;
        this.end = end;
        isRecurring = false;
    }

    /***
     * This is the constructor used for a recurring event.
     * The WINDOW of the event is indicated by the DATES of start and end
     * The TIME of EACH INSTANCE of the event is indicated by the TIME of start and end
     *
     * That is, the event does not necessarily have a block start at Start-Time on Start-Date
     *
     * The weekdays[] array should have a length of 7, each entry indicating whether the event
     * applies to the i-th day of the week(with SUNDAY as the first day)
     */
    public ScheduleEvent(String eventName, Calendar start, Calendar end, boolean[] weekdays) {
        this.eventName = eventName;

        this.start = start;
        this.end = end;
        isRecurring = true;
        activeWeekdays = weekdays;
    }

    public boolean isDayInWindow(CalendarDay targetDay) {
        Calendar date = targetDay.getDate();

        boolean dayBeforeStart = date.before(start);
        boolean dayAfterEnd = date.after(end);

        //Appears outside the window
        if (!(dayBeforeStart || dayAfterEnd)) {
            //Check for same-day as one of the windows (edge case)
            return isSameDay(start, date) || isSameDay(end, date);
        } else {
            return true;
        }
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /***
     * This will create a schedule block and add it to the given day
     * if and only if the day falls within the active window of the event
     * AND for recurring events, is on an active weekday
     */
    public void generateBlockOnDay(CalendarDay targetDay) {
        //Day is outside the window of this event
        if (isDayInWindow(targetDay)) {
            boolean isValidDay = !isRecurring;

            if (isRecurring) {
                //TODO: Make sure the numbering system here matches...
                int day = targetDay.getDate().get(Calendar.DAY_OF_WEEK);
                isValidDay = activeWeekdays[day - 1];
            }

            if (isValidDay) {
                ScheduleBlock block = new ScheduleBlock(this, this.eventName,
                        start, end);

                targetDay.addBlock(block);
            }
        }
    }

    public void changeName(String newName) {
        this.eventName = newName;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String id) {
        eventID = id;
    }

    private String weekString() {
        String result = "";

        if (activeWeekdays[0]) {
            result += "U";
        }
        if (activeWeekdays[1]) {
            result += "M";
        }
        if (activeWeekdays[2]) {
            result += "T";
        }
        if (activeWeekdays[3]) {
            result += "W";
        }
        if (activeWeekdays[4]) {
            result += "R";
        }
        if (activeWeekdays[5]) {
            result += "F";
        }
        if (activeWeekdays[6]) {
            result += "S";
        }
        return result;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventRecurrence() {
        if (isRecurring) {
            return "On days: " + weekString();
        } else {
            return "One Time Event";
        }
    }

    public String getEventWindow() {
        String formattedStartDate = DataManager.DATE_FORMATTER.format(start.getTime());
        String formattedEndDate = DataManager.DATE_FORMATTER.format(end.getTime());

        if (isRecurring) {
            return "From: " + formattedStartDate + " to " + formattedEndDate + "\n";
        } else {
            return "On date: " + formattedStartDate;
        }
    }

    public String getEventTime() {
        String formattedStartTime = DataManager.TIME_FORMATTER.format(start.getTime());
        String formattedEndTime = DataManager.TIME_FORMATTER.format(end.getTime());

        return "Time: " + formattedStartTime + " to " + formattedEndTime;
    }

    @Override
    public String toString() {
        return getEventName();
    }

    public static boolean anyDaySelected(boolean[] weekdays){
        for(boolean b : weekdays){
            if (b){
                return true;
            }
        }
        return false;
    }
}
