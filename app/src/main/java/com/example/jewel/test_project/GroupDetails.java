package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GroupDetails extends AppCompatActivity implements View.OnClickListener{
    Button btnAddMember, btnGoSchedule, btnLeave;
    TextView userList;
    EditText txtMemberName;
    Group myGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        btnAddMember = (Button)findViewById(R.id.btn_group_add_member);
        btnAddMember.setOnClickListener(this);
        btnGoSchedule = (Button)findViewById(R.id.btn_group_schedule);
        btnGoSchedule.setOnClickListener(this);
        btnLeave = (Button)findViewById(R.id.btn_leave_group);
        btnLeave.setOnClickListener(this);

        txtMemberName = (EditText)findViewById(R.id.txt_member_name);

        String groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
        myGroup = DataManager.Instance().getGroups().get(groupKey);

        userList = (TextView)findViewById(R.id.txt_user_list);
        String text = myGroup.getName() + "\n" + myGroup.getMemberList();
        userList.setText(text);
    }

    @Override
    public void onClick(View view){
        if(view == btnAddMember){
            String name = txtMemberName.getText().toString();
            if(name.length() > 0){
                //TODO: Lookup person through DB instead
                Person p = new Person(name, "2");
                if(p != null) {
                    myGroup.addMember(p);
                } else{
                    //TODO: Something about user not found
                }
            }

        } else if (view == btnGoSchedule){
            Bundle b = new Bundle();
            b.putString(DataManager.SCHEDULE_TYPE_KEY, "Group");
            b.putString(DataManager.GROUP_ID_KEY, myGroup.getGroupID());

            Intent intent = new Intent(this, EventListViewer.class);
            intent.putExtras(b);
            startActivity(intent);
        } else if (view == btnLeave){
            //TODO: Remove user-group membership in database
            DataManager.Instance().getGroups().remove(myGroup);

            Bundle b = new Bundle();
            b.putString(DataManager.SCHEDULE_TYPE_KEY, "User");

            Intent intent = new Intent(this, EventListViewer.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }
}
