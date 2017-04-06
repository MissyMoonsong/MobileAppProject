package com.example.jewel.test_project;

/**
 * Created by allis on 4/5/2017.
 */

public class DatabaseUsersNameToID {
    private String userID;

    public DatabaseUsersNameToID() {
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

