package com.example.jewel.test_project;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EventAutoCreate extends AppCompatActivity {
    String scheduleType, groupKey;
    Schedule schedule;

    //Variables For Date
    final int DATE_PICKER_BEGIN = 0;
    final int DATE_PICKER_END = 1;
    private Calendar calendar;
    private Button button_beginDate, button_endDate;
    private int b_year, b_month, b_day;
    private int e_year, e_month, e_day;

    //Variables For Other
    private EditText txtEventName, txtEventDuration;
    private Button btnGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_auto_create);

        //Getting extras from bundle
        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);
        if (scheduleType.equals("User")) {
            schedule = DataManager.Instance().getUser().getSchedule();
        } else if (scheduleType.equals("Group")) {
            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            Group g = DataManager.Instance().getGroups().get(groupKey);
            if (g != null){
                schedule = g.getGroupSchedule();
            } else{
                //TODO: Pop-up message about group not being valid
                Intent i = new Intent(this, GroupMainPageActivity.class);
                startActivity(i);
            }
        }

        //Setting all the view variables
        setViewsDate();
        setViewsOther();

        Firebase.setAndroidContext(this);

        btnGenerate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Get input values
                String eventName = txtEventName.getText().toString();

                int startYear = b_year;
                int startMonth = b_month;
                int startDay = b_day;

                int endYear = e_year;
                int endMonth = e_year;
                int endDay = e_day;

                int duration = Integer.parseInt(txtEventDuration.getText().toString());

                //Generate an event this schedule
                Calendar windowStart = new GregorianCalendar(startYear, startMonth, startDay);
                Calendar windowEnd = new GregorianCalendar(endYear, endMonth, endDay);

                ScheduleEvent event = schedule.findTimeInSchedule(windowStart, windowEnd, duration);

                if (event != null) { //Event Found
                    event.changeName(eventName);
                    //Creating firebase object
                    Firebase ref = new Firebase(Config.FIREBASE_URL);
                    DataManager.Instance().addUnpublishedEvent(event, scheduleType, groupKey, ref);
                }

                //Go to list again
                returnToList();
            }
        });
    }

    private void setViewsDate() {
        button_beginDate = (Button) findViewById(R.id.button_beginDate_auto);
        button_endDate = (Button) findViewById(R.id.button_endDate_auto);
        calendar = Calendar.getInstance();
        b_year = calendar.get(Calendar.YEAR);
        b_month = calendar.get(Calendar.MONTH);
        b_day = calendar.get(Calendar.DAY_OF_MONTH);
        e_year = calendar.get(Calendar.YEAR);
        e_month = calendar.get(Calendar.MONTH);
        e_day = calendar.get(Calendar.DAY_OF_MONTH);
        showDateBegin(b_year, b_month + 1, b_day);
        showDateEnd(e_year, e_month + 1, e_day);
    }

    private void setViewsOther() {
        txtEventDuration = (EditText) findViewById(R.id.txt_auto_event_duration);
        txtEventName = (EditText) findViewById(R.id.txt_auto_event_name);
        btnGenerate = (Button) findViewById(R.id.btn_auto_gen_event);
    }

    @SuppressWarnings("deprecation")
    public void setBeginDateAuto(View view) {
        showDialog(DATE_PICKER_BEGIN);
    }

    @SuppressWarnings("deprecation")
    public void setEndDateAuto(View view) {
        showDialog(DATE_PICKER_END);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            //For Date
            case DATE_PICKER_BEGIN:
                return new DatePickerDialog(this, beginDateListener, b_year, b_month, b_day);
            case DATE_PICKER_END:
                return new DatePickerDialog(this, endDateListener, e_year, e_month, e_day);
        }
        return null;
    }

    //For Date
    DatePickerDialog.OnDateSetListener beginDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // arg1 = year
            b_year = arg1;
            // arg2 = month
            b_month = arg2;
            // arg3 = day
            b_day = arg3;
            showDateBegin(arg2 + 1, arg3, arg1);
        }
    };
    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // arg1 = year
            e_year = arg1;
            // arg2 = month
            e_month = arg2;
            // arg3 = day
            e_day = arg3;
            showDateEnd(arg2 + 1, arg3, arg1);
        }
    };

    //For Date
    private void showDateBegin(int month, int day, int year) {
        button_beginDate.setText(new StringBuilder().append(month).append("/").append(day).append("/").append(year));
    }

    private void showDateEnd(int month, int day, int year) {
        button_endDate.setText(new StringBuilder().append(month).append("/").append(day).append("/").append(year));
    }

    private void returnToList() {
        //Go back to ListView
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
        b.putString(DataManager.GROUP_ID_KEY, groupKey);

        Intent intent = new Intent(this, EventListViewer.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
