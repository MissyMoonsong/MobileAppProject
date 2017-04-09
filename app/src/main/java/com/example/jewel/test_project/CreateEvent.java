package com.example.jewel.test_project;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;

import java.util.Calendar;

/**
 * Created by allis on 3/26/2017.
 */

public class CreateEvent extends AppCompatActivity {
    private String scheduleType, groupKey;
    private Schedule schedule;

    //Variables For Date
    final int DATE_PICKER_BEGIN = 0;
    final int DATE_PICKER_END = 1;
    private Calendar calendar;
    private Button button_beginDate, button_endDate;
    private int b_year, b_month, b_day;
    private int e_year, e_month, e_day;

    //Variables For Time
    final int TIME_PICKER_BEGIN = 2;
    final int TIME_PICKER_END = 3;
    private Button button_beginTime, button_endTime;
    private String format = "";
    private int b_hour, b_min;
    private int e_hour, e_min;

    //Variables For Other
    private EditText event_name;
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
        setViewsDate();
        setViewsTime();
        setViewsOther();

        Firebase.setAndroidContext(this);

        create_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Creating firebase object
                Firebase ref = new Firebase(Config.FIREBASE_URL);

                DatabaseEvent event = fillEvent();

                //Check for network Connection
                boolean networkConnection = DataManager.Instance().haveConnection(getApplicationContext());

                if (networkConnection == true) {
                    //Add event to app -- no DB variant
                    ScheduleEvent se = DataManager.buildScheduleEventFromEvent(event);
                    DataManager.Instance().addUnpublishedEvent(se, scheduleType, groupKey, ref);
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                }

                //Go to list again
                returnToList();
            }
        });
    }

    private void setViewsDate() {
        button_beginDate = (Button) findViewById(R.id.button_beginDate);
        button_endDate = (Button) findViewById(R.id.button_endDate);
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

    private void setViewsTime() {
        button_beginTime = (Button) findViewById(R.id.button_beginTime);
        button_endTime = (Button) findViewById(R.id.button_endTime);
        b_hour = calendar.get(Calendar.HOUR_OF_DAY);
        b_min = calendar.get(Calendar.MINUTE);
        e_hour = b_hour + 1;
        e_min = calendar.get(Calendar.MINUTE);
        showTimeBegin(b_hour, b_min);
        showTimeEnd(e_hour, e_min);
    }

    private void setViewsOther() {
        //Initialize Views
        event_name = (EditText) findViewById(R.id.event_name);
        r_sunday = (ToggleButton) findViewById(R.id.r_sunday);
        r_monday = (ToggleButton) findViewById(R.id.r_monday);
        r_tuesday = (ToggleButton) findViewById(R.id.r_tuesday);
        r_wednesday = (ToggleButton) findViewById(R.id.r_wednesday);
        r_thursday = (ToggleButton) findViewById(R.id.r_thursday);
        r_friday = (ToggleButton) findViewById(R.id.r_friday);
        r_saturday = (ToggleButton) findViewById(R.id.r_saturday);
        create_event = (Button) findViewById(R.id.button_create_event);
    }

    @SuppressWarnings("deprecation")
    public void setBeginDate(View view) {
        showDialog(DATE_PICKER_BEGIN);
    }

    @SuppressWarnings("deprecation")
    public void setEndDate(View view) {
        showDialog(DATE_PICKER_END);
    }

    public void setBeginTime(View view) {
        showDialog(TIME_PICKER_BEGIN);
    }

    public void setEndTime(View view) {
        showDialog(TIME_PICKER_END);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            //For Date
            case DATE_PICKER_BEGIN:
                return new DatePickerDialog(this, beginDateListener, b_year, b_month, b_day);
            case DATE_PICKER_END:
                return new DatePickerDialog(this, endDateListener, e_year, e_month, e_day);
            //For Time
            case TIME_PICKER_BEGIN:
                return new TimePickerDialog(this, beginTimeListener, b_hour, b_min, false);
            case TIME_PICKER_END:
                return new TimePickerDialog(this, endTimeListener, e_hour, e_min, false);
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

    //For Time
    private TimePickerDialog.OnTimeSetListener beginTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0,
                              int arg1, int arg2) {
            // arg1 = hour
            b_hour = arg1;
            // arg2 = min
            b_min = arg2;
            showTimeBegin(arg1, arg2);
        }
    };
    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0,
                              int arg1, int arg2) {
            // arg1 = hour
            e_hour = arg1;
            // arg2 = min
            e_min = arg2;
            showTimeEnd(arg1, arg2);
        }
    };

    //For Date
    private void showDateBegin(int month, int day, int year) {
        button_beginDate.setText(new StringBuilder().append(month).append("/").append(day).append("/").append(year));
    }

    private void showDateEnd(int month, int day, int year) {
        button_endDate.setText(new StringBuilder().append(month).append("/").append(day).append("/").append(year));
    }

    //For Time
    private void showTimeBegin(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (min < 10) {
            button_beginTime.setText(new StringBuilder().append(hour).append(":").append("0").append(min).append(" ").append(format));
        } else {
            button_beginTime.setText(new StringBuilder().append(hour).append(":").append(min).append(" ").append(format));
        }
    }

    private void showTimeEnd(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (min < 10) {
            button_endTime.setText(new StringBuilder().append(hour).append(":").append("0").append(min).append(" ").append(format));
        } else {
            button_endTime.setText(new StringBuilder().append(hour).append(":").append(min).append(" ").append(format));
        }
    }

    private DatabaseEvent fillEvent() {
        //Getting values to store
        String evname = event_name.getText().toString().trim();
        Integer shour = b_hour;
        Integer smin = b_min;
        Integer ehour = e_hour;
        Integer emin = e_min;
        Integer smonth = b_month;
        Integer sday = b_day;
        Integer syear = b_year;
        Integer emonth = e_month;
        Integer eday = e_day;
        Integer eyear = e_year;
        Boolean iSun = r_sunday.isChecked();
        Boolean iMon = r_monday.isChecked();
        Boolean iTue = r_tuesday.isChecked();
        Boolean iWed = r_wednesday.isChecked();
        Boolean iThur = r_thursday.isChecked();
        Boolean iFri = r_friday.isChecked();
        Boolean iSat = r_saturday.isChecked();

        //Create Event Object
        DatabaseEvent event = new DatabaseEvent();

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
