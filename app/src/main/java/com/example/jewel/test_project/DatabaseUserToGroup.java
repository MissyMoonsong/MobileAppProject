package com.example.jewel.test_project;

/**
 * Created by allis on 3/27/2017.
 */

public class DatabaseUserToGroup {
    private String groupID;

    public DatabaseUserToGroup() {
        /*Blank default constructor essential for Firebase*/
    }

    //Getters and Setters
    public String getGroupID(){
        return groupID;
    }
    public void setGroupID(String group) {
        this.groupID = group;
    }
}
