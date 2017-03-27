package com.example.jewel.test_project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    //These two objects are used to format the Calendar.getDate() objects into readable strings
    public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE, MMM d, yyyy");
    public static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm aaa");
    //Tons of activities use these keys, so they reference this one
    public static String SCHEDULE_TYPE_KEY = "ScheduleType"; //User or Group
    public static String GROUP_ID_KEY = "GroupID"; //key to access correct Group
    public static String EVENT_ID_KEY = "EventID"; //key to access correct Event

    private Person user;
    private Map<String, Group> groups;

    private DataManager(){
        //TODO: Replace with database stuff to actually get this info
        user = new Person("Phone Owner", 0);
        groups = new HashMap<>();

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

    public Map<String, Group> getGroups(){
        return groups;
    }

    //TODO: Replace this with something for loading real schedules or something
    private void createDummySchedule(){
        Calendar start1 = Calendar.getInstance();
        Calendar end1 = Calendar.getInstance();
        Calendar start2 = Calendar.getInstance();
        Calendar end2 = Calendar.getInstance();

        start1.set(2017,3, 10, 0,0);
        end1.set(2017, 3, 17, 1, 0);

        start2.set(2017,3, 11, 8, 30);
        end2.set(2017, 3, 11, 9, 0);


        boolean[] weekdays = new boolean[]{true, true, true, true, true, true, true};

        ScheduleEvent recurring = new ScheduleEvent("Recurring Event", start1, end1, weekdays);
        recurring.setEventID(0);
        ScheduleEvent oneTime = new ScheduleEvent("One Time Event", start2, end2);
        recurring.setEventID(1);

        user.getSchedule().addEvent(recurring);
        user.getSchedule().addEvent(oneTime);

        //Creating a second Person
        Person friend = new Person("Friend", 1);
        Calendar startF = Calendar.getInstance();
        Calendar endF = Calendar.getInstance();
        startF.set(2017,3, 10, 1,0);
        endF.set(2017, 3, 17, 2, 0);

        ScheduleEvent oneTimeF = new ScheduleEvent("Friend's Event", startF, endF);

        friend.getSchedule().addEvent(oneTimeF);

        //Creating a Group
        Group g = new Group("Test Group", 1);
        groups.put("1", g);
        g.addMember(user);
        g.addMember(friend);
    }
}
