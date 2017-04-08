package com.example.jewel.test_project;

import android.provider.ContactsContract;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

        //TODO: DOES THIS NEED TO BE HREE?
        //refreshFromDatabase();
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

    public boolean haveConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /***
     * This also gives the Event its EventID, maybe
     * @param event
     * @param scheduleType
     * @param groupKey
     */
    //TODO: Number 1
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

    //TODO: Number 2
    public void createGroupAndAddUser(String groupName, Firebase ref){
        Firebase pushedGroupRef = ref.child("Group").push();
        DatabaseGroup dgroup = new DatabaseGroup();
        dgroup.setGroupName(groupName);
        pushedGroupRef.setValue(dgroup);
        String groupId = pushedGroupRef.getKey();

        addUserToGroupMembership(user.getUserID(), groupId, ref);
        addGroupToUserMembership(user.getUserID(), groupId, ref);

        Group g = new Group(groupName, groupId);
        g.addMember(DataManager.Instance().getUser());
        DataManager.Instance().getGroups().put(g.getGroupID(), g);
    }

    //TODO: Number 3
    private void addUserToGroupMembership(String userID, String groupID, Firebase ref){
        Firebase pushedMembershipRef = ref.child("MembershipUserToGroup").child(userID).push();
        DatabaseUserToGroup temp = new DatabaseUserToGroup();
        temp.setGroupID(groupID);
        pushedMembershipRef.setValue(temp);
    }

    //TODO: Number 4
    private void addGroupToUserMembership(String userID, String groupID, Firebase ref){
        Firebase pushedMembershipRef = ref.child("MembershipGroupToUser").child(groupID).push();
        DatabaseGroupToUser temp = new DatabaseGroupToUser();
        temp.setUserID(userID);
        pushedMembershipRef.setValue(temp);
    }

    //TODO: Number 5
    private void removeUserToGroupMembership(String userID, String groupID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = db.child("MembershipUserToGroup").child(userID).orderByChild("groupID").equalTo(groupID);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership: dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }

    //TODO: Number 6
    private void removeGroupToUserMembership(String userID, String groupID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = db.child("MembershipGroupToUser").child(groupID).orderByChild("userID").equalTo(userID);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership: dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }

    //TODO: Number 7
    public void removeUserFromGroup(String groupID){
        removeUserToGroupMembership(user.getUserID(), groupID);
        removeGroupToUserMembership(user.getUserID(), groupID);

        groups.remove(groupID);
    }

    //TODO: Number 8
    public void addOtherNameUserToGroup(String groupID, String userEmail, Firebase ref, DataSnapshot snap){
        Person p = DataManager.Instance().lookUpUserByEmail(userEmail, snap);
        if(p != null) {
            String userID = p.getUserID();

            addUserToGroupMembership(userID, groupID, ref);
            addGroupToUserMembership(userID, groupID, ref);
        }
    }

    //TODO: Number 9
    public void addOtherPersonUserToGroup(String groupID, Person p, Firebase ref) {
        if (p != null) {
            String userID = p.getUserID();

            addUserToGroupMembership(userID, groupID, ref);
            addGroupToUserMembership(userID, groupID, ref);
        }
    }

    //TODO: Number 10
    public void deleteUserEvent(String eventID){
        //Deletes the event from THIS USER'S schedule
        user.getSchedule().removeEvent(user.getSchedule().findEventByID(eventID));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query eventQuery = db.child("Schedules").child(user.getUserID()).orderByChild("eventID").equalTo(eventID);

        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot membership: dataSnapshot.getChildren()) {
                    membership.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB", "onCancelled", databaseError.toException());
            }
        });
    }

    //TODO: Number 12
    public void fillPersonObjectWithEvents(Person p, DataSnapshot snap){
        String userID = p.getUserID();

        DataSnapshot userEventList = snap.child("Schedules").child(userID);
        for(DataSnapshot e : userEventList.getChildren()){
            DatabaseScheduleEvent ev = e.getValue(DatabaseScheduleEvent.class);
            String eventID = ev.getEventID();

            //Lookup the event
            DatabaseEvent event = snap.child("Event").child(eventID).getValue(DatabaseEvent.class);

            ScheduleEvent se = buildScheduleEventFromEvent(event);
            se.setEventID(eventID);

            p.getSchedule().addEvent(se);
        }
    }

    //TODO: Number 13
    public void fillGroupWithMembers(Group g, DataSnapshot snap){
        String groupID = g.getGroupID();

        DataSnapshot groupMembership = snap.child("MembershipGroupToUser").child(groupID);

        for(DataSnapshot m : groupMembership.getChildren()){
            DatabaseGroupToUser gu = m.getValue(DatabaseGroupToUser.class);
            String memberID = gu.getUserID();

            if(memberID.equals(user.getUserID())) {
                g.addMember(user);
            } else{
                //Create a Person object
                Person p = new Person(lookUpUserByID(memberID, snap), memberID);
                fillPersonObjectWithEvents(p, snap);
                g.addMember(p);
            }
        }
    }

    //TODO: Number 14
    public String lookUpUserByID(String userID, DataSnapshot snap){
        String userName = (String) snap.child("UsersIDToName").child(userID).getValue();
        return userName;
    }

    //TODO: Number 15
    public Person lookUpUserByEmail(String nameEmail, DataSnapshot dataSnapshot) {
        //Users stored in Firebase by Email
        Person otherUser = null;

        //Convert email to username
        String emailToL = nameEmail.toLowerCase();
        String username = emailToL.replaceAll("\\W", "");

        String otherUserID = (String) dataSnapshot.child("UsersNameToID").child(username).getValue();

        if (otherUserID != NULL) {
            Person temp = new Person(username, otherUserID);
            otherUser = temp;
        }

        return otherUser;
    }

    //TODO: Number 16
    public void refreshWithSnap(DataSnapshot snap) {
        //Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Get user id
        String userID = user.getUid();
        String name =  user.getEmail();
        //Set up user object
        this.user = new Person(name, userID);
        fillPersonObjectWithEvents(this.user,  snap);

        //get all groups user is in
        DataSnapshot userGroups =  snap.child("MembershipUserToGroup").child(userID);
        //For reach group
        for(DataSnapshot userGroup : userGroups.getChildren()){
            DatabaseUserToGroup ug = userGroup.getValue(DatabaseUserToGroup.class);
            String groupID = ug.getGroupID();

            //Get group name
            DatabaseGroup dbGroup =  snap.child("Group").child(groupID).getValue(DatabaseGroup.class);
            String groupName = dbGroup.getGroupName();

            Group g = new Group(groupName, groupID);
            groups.put(groupID, g);

            fillGroupWithMembers(g,  snap);

            g.rebuildGroupSchedule();
        }
    }

    //TODO: Number 17
    public void refreshFromDatabase(){
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

    //TODO: Number 18
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
