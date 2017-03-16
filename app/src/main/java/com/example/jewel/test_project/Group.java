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
    private List<Person> members;
    private Schedule groupSchedule;
    private List<ScheduleEvent> groupEvents;

    //TODO: Add everything else

    public Group(String name){
        this.name = name;
        members = new ArrayList<>();
        groupSchedule = new Schedule(name + "Schedule");
        groupEvents = new ArrayList<>();
    }

    //Clears old group schedule and rebuilds a a new one
    public void buildGroupSchedule(){
        groupSchedule.clearEvents();
        //TODO: Combine members' schedules into a group schedule (does not change member schedule)
    }

    private void addPersonalSchedule(Schedule ps){
        //TODO: add this person's schedule into the group's
    }

    public void addGroupEvent(ScheduleEvent event){
        groupSchedule.addEvent(event);
        groupEvents.add(event);
    }

    private void removePersonalSchedule(Schedule ps){
        //TODO: This is going to need some work to find all the right events to remove
        //Maybe save Group Events separately, then rebuild the schedule without the person
    }

    public void addMember(Person p){
        members.add(p);
        addPersonalSchedule(p.getSchedule());
    }

    public void removeMember(Person p){
        members.add(p);
        removePersonalSchedule(p.getSchedule());
    }
}
