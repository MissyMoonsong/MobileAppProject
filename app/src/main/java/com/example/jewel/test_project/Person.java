package com.example.jewel.test_project;

/**
 * Created by Jewel on 3/16/2017.
 * This class represents a "Person" in our app.
 */

public class Person {
    private Schedule schedule;
    private String name;
    //TODO: Add everything else

    public Person(String name){
        this.name = name;
        //TODO: Change naming?
        schedule = new Schedule(name + "'s Schedule");
    }

    public Schedule getSchedule(){
        return  schedule;
    }


}
