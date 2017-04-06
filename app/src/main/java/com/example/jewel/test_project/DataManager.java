package com.example.jewel.test_project;

import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

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
        user = new Person("Phone Owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
        groups = new HashMap<>();

        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().getRoot();
        //createDummySchedule(); //TODO: Remove this when database works


        refreshFromDatabase();
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
        user = new Person("Phone Owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        pushedEventRef.setValue(event.toDatabaseEvent());
        String eventId = pushedEventRef.getKey();
        event.setEventID(eventId);

        if (scheduleType.equals("Group")) { //Group so add to each member
            for (Person p : groups.get(groupKey).getMembers()) {
                //Add event for each person
                p.getSchedule().addEvent(event);
                String userID = p.getUserID();

                DatabaseScheduleEvent temp = new DatabaseScheduleEvent();
                temp.setEventID(eventId);
                ref.child("Schedules").child(userID).push().setValue(temp);
            }

            //Update to new group view
            groups.get(groupKey).rebuildGroupSchedule();
        } else { //Single user schedule -- THIS user
            user.getSchedule().addEvent(event);

            DatabaseScheduleEvent temp = new DatabaseScheduleEvent();
            temp.setEventID(eventId);
            ref.child("Schedules").child(user.getUserID()).push().setValue(temp);
        }
    }

    public void createGroupAndAddUser(String groupName, Firebase gref) {
        Firebase pushedGroupRef = gref.child("Group").push();
        DatabaseGroup dgroup = new DatabaseGroup();
        dgroup.setGroupName(groupName);
        pushedGroupRef.setValue(dgroup);
        String groupId = pushedGroupRef.getKey();

        addUserToGroupMembership(user.getUserID(), groupId, gref);
        addGroupToUserMembership(user.getUserID(), groupId, gref);

        Group g = new Group(groupName, groupId);
        g.addMember(DataManager.Instance().getUser());
        DataManager.Instance().getGroups().put(g.getGroupID(), g);
    }

    private void addUserToGroupMembership(String userID, String groupID, Firebase ref) {
        Firebase pushedMembershipRef = ref.child("MembershipUserToGroup").child(userID).push();
        DatabaseUserToGroup temp = new DatabaseUserToGroup();
        temp.setGroupID(groupID);
        pushedMembershipRef.setValue(temp);
    }

    private void addGroupToUserMembership(String userID, String groupID, Firebase ref) {
        Firebase pushedMembershipRef = ref.child("MembershipGroupToUser").child(groupID).push();
        DatabaseGroupToUser temp = new DatabaseGroupToUser();
        temp.setUserID(userID);
        pushedMembershipRef.setValue(temp);
    }

    private void removeUserToGroupMembership(String userID, String groupID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = db.child("MembershipUserToGroup").child(userID).orderByChild("groupID").equalTo(groupID);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership : dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }

    private void removeGroupToUserMembership(String userID, String groupID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = db.child("MembershipGroupToUser").child(groupID).orderByChild("userID").equalTo(userID);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership : dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }


    public void removeUserFromGroup(String groupID) {
        removeUserToGroupMembership(user.getUserID(), groupID);
        removeGroupToUserMembership(user.getUserID(), groupID);

        groups.remove(groupID);
    }

    public void addOtherUserToGroup(String groupID, String userLookup, Firebase ref) {
        CommandAddUserToGroup command = new CommandAddUserToGroup(groupID, userLookup, ref);
        command.begin();
    }

    public void addOtherUserToGroup(String groupID, Person p, Firebase ref){
        if (p != null) {
            String userID = p.getUserID();

            addUserToGroupMembership(userID, groupID, ref);
            addGroupToUserMembership(userID, groupID, ref);
        }
    }


    public void deleteUserEvent(String eventID) {
        //Deletes the event from THIS USER'S schedule
        user.getSchedule().removeEvent(user.getSchedule().findEventByID(eventID));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query eventQuery = db.child("Schedules").child(user.getUserID()).orderByChild("eventID").equalTo(eventID);

        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership : dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }


    public void fillPersonObjectWithEvents(Person p, DataSnapshot snap) {
        String userID = p.getUserID();

        DataSnapshot userEventList = snap.child("Schedules").child(userID);
        for (DataSnapshot e : userEventList.getChildren()) {
            DatabaseScheduleEvent ev = e.getValue(DatabaseScheduleEvent.class);
            String eventID = ev.getEventID();

            //Lookup the event
            DatabaseEvent event = snap.child("Event").child(eventID).getValue(DatabaseEvent.class);

            ScheduleEvent se = buildScheduleEventFromEvent(event);
            se.setEventID(eventID);

            p.getSchedule().addEvent(se);
        }
    }

    public void fillGroupWithMembers(Group g, DataSnapshot snap) {
        String groupID = g.getGroupID();

        DataSnapshot groupMembership = snap.child("MembershipGroupToUser").child(groupID);

        for (DataSnapshot m : groupMembership.getChildren()) {
            DatabaseGroupToUser gu = m.getValue(DatabaseGroupToUser.class);
            String memberUN = gu.getUserID();

            if (memberUN.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase().replaceAll("\\W", ""))) {
                g.addMember(user);
            } else {
                //Create a Person object
                Person p = new Person(getNameOfUser(memberUN, snap), memberUN);
                    fillPersonObjectWithEvents(p, snap);
                    g.addMember(p);
            }
        }
    }

    public String getNameOfUser(String userID, DataSnapshot snap){
        //TODO: The thing
        return "PLACEHOLDER";
    }

    public Person lookUpUserByEmail(String nameEmail, DataSnapshot dataSnapshot) {
        //Users stored in Firebase by Email
        Person otherUser = null;

        //Convert email to username
        String emailToL = nameEmail.toLowerCase();
        String username = emailToL.replaceAll("\\W", "");

        DatabaseUserID userSnapRef = dataSnapshot.child("Users").child(username).getValue(DatabaseUserID.class);

        String otherUserID = userSnapRef.getUserID();
        if (otherUserID != NULL) {
            Person temp = new Person(username, otherUserID);
            otherUser = temp;
        }

        return otherUser;
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

    public void refreshWithSnap(DataSnapshot dataSnapshot) {
        //Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Get user id
        String userID = user.getUid();
        String name = user.getEmail();
        //Set up user object
        this.user = new Person(name, userID);
        fillPersonObjectWithEvents(this.user, dataSnapshot);

        //get all groups user is in
        DataSnapshot userGroups = dataSnapshot.child("MembershipUserToGroup").child(userID);
        //For reach group
        for (DataSnapshot userGroup : userGroups.getChildren()) {
            DatabaseUserToGroup ug = userGroup.getValue(DatabaseUserToGroup.class);
            String groupID = ug.getGroupID();

            //Get group name
            DatabaseGroup dbGroup = dataSnapshot.child("Group").child(groupID).getValue(DatabaseGroup.class);
            String groupName = dbGroup.getGroupName();

            Group g = new Group(groupName, groupID);
            groups.put(groupID, g);

            fillGroupWithMembers(g, dataSnapshot);

            g.rebuildGroupSchedule();
        }
    }

    public void refreshFromDatabase() {
        groups = new HashMap<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        refreshWithSnap(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public static ScheduleEvent buildScheduleEventFromEvent(DatabaseEvent ev) {
        String eName = ev.getEventName();

        int sYear = ev.getStartYear();
        int sMonth = ev.getStartMonth();
        int sDay = ev.getStartDay();
        int sHour = ev.getStartHour();
        int sMin = ev.getStartMin();

        int eYear = ev.getEndYear();
        int eMonth = ev.getEndMonth();
        int eDay = ev.getEndDay();
        int eHour = ev.getEndHour();
        int eMin = ev.getEndMin();

        boolean sun = ev.getRSunday();
        boolean mon = ev.getRMonday();
        boolean tue = ev.getRTuesday();
        boolean wed = ev.getRWednesday();
        boolean thu = ev.getRThursday();
        boolean fri = ev.getRFriday();
        boolean sat = ev.getRSaturday();

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

        if (ScheduleEvent.anyDaySelected(weekdays)) { //Recurring
            e = new ScheduleEvent(eName, start, end, weekdays);

        } else {
            e = new ScheduleEvent(eName, start, end);
        }

        return e;
    }
}