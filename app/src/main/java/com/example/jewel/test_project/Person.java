package com.example.jewel.test_project;

/**
 * Created by Jewel on 3/16/2017.
 * This class represents a "Person" in our app.
 */

public class Person {
    private Schedule schedule;
    private String name;
    private int userID;

    public Person(String name,int id){
        this.name = name;
        schedule = new Schedule(name + "'s Schedule", id);
        userID = id;
    }

    public Schedule getSchedule(){
        return  schedule;
    }

    @Override
    public String toString(){
        return name;
    }

    public int getUserID(){
        return userID;
    }
}
