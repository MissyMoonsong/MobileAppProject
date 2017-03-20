package com.example.jewel.test_project;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jewel on 3/19/2017.
 * This class is meant to hold references to our groups, people, etc. so that they aren't
 * tied to any one activity.
 *
 * This is a Singleton.
 *
 * In the future, this class will probably have to interact with the database.
 * It stores all schedules in Key, Value pairs
 */

public class DataManager {
    private static DataManager instance;

    private Person user;
    private List<Person> contacts;
    private List<Group> groups;

    private static Map<String, Schedule> schedules = new HashMap<String, Schedule>();

    private DataManager(){
        //TODO: Replace with stuff to actually get this info
        user = new Person("Phone Owner");
        contacts = new ArrayList<>();
        groups = new ArrayList<>();

        createDummySchedule();
    }

    public static DataManager Instance(){
        if (instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    public Person getUser(){
        return user;
    }

    public List<Person> getContacts(){
        return contacts;
    }

    public List<Group> getGroups(){
        return groups;
    }


    //TODO: Replace this with something for loading real schdules or something
    private void createDummySchedule(){
        Calendar start = new GregorianCalendar(2017, 3, 10);
        Calendar end = new GregorianCalendar(2017, 3, 17);

        Time startTime1 = Time.valueOf("04:00:00");
        Time endTime1 = Time.valueOf("07:30:00");
        Time startTime2 = Time.valueOf("08:00:00");
        Time endTime2 = Time.valueOf("09:30:00");

        boolean[] weekdays = new boolean[]{false, true, true, false, false, false, false};

        ScheduleEvent recurring = new ScheduleEvent("Recurring Event", start, end,
                startTime1,  endTime1, weekdays);
        ScheduleEvent oneTime = new ScheduleEvent("One Time Event", start,
                startTime2,  endTime2);

        user.getSchedule().addEvent(recurring);
        user.getSchedule().addEvent(oneTime);
    }
}
