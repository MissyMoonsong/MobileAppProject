package com.example.jewel.test_project;

/**
 * Created by allis on 3/27/2017.
 */

public class DatabaseGroup {
    private String groupName;

    public DatabaseGroup() {
        /*Blank default constructor essential for Firebase*/
    }

    //Getters and Setters
    public String getGroupName(){
        return groupName;
    }
    public void setGroupName(String name) {
        this.groupName = name;
    }
}
