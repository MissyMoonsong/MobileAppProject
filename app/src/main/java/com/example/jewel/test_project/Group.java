package com.example.jewel.test_project;

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
    private int groupID;
    private List<Person> members;
    private Schedule groupSchedule;

    //TODO: Add everything else

    public Group(String name, int id){
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

        /*TODO: Probably a good candidate for improving performance by
         * creating blocks of "busy" time instead of having many events,
         * which will probably have overlap
         */
    }

    private void addPersonalSchedule(Schedule ps){
        //Adds a reference to each event in the person's schedule to the group schedule
        for(ScheduleEvent e : ps.getAllEvents()){
            groupSchedule.addEvent(e);
        }
    }

    public void addMember(Person p){
        members.add(p);
        addPersonalSchedule(p.getSchedule());
    }

    public void removeMember(Person p){
        members.remove(p);
        //Rebuild without this member
        rebuildGroupSchedule();
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

    public String getName(){
        return name;
    }

    public int getGroupID(){
        return groupID;
    }
}
