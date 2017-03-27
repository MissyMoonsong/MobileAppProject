package com.example.jewel.test_project;

import android.provider.ContactsContract;
import android.util.Log;

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
 * <p>
 * This is a Singleton.
 * <p>
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

    private DataManager() {
        createDummySchedule(); //TODO: Remove this when database works

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot();
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //refreshFromDatabase(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    public static DataManager Instance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Person getUser() {
        return user;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }


    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    //TODO: Replace this with something for loading real schedules or something
    private void createDummySchedule() {
        user = new Person("Phone Owner", getNextUserID());
        groups = new HashMap<>();

        Calendar start1 = Calendar.getInstance();
        Calendar end1 = Calendar.getInstance();
        Calendar start2 = Calendar.getInstance();
        Calendar end2 = Calendar.getInstance();

        start1.set(2017, 3, 10, 0, 0);
        end1.set(2017, 3, 17, 1, 0);

        start2.set(2017, 3, 11, 8, 30);
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
        startF.set(2017, 3, 10, 1, 0);
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

    /***
     * This also gives the Event its EventID, maybe
     * @param event
     * @param scheduleType
     * @param groupKey
     */
    public void addUnpublishedEvent(ScheduleEvent event, String scheduleType, String groupKey, Firebase ref) {
        //Store values to firebase
        Firebase pushedEventRef = ref.child("Event").push();
        pushedEventRef.setValue(event);
        String postId = pushedEventRef.getKey();

        event.setEventID(postId);

        if (scheduleType.equals("Group")) { //Group so add to each member
            for (Person p : groups.get(groupKey).getMembers()) {
                //Add event for each person
                p.getSchedule().addEvent(event);
                //TODO: add event to EACH MEMBER OF GROUP IN DATABASE
                String userID = p.getUserID();
            }

            //Update to new group view
            groups.get(groupKey).rebuildGroupSchedule();
        } else { //Single user schedule -- THIS user
            user.getSchedule().addEvent(event);
            //TODO: Add event to database for the phone user

        }
    }

    public void createGroupAndAddUser(String groupName){
        Group g = new Group(groupName, getNextGroupID());
        g.addMember(DataManager.Instance().getUser());
        //TODO: DATABASE THING HERE -- Use the GROUP ID for the key below
        DataManager.Instance().getGroups().put(g.getGroupID(), g);
    }

    public void removeUserFromGroup(String groupID){
        //TODO: Remove user-group membership in database
        groups.remove(groupID);
    }

    public void addOtherUserToGroup(String groupID, String userLookup){
        Person p = DataManager.Instance().lookUpUser(userLookup);
        if(p != null) {
            groups.get(groupID).addMember(p);
            //TODO: Add person to group in database
        }
    }

    public void deleteUserEvent(String eventID){
        //Deletes the event from THIS USER'S schedule
        user.getSchedule().removeEvent(user.getSchedule().findEventByID(eventID));
        //TODO: Remove the connection between this event and user in the database
    }

    public Person lookUpUser(String nameEmail) {
        //Users stored in Firebase by Email

        //TODO: Replace this with actually getting info from DB
        //Return NULL if no such user exists

        return new Person(nameEmail, getNextUserID());
    }


    public String getNextEventID() {
        String val = Integer.toString(nextEventID);
        nextEventID++;
        return val;
    }

    public String getNextGroupID() {
        String val = Integer.toString(nextGroupID);
        nextGroupID++;
        return val;
    }

    public String getNextUserID() {
        String val = Integer.toString(nextUserID);
        nextUserID++;
        return val;
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

                //Create a Person object
                Person p = new Person("NAME HERE", memberID);
                g.addMember(p);

                buildSchedule(p.getSchedule(), memberID, dataSnapshot);
            }

            g.rebuildGroupSchedule();
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
