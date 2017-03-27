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

    private Person user;
    private Map<String, Group> groups;

    private DataManager(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot();
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        refreshFromDatabase(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        //createDummySchedule();
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
        ScheduleEvent oneTime = new ScheduleEvent("One Time Event", start2, end2);

        user.getSchedule().addEvent(recurring);
        user.getSchedule().addEvent(oneTime);

        //Creating a second Person
        Person friend = new Person("Friend", "1");
        Calendar startF = Calendar.getInstance();
        Calendar endF = Calendar.getInstance();
        startF.set(2017,3, 10, 1,0);
        endF.set(2017, 3, 17, 2, 0);

        ScheduleEvent oneTimeF = new ScheduleEvent("Friend's Event", startF, endF);

        friend.getSchedule().addEvent(oneTimeF);

        //Creating a Group
        Group g = new Group("Test Group", "1");
        groups.put("1", g);
        g.addMember(user);
        g.addMember(friend);
    }

    public void refreshFromDatabase(DataSnapshot dataSnapshot){
        groups = new HashMap<>();

        //Creating firebase object
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Get user id
        String userID = user.getUid();
        String name =  user.getEmail();
        //Set up user object
        this.user = new Person(name, userID);

        Schedule userSchedule = this.user.getSchedule();
        buildSchedule(userSchedule, userID, dataSnapshot);

        //get all groups user is in
        DataSnapshot userGroups = dataSnapshot.child("MembershipUserToGroup").child(userID);
        //For reach group
        for(DataSnapshot groupIDSnap : userGroups.getChildren()){
            String groupID = groupIDSnap.getValue().toString();

            //Get group name
            DataSnapshot groupSnap = dataSnapshot.child("Group").child(groupID);
            String groupName = groupSnap.getValue().toString();

            Group g = new Group(groupName, groupID);
            groups.put(groupID, g);

            //for each member
            DataSnapshot groupMembers = dataSnapshot.child("MembershipGroupToUser").child(groupID);

            for(DataSnapshot memberIDSnap : groupMembers.getChildren()){
                String memberID = memberIDSnap.getValue().toString();

                buildSchedule(g.getGroupSchedule(), memberID, dataSnapshot);
            }
        }
    }

    public void buildSchedule(Schedule s, String userID, DataSnapshot dataSnapshot){
        //get all events user connects to -- build schedule
        DataSnapshot schedules = dataSnapshot.child("Schedule");
        DataSnapshot userSchedule = schedules.child(userID);

        for(DataSnapshot eventIDChild : userSchedule.getChildren()){
            String eventID = eventIDChild.getValue().toString();

            DataSnapshot eventSnapshot = dataSnapshot.child("Event");
            DataSnapshot event_id_snap = eventSnapshot.child(eventID);

            Map<String, Object> attributes = new HashMap<>();

            //Make a map of this event's attributes
            for(DataSnapshot eventAttribute : event_id_snap.getChildren()) {
                attributes.put(eventAttribute.getKey(), eventAttribute.getValue());
            }

            ScheduleEvent e = buildEventFromMap(attributes);
            e.setEventID(eventID);

            s.addEvent(e);
        }
    }

    public ScheduleEvent buildEventFromMap(Map<String, Object> attributes){
        String eName = attributes.get("eventName").toString();

        int sYear = Integer.parseInt(attributes.get("startYear").toString());
        int sMonth = Integer.parseInt(attributes.get("startMonth").toString());
        int sDay = Integer.parseInt(attributes.get("startDay").toString());
        int sHour = Integer.parseInt(attributes.get("startHour").toString());
        int sMin = Integer.parseInt(attributes.get("startMin").toString());

        int eYear  = Integer.parseInt(attributes.get("endYear").toString());
        int eMonth  = Integer.parseInt(attributes.get("endMonth").toString());
        int eDay  = Integer.parseInt(attributes.get("endDay").toString());
        int eHour  = Integer.parseInt(attributes.get("endHour").toString());
        int eMin  = Integer.parseInt(attributes.get("endMin").toString());

        boolean sun = (boolean)attributes.get("rsunday");
        boolean mon= (boolean)attributes.get("rmonday");
        boolean tue= (boolean)attributes.get("rtuesday");
        boolean wed= (boolean)attributes.get("rwednesday");
        boolean thu= (boolean)attributes.get("rthursday");
        boolean fri= (boolean)attributes.get("rfriday");
        boolean sat= (boolean)attributes.get("rsaturday");

        //Adding to the schedule within the app
        Calendar start = Calendar.getInstance();

        start.set(Calendar.YEAR, sYear);
        start.set(Calendar.MONTH, sMonth);
        start.set(Calendar.DAY_OF_MONTH, sDay);
        start.set(Calendar.HOUR_OF_DAY, sHour);
        start.set(Calendar.MINUTE, sMin);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, eYear);
        end.set(Calendar.MONTH, eMonth);
        end.set(Calendar.DAY_OF_MONTH, eDay);
        end.set(Calendar.HOUR_OF_DAY, eHour);
        end.set(Calendar.MINUTE, eMin);

        boolean[] weekdays = new boolean[7];
        weekdays[0] = sun;
        weekdays[1] = mon;
        weekdays[2] = tue;
        weekdays[3] = wed;
        weekdays[4] = thu;
        weekdays[5] = fri;
        weekdays[6] = sat;


        ScheduleEvent e;

        if(ScheduleEvent.anyDaySelected(weekdays)){ //Recurring
             e = new ScheduleEvent(eName, start, end, weekdays);

        } else{
             e = new ScheduleEvent(eName, start, end);
        }

        return e;
    }
}
