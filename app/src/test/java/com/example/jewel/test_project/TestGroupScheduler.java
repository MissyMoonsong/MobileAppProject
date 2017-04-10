package com.example.jewel.test_project;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.Calendar;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static org.junit.Assert.*;

public class TestGroupScheduler {
    @Test
    public void DataManager_buildScheduleEventFromEvent_recurring() throws Exception {

        //Create Event Object
        DatabaseEvent event = new DatabaseEvent();

        event.setEventName("Class");
        event.setStartHour(3);
        event.setStartMin(0);
        event.setEndHour(3);
        event.setEndMin(55);
        event.setStartMonth(1);
        event.setStartDay(1);
        event.setStartYear(2017);
        event.setEndMonth(4);
        event.setEndDay(31);
        event.setEndYear(2017);
        event.setRSunday(false);
        event.setRMonday(false);
        event.setRTuesday(true);
        event.setRWednesday(false);
        event.setRThursday(true);
        event.setRFriday(false);
        event.setRSaturday(false);

        ScheduleEvent scheduleEvent = DataManager.buildScheduleEventFromEvent(event);
        assertEquals("Class", scheduleEvent.getEventName());
        assertEquals("Time: 03:00 AM to 03:55 AM", scheduleEvent.getEventTime());
        assertEquals("On days: TR", scheduleEvent.getEventRecurrence());
    }

    @Test
    public void DataManager_buildScheduleEventFromEvent_oneTime() throws Exception {

        //Create Event Object
        DatabaseEvent event = new DatabaseEvent();

        event.setEventName("Dinner");
        event.setStartHour(3);
        event.setStartMin(0);
        event.setEndHour(3);
        event.setEndMin(55);
        event.setStartMonth(1);
        event.setStartDay(1);
        event.setStartYear(2017);
        event.setEndMonth(4);
        event.setEndDay(31);
        event.setEndYear(2017);
        event.setRSunday(false);
        event.setRMonday(false);
        event.setRTuesday(false);
        event.setRWednesday(false);
        event.setRThursday(false);
        event.setRFriday(false);
        event.setRSaturday(false);

        ScheduleEvent scheduleEvent = DataManager.buildScheduleEventFromEvent(event);
        assertEquals("Dinner", scheduleEvent.getEventName());
        assertEquals("Time: 03:00 AM to 03:55 AM", scheduleEvent.getEventTime());
        assertEquals("One Time Event", scheduleEvent.getEventRecurrence());
    }

    @Test
    public void ScheduleEvent_createBlockInDay() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        assertEquals(1,today.getBlocksInDay().size());
        assertEquals(scheduleEvent, b.getParentEvent());
    }

    @Test
    public void ScheduleBlock_intersectsWith_startsBefore() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts before the block and ends in the middle of it
        start.set(Calendar.HOUR_OF_DAY, 2);
        end.set(Calendar.HOUR_OF_DAY, 3);
        end.set(Calendar.MINUTE, 30);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(true,intersection);
    }

    @Test
    public void ScheduleBlock_intersectsWith_endsAfter() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts inside the block and ends after it
        start.set(Calendar.HOUR_OF_DAY, 3);
        start.set(Calendar.MINUTE, 30);
        end.set(Calendar.HOUR_OF_DAY, 5);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(true,intersection);
    }

    @Test
    public void ScheduleBlock_intersectsWith_withinBlock() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts inside the block and ends inside it
        start.set(Calendar.HOUR_OF_DAY, 3);
        start.set(Calendar.MINUTE, 5);
        end.set(Calendar.HOUR_OF_DAY, 3);
        end.set(Calendar.MINUTE, 30);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(true,intersection);
    }

    @Test
    public void ScheduleBlock_intersectsWith_containsBlock() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts before the block and ends after it
        start.set(Calendar.HOUR_OF_DAY, 2);
        start.set(Calendar.MINUTE, 5);
        end.set(Calendar.HOUR_OF_DAY, 5);
        end.set(Calendar.MINUTE, 30);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(true,intersection);
    }

    @Test
    public void ScheduleBlock_intersectsWith_beforeBlock() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts before the block and ends before it
        start.set(Calendar.HOUR_OF_DAY, 2);
        start.set(Calendar.MINUTE, 5);
        end.set(Calendar.HOUR_OF_DAY, 2);
        end.set(Calendar.MINUTE, 30);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(false ,intersection);
    }

    @Test
    public void ScheduleBlock_intersectsWith_afterBlock() throws Exception {
        Calendar day = Calendar.getInstance();

        Calendar eventStart = Calendar.getInstance();
        eventStart.set(Calendar.MONTH, 0);
        eventStart.set(Calendar.DAY_OF_MONTH, 1);
        eventStart.set(Calendar.HOUR_OF_DAY, 3);
        eventStart.set(Calendar.MINUTE, 0);

        Calendar eventEnd = Calendar.getInstance();
        eventEnd.set(Calendar.MONTH, 0);
        eventEnd.set(Calendar.DAY_OF_MONTH, 1);
        eventEnd.set(Calendar.HOUR_OF_DAY, 3);
        eventEnd.set(Calendar.MINUTE, 55);

        boolean[] weekdays = new boolean[] {true, true, true, true, true, true, true};

        ScheduleEvent scheduleEvent = new ScheduleEvent("Intersections", eventStart, eventEnd, weekdays);

        CalendarDay today = new CalendarDay(day);

        scheduleEvent.generateBlockOnDay(today);

        ScheduleBlock b = today.getBlocksInDay().get(0);

        //Note that the above event takes place at 3 am to 3:55 am each day
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //This window starts after the block and ends after it
        start.set(Calendar.HOUR_OF_DAY, 4);
        start.set(Calendar.MINUTE, 0);
        end.set(Calendar.HOUR_OF_DAY, 5);
        end.set(Calendar.MINUTE, 30);

        boolean intersection =  b.intersectsWith(start, end);
        assertEquals(false,intersection);
    }
}

