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

import java.util.Calendar;

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

        //Setting all the view variables
        setViews();

        Firebase.setAndroidContext(this);

        create_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Creating firebase object
                Firebase ref = new Firebase(Config.FIREBASE_URL);

                Event event = fillEvent();

                //Store values to firebase
                ref.child("Event").setValue(event);

                if (scheduleType.equals("Group")) {
                    Group g = DataManager.Instance().getGroups().get(groupKey);
                    for (Person p : g.getMembers()) {
                        String userID = p.getUserID();
                        //TODO: Database stuff: connect to this person's user id if not already
                    }
                } else {
                    //TODO: Was a single user schedule
                }

                //Add event to app -- no DB variant
                ScheduleEvent se = buildScheduleEventFromEvent(event);
                se.setEventID(DataManager.Instance().getNextEventID());
                schedule.addEvent(se);

                //Go to list again
                returnToList();
            }
        });
    }


    private void setViews(){
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
    }

    private Event fillEvent(){
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

        return event;
    }

    private void returnToList(){
        //Go back to ListView
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
        b.putString(DataManager.GROUP_ID_KEY, groupKey);

        Intent intent = new Intent(this, EventListViewer.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public ScheduleEvent buildScheduleEventFromEvent(Event ev) {
        String eName = ev.getEventName();

        int sYear = ev.getStartYear();
        int sMonth = ev.getStartMonth();
        int sDay = ev.getStartDay();
        int sHour = ev.getStartHour();
        int sMin = ev.getStartMin();

        int eYear = ev.getEndYear();
        int eMonth = ev.getEndMonth();
        int eDay = ev.getEndDay();
        int eHour = ev.getEndHour();
        int eMin = ev.getEndMin();

        boolean sun = ev.getRSunday();
        boolean mon = ev.getRMonday();
        boolean tue = ev.getRTuesday();
        boolean wed = ev.getRWednesday();
        boolean thu = ev.getRThursday();
        boolean fri = ev.getRFriday();
        boolean sat = ev.getRSaturday();

        //Adding to the schedule within the app
        Calendar start = Calendar.getInstance();

        start.set(Calendar.YEAR, sYear);
        start.set(Calendar.MONTH, sMonth);
        start.set(Calendar.DAY_OF_MONTH, sDay);
        start.set(Calendar.HOUR_OF_DAY, sHour);
        start.set(Calendar.MINUTE, sMin);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, eYear);
        end.set(Calendar.MONTH, eMonth);
        end.set(Calendar.DAY_OF_MONTH, eDay);
        end.set(Calendar.HOUR_OF_DAY, eHour);
        end.set(Calendar.MINUTE, eMin);

        boolean[] weekdays = new boolean[7];
        weekdays[0] = sun;
        weekdays[1] = mon;
        weekdays[2] = tue;
        weekdays[3] = wed;
        weekdays[4] = thu;
        weekdays[5] = fri;
        weekdays[6] = sat;


        ScheduleEvent e;

        if (ScheduleEvent.anyDaySelected(weekdays)) { //Recurring
            e = new ScheduleEvent(eName, start, end, weekdays);

        } else {
            e = new ScheduleEvent(eName, start, end);
        }

        return e;
    }
}
