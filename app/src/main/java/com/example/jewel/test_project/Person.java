package com.example.jewel.test_project;

/**
 * Created by Jewel on 3/16/2017.
 * This class represents a "Person" in our app.
 */

public class Person {
    private Schedule schedule;
    private String name;
    private int UserID; //TODO: This is supposed to be the database key. Change type if needed.
    //TODO: Add everything else

    public Person(String name,int id){
        this.name = name;
        //TODO: Change naming?
        schedule = new Schedule(name + "'s Schedule");
        UserID = id;
    }

    public Schedule getSchedule(){
        return  schedule;
    }

    @Override
    public String toString(){
        return name;
    }

    public int getUserID(){
        return  UserID;
    }
}
