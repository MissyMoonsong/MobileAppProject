package com.example.jewel.test_project;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Jewel on 4/6/2017.
 */

public class CommandAddUserToGroup {
    private String groupID;
    private String userLookup, userID;
    private Firebase ref;

    public CommandAddUserToGroup(String groupID, String userLookup, Firebase ref){
        this.groupID = groupID;
        this.userLookup = userLookup;
        this.ref = ref;
    }

    public void begin(){
        //Get user's ID from the database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Convert email to username
                        String emailToL = userLookup.toLowerCase();
                        String username = emailToL.replaceAll("\\W", "");

                        DatabaseUserID userSnapRef = dataSnapshot.child("Users").child(username).getValue(DatabaseUserID.class);

                        userID = userSnapRef.getUserID();

                        executeCommand();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    //Go back to the datamanager with the user found
    public void executeCommand(){
        if(userID != null) {
            Person p = new Person(userLookup, userID);
            DataManager.Instance().addOtherUserToGroup(groupID, p, ref);
        }
    }
}
