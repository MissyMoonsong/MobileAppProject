package com.example.jewel.test_project;

import android.provider.ContactsContract;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    //Temporary variables
    private static int nextEventID = 1, nextGroupID = 1, nextUserID = 0;

    private Person user;
    private Map<String, Group> groups;

    private DataManager(){
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
        user = new Person("Phone Owner", getNextUserID());
        groups = new HashMap<>();

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
        recurring.setEventID(getNextEventID());
        ScheduleEvent oneTime = new ScheduleEvent("One Time Event", start2, end2);
        oneTime.setEventID(getNextEventID());

        user.getSchedule().addEvent(recurring);
        user.getSchedule().addEvent(oneTime);

        //Creating a second Person
        Person friend = new Person("Friend", getNextUserID());
        Calendar startF = Calendar.getInstance();
        Calendar endF = Calendar.getInstance();
        startF.set(2017,3, 10, 1,0);
        endF.set(2017, 3, 17, 2, 0);

        ScheduleEvent oneTimeF = new ScheduleEvent("Friend's Event", startF, endF);
        oneTimeF.setEventID(getNextEventID());

        friend.getSchedule().addEvent(oneTimeF);

        //Creating a Group
        Group g = new Group("Test Group", getNextGroupID());
        groups.put(g.getGroupID(), g);
        g.addMember(user);
        g.addMember(friend);
    }

    public String getNextEventID(){
        String val = Integer.toString(nextEventID);
        nextEventID++;
        return val;
    }

    public String getNextGroupID(){
        String val = Integer.toString(nextGroupID);
        nextGroupID++;
        return val;
    }

    public String getNextUserID(){
        String val = Integer.toString(nextUserID);
        nextUserID++;
        return val;
    }
}
