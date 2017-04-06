package com.example.jewel.test_project;

/**
 * Created by allis on 4/6/2017.
 */

public class DatabaseUsersIDToName {
    private String userName;

    public DatabaseUsersIDToName() {
        /*Blank default constructor essential for Firebase*/
    }

    //Getters and Setters
    public String getUserName(){
        return userName;
    }
    public void setUserName(String user) {
        this.userName = user;
    }
}
