package com.example.jewel.test_project;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

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
}

