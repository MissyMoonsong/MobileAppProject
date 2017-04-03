package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class EventDetails extends AppCompatActivity implements View.OnClickListener {
    TextView txtName, txtWindow, txtTime, txtRecurrence;
    Button btnDelete, btnBack;
    String scheduleType, groupKey, eventID;
    Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        Firebase.setAndroidContext(this);

        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);
        if (scheduleType.equals("User")) {
            schedule = DataManager.Instance().getUser().getSchedule();
        } else if (scheduleType.equals("Group")) {

            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            Group g = DataManager.Instance().getGroups().get(groupKey);
            if (g != null){
                schedule = g.getGroupSchedule();
            } else{
                //TODO: Pop-up message about couldn't view group info
                Intent i = new Intent(this, GroupMainPageActivity.class);
                startActivity(i);
            }
        }

        eventID = getIntent().getExtras().getString(DataManager.EVENT_ID_KEY);

        txtName = (TextView) findViewById(R.id.txt_details_event_name);
        txtRecurrence = (TextView) findViewById(R.id.txt_details_event_recurrence);
        txtWindow = (TextView) findViewById(R.id.txt_details_event_window);
        txtTime = (TextView) findViewById(R.id.txt_details_event_time);

        btnDelete = (Button) findViewById(R.id.btn_delete_event);
        btnDelete.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btn_back_to_event_list);
        btnBack.setOnClickListener(this);

        fillInfo();
        adjustButtons();
    }

    private void adjustButtons(){
        if (scheduleType.equals("User")) {
            btnDelete.setEnabled(true);
        } else if (scheduleType.equals("Group")) {
            //Delete makes no sense when a Group schedule is based on members
            btnDelete.setEnabled(false);
        }
    }

    private void fillInfo() {
        ScheduleEvent event = schedule.findEventByID(eventID);

        if(event != null){
            txtName.setText(event.getEventName());
            txtRecurrence.setText(event.getEventRecurrence());
            txtWindow.setText(event.getEventWindow());
            txtTime.setText(event.getEventTime());
        }
    }

    @Override
    public void onClick(View view) {
        //Prepare to go back to list view
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
        b.putString(DataManager.GROUP_ID_KEY, groupKey);

        Intent intent = new Intent(this, EventListViewer.class);
        intent.putExtras(b);

        if (view == btnBack) {
            //Make no changes, just return to the list view
            startActivity(intent);

        } else if (view == btnDelete) {
            DataManager.Instance().deleteUserEvent(eventID);

            //Go back to list view
            startActivity(intent);
        }
    }
}
