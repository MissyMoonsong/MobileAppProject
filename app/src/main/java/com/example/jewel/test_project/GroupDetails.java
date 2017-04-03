package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class GroupDetails extends AppCompatActivity implements View.OnClickListener{
    Button btnAddMember, btnGoSchedule, btnLeave;
    TextView userList;
    EditText txtMemberName;
    Group myGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Firebase.setAndroidContext(this);

        btnAddMember = (Button)findViewById(R.id.btn_group_add_member);
        btnAddMember.setOnClickListener(this);
        btnGoSchedule = (Button)findViewById(R.id.btn_group_schedule);
        btnGoSchedule.setOnClickListener(this);
        btnLeave = (Button)findViewById(R.id.btn_leave_group);
        btnLeave.setOnClickListener(this);

        txtMemberName = (EditText)findViewById(R.id.txt_member_name);

        String groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
        Group g = DataManager.Instance().getGroups().get(groupKey);
        if (g != null){
            myGroup = DataManager.Instance().getGroups().get(groupKey);
            myGroup.rebuildGroupSchedule(); //Refresh the group
        } else{
            //TODO: Pop-up message about couldn't view group info
            Intent i = new Intent(this, GroupMainPageActivity.class);
            startActivity(i);
        }

        fillNames();
    }

    private void fillNames(){
        userList = (TextView)findViewById(R.id.txt_user_list);
        String text = "Group: " + myGroup.getName() + "\n" + myGroup.getMemberList();
        userList.setText(text);
    }

    @Override
    public void onResume(){
        super.onResume();
        fillNames();
    }

    @Override
    public void onClick(View view){
        if(view == btnAddMember){
            String name = txtMemberName.getText().toString();
            if(name.length() > 0){

                Firebase ref = new Firebase(Config.FIREBASE_URL);
                DataManager.Instance().addOtherUserToGroup(myGroup.getGroupID(), name, ref);
            }

            //Refresh member names
            fillNames();

        } else if (view == btnGoSchedule){
            Bundle b = new Bundle();
            b.putString(DataManager.SCHEDULE_TYPE_KEY, "Group");
            b.putString(DataManager.GROUP_ID_KEY, myGroup.getGroupID());

            Intent intent = new Intent(this, EventListViewer.class);
            intent.putExtras(b);
            startActivity(intent);
        } else if (view == btnLeave){
            DataManager.Instance().removeUserFromGroup(myGroup.getGroupID());
            //Go Back
            Intent intent = new Intent(this, GroupMainPageActivity.class);
            startActivity(intent);
        }
    }
}
