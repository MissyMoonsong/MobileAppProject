package com.example.jewel.test_project;

import android.provider.Contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jewel on 3/16/2017.
 * This class represents a "Group"
 * It includes multiple people but has
 * one schedule of its own
 */

public class Group {
    private String name;
    private String groupID;
    private List<Person> members;
    private Schedule groupSchedule;

    public Group(String name, String id){
        this.name = name;
        members = new ArrayList<>();
        groupSchedule = new Schedule(name + "Schedule", id);
        groupID = id;
    }

    /***
     * Clears the old group schedule and rebuilds a new one.
     * Use this for updates to member schedules occur that are not handled
     * by adding or removing the member.
     */
    public void rebuildGroupSchedule(){
        groupSchedule.clearEvents();

        for(Person p : members){
            addPersonalSchedule(p.getSchedule());
        }
    }

    private void addPersonalSchedule(Schedule ps){
        //Adds a reference to each event in the person's schedule to the group schedule
        for(ScheduleEvent e : ps.getAllEvents()){
            //Check for if the event is already in the group schedule from another user
            if(groupSchedule.findEventByID(e.getEventID()) == null){
                groupSchedule.addEvent(e);
            }
        }
    }

    public void addMember(Person p){
        members.add(p);
        addPersonalSchedule(p.getSchedule());
    }

    public Schedule getGroupSchedule(){
        return  groupSchedule;
    }

    public String getMemberList(){
        String result = "";
        for(Person p : members){
            result += p.toString() + " ";
        }
        return result;
    }

    public List<Person> getMembers(){
        return members;
    }

    public String getName(){
        return name;
    }

    public String getGroupID(){
        return groupID;
    }

    @Override
    public String toString(){
        return getName();
    }
}
