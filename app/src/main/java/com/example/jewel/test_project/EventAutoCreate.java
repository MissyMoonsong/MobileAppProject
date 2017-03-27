package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EventAutoCreate extends AppCompatActivity implements View.OnClickListener {
    EditText txtEventName, txtStartYear, txtStartMonth, txtStartDay, txtEndYear, txtEndMonth,
            txtEndDay, txtEventDuration;
    Button btnGenerate;

    String scheduleType, groupKey;
    Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_auto_create);

        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);
        if (scheduleType.equals("User")) {
            schedule = DataManager.Instance().getUser().getSchedule();
        } else if (scheduleType.equals("Group")) {
            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            schedule = DataManager.Instance().getGroups().get(groupKey).getGroupSchedule();
        }

        txtEventName = (EditText) findViewById(R.id.txt_auto_event_name);

        txtStartYear = (EditText) findViewById(R.id.txt_auto_start_year);
        txtStartMonth = (EditText) findViewById(R.id.txt_auto_start_month);
        txtStartDay = (EditText) findViewById(R.id.txt_auto_start_day);

        txtEndYear = (EditText) findViewById(R.id.txt_auto_end_year);
        txtEndMonth = (EditText) findViewById(R.id.txt_auto_end_month);
        txtEndDay = (EditText) findViewById(R.id.txt_auto_end_day);

        txtEventDuration = (EditText) findViewById(R.id.txt_auto_event_duration);

        btnGenerate = (Button) findViewById(R.id.btn_auto_gen_event);
        btnGenerate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Get input values
        String eventName = txtEventName.getText().toString();

        int startYear = Integer.parseInt(txtStartYear.getText().toString());
        int startMonth = Integer.parseInt(txtStartMonth.getText().toString());
        int startDay = Integer.parseInt(txtStartDay.getText().toString());

        int endYear = Integer.parseInt(txtEndYear.getText().toString());
        int endMonth = Integer.parseInt(txtEndMonth.getText().toString());
        int endDay = Integer.parseInt(txtEndDay.getText().toString());

        int duration = Integer.parseInt(txtEventDuration.getText().toString());

        //Generate an event this schedule
        Calendar windowStart = new GregorianCalendar(startYear, startMonth, startDay);
        Calendar windowEnd = new GregorianCalendar(endYear, endMonth, endDay);

        ScheduleEvent event = schedule.findTimeInSchedule(windowStart, windowEnd, duration);
        event.changeName(eventName);
        //TODO: Add event to database -- connect to the right users! (also get an ID for the event)
        schedule.addEvent(event);
        if (scheduleType.equals("Group")) {
            DataManager.Instance().getGroups().get(groupKey).rebuildGroupSchedule();
            //TODO: add event to EACH MEMBER OF GROUP IN DATABASE
        }

        //Go back to ListView
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
        b.putString(DataManager.GROUP_ID_KEY, groupKey);

        Intent intent = new Intent(this, EventListViewer.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
