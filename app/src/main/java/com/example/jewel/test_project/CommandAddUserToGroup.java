package com.example.jewel.test_project;

import android.widget.Toast;

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

                        userID = (String) dataSnapshot.child("UsersNameToID").child(username).getValue();

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
            DataManager.Instance().addOtherPersonUserToGroup(groupID, p, ref);
        }
    }
}
