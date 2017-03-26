package com.example.jewel.test_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class CreateLoginActivity extends AppCompatActivity {
    private ScrollView view;
    private static final String TAG = "CreateLoginActivity";
    EditText ed1,ed2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_login);

        view = (ScrollView)findViewById(R.id.login_create_form);
        fillInfo();

        ed1 = (EditText)findViewById(R.id.email);
        ed2 = (EditText)findViewById(R.id.password);
        Button mButton = (Button) findViewById(R.id.button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Note: This stuff is testing event creation. After the click, results are shown.
                Calendar start = new GregorianCalendar(2017, 3, 10);
                Calendar end = new GregorianCalendar(2017, 3, 17);

                ScheduleEvent event =
                        DataManager.Instance().getUser().getSchedule().findTimeInSchedule(start, end, 60);

                if(event != null){
                    event.changeName("New Event");
                    DataManager.Instance().getUser().getSchedule().addEvent(event);
                }
                else{
                    Log.d(TAG, "Null event returned");
                }

                //set login info from the edit text things

                //in regular login use
                //if(ed1.getText().toString().equals("database value") &&
                //      ed2.getText().toString().equals("database value")) {
                //to check
                Intent i = new Intent(CreateLoginActivity.this, Main2Activity.class);
                startActivity(i);
            }
        });

    }

    //TODO: This is just a method to test calculations!!! Remove it later!!!
    private void fillInfo(){
        String text = "Results for the following schedule: \n";
        text += DataManager.Instance().getUser().getSchedule().toString();

       // view.setText(text);
    }
}
