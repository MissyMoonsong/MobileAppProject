package com.example.jewel.test_project;

/**
 * Created by allis on 3/27/2017.
 */

public class DatabaseScheduleEvent {
    private String eventID;

    public DatabaseScheduleEvent() {
        /*Blank default constructor essential for Firebase*/
    }

    //Getters and Setters
    public String getEventID(){
        return eventID;
    }
    public void setEventID(String event) {
        this.eventID = event;
    }
}
