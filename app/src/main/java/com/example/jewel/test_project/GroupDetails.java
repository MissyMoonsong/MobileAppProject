package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupDetails extends AppCompatActivity implements View.OnClickListener {
    Button btnAddMember, btnGoSchedule, btnLeave;
    TextView userList;
    EditText txtMemberName;
    Group myGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        //DataManager.Instance().refreshFromDatabase();
        Firebase.setAndroidContext(this);

        btnAddMember = (Button) findViewById(R.id.btn_group_add_member);
        btnAddMember.setOnClickListener(this);
        btnGoSchedule = (Button) findViewById(R.id.btn_group_schedule);
        btnGoSchedule.setOnClickListener(this);
        btnLeave = (Button) findViewById(R.id.btn_leave_group);
        btnLeave.setOnClickListener(this);

        txtMemberName = (EditText) findViewById(R.id.txt_member_name);

        String groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
        myGroup = DataManager.Instance().getGroups().get(groupKey);
        myGroup.rebuildGroupSchedule(); //Refresh the group

        fillNames();
    }

    private void fillNames() {
        userList = (TextView) findViewById(R.id.txt_user_list);
        String text = "Group: " + myGroup.getName() + "\n" + myGroup.getMemberList();
        userList.setText(text);
    }


    @Override
    public void onClick(View view) {
        if (view == btnAddMember) {
            final String name = txtMemberName.getText().toString();
            if (name.length() > 0) {

                final Firebase ref = new Firebase(Config.FIREBASE_URL);

                //Check for network Connection
                boolean networkConnection = DataManager.Instance().haveConnection(getApplicationContext());

                if (networkConnection == true) {
                    //Get user's ID from the database
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                    db.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    DataManager.Instance().addOtherNameUserToGroup(myGroup.getGroupID(), name, ref, dataSnapshot);
                                    Toast.makeText(getApplicationContext(), "Member Added", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                    );
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                }
            }
            //Refresh member names
            fillNames();
        } else if (view == btnGoSchedule) {
            Bundle b = new Bundle();
            b.putString(DataManager.SCHEDULE_TYPE_KEY, "Group");
            b.putString(DataManager.GROUP_ID_KEY, myGroup.getGroupID());

            Intent intent = new Intent(this, EventListViewer.class);
            intent.putExtras(b);
            startActivity(intent);
        } else if (view == btnLeave) {

            //Check for network Connection
            boolean networkConnection = DataManager.Instance().haveConnection(getApplicationContext());

            if (networkConnection == true) {
                DataManager.Instance().removeUserFromGroup(myGroup.getGroupID());
            } else {
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
            }

            //Go Back
            Intent intent = new Intent(this, GroupMainPageActivity.class);
            startActivity(intent);
        }
    }
}
