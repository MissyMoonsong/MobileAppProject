package com.example.jewel.test_project;

/**
 * Created by allis on 3/27/2017.
 */

public class DatabaseGroupToUser {
    private String userID;

    public DatabaseGroupToUser() {
        /*Blank default constructor essential for Firebase*/
    }

    //Getters and Setters
    public String getUserID(){
        return userID;
    }
    public void setUserID(String user) {
        this.userID = user;
    }
}
