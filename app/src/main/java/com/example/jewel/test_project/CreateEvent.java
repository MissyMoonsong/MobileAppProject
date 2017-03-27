package com.example.jewel.test_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by allis on 3/26/2017.
 */

public class CreateEvent extends AppCompatActivity {
    private String scheduleType, groupKey;
    private Schedule schedule;

    private EditText event_name;
    private EditText start_hour, start_min;
    private EditText end_hour, end_min;
    private EditText start_month, start_day, start_year;
    private EditText end_month, end_day, end_year;
    private ToggleButton r_sunday, r_monday, r_tuesday, r_wednesday, r_thursday, r_friday, r_saturday;
    private Button create_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Getting extras from bundle
        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);
        if (scheduleType.equals("User")) {
            schedule = DataManager.Instance().getUser().getSchedule();
        } else if (scheduleType.equals("Group")) {
            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            schedule = DataManager.Instance().getGroups().get(groupKey).getGroupSchedule();
        }

        //Initialize Views
        event_name = (EditText) findViewById(R.id.event_name);
        start_hour = (EditText) findViewById(R.id.start_hour);
        start_min = (EditText) findViewById(R.id.start_minute);
        end_hour = (EditText) findViewById(R.id.end_hour);
        end_min = (EditText) findViewById(R.id.end_minute);
        start_month = (EditText) findViewById(R.id.start_month);
        start_day = (EditText) findViewById(R.id.start_day);
        start_year = (EditText) findViewById(R.id.start_year);
        end_month = (EditText) findViewById(R.id.end_month);
        end_day = (EditText) findViewById(R.id.end_day);
        end_year = (EditText) findViewById(R.id.end_year);
        r_sunday = (ToggleButton) findViewById(R.id.r_sunday);
        r_monday = (ToggleButton) findViewById(R.id.r_monday);
        r_tuesday = (ToggleButton) findViewById(R.id.r_tuesday);
        r_wednesday = (ToggleButton) findViewById(R.id.r_wednesday);
        r_thursday = (ToggleButton) findViewById(R.id.r_thursday);
        r_friday = (ToggleButton) findViewById(R.id.r_friday);
        r_saturday = (ToggleButton) findViewById(R.id.r_saturday);
        create_event = (Button) findViewById(R.id.button_create_event);

        Firebase.setAndroidContext(this);

        create_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Creating firebase object
                Firebase ref = new Firebase(Config.FIREBASE_URL);

                //Getting values to store
                String evname = event_name.getText().toString().trim();
                Integer shour = Integer.parseInt(start_hour.getText().toString().trim());
                Integer smin = Integer.parseInt(start_min.getText().toString().trim());
                Integer ehour = Integer.parseInt(end_hour.getText().toString().trim());
                Integer emin = Integer.parseInt(end_min.getText().toString().trim());
                Integer smonth = Integer.parseInt(start_month.getText().toString().trim());
                Integer sday = Integer.parseInt(start_day.getText().toString().trim());
                Integer syear = Integer.parseInt(start_year.getText().toString().trim());
                Integer emonth = Integer.parseInt(end_month.getText().toString().trim());
                Integer eday = Integer.parseInt(end_day.getText().toString().trim());
                Integer eyear = Integer.parseInt(end_year.getText().toString().trim());
                Boolean iSun = r_sunday.isChecked();
                Boolean iMon = r_monday.isChecked();
                Boolean iTue = r_tuesday.isChecked();
                Boolean iWed = r_wednesday.isChecked();
                Boolean iThur = r_thursday.isChecked();
                Boolean iFri = r_friday.isChecked();
                Boolean iSat = r_saturday.isChecked();

                //Create Event Object
                Event event = new Event();

                //Add Values
                event.setEventName(evname);
                event.setStartHour(shour);
                event.setStartMin(smin);
                event.setEndHour(ehour);
                event.setEndMin(emin);
                event.setStartMonth(smonth);
                event.setStartDay(sday);
                event.setStartYear(syear);
                event.setEndMonth(emonth);
                event.setEndDay(eday);
                event.setEndYear(eyear);
                event.setRSunday(iSun);
                event.setRMonday(iMon);
                event.setRTuesday(iTue);
                event.setRWednesday(iWed);
                event.setRThursday(iThur);
                event.setRFriday(iFri);
                event.setRSaturday(iSat);

                //Store values to firebase
                ref.child("Event").setValue(event);

                //TODO: add event to mutiple schedules if this a group (scheduletype)

                //TODO: Add event to schedule within app
            }
        });
    }
}
