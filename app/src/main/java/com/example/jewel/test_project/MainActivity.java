package com.example.jewel.test_project;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Put this stuff wherever the shakes are used!
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate() called");

        Button mButton = (Button) findViewById(R.id.button);
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

                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);
            }
        });


        //Put this in onCreate in any activity needing the shake event!
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				shakeResponse();
            }
        });
    }

    private void shakeResponse(){
        // TODO: The stuff!
        TextView v = (TextView)findViewById(R.id.main_text);
        v.setText("You shook me!");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        //Put in the onResume of anything use the shake event!
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        //Put in the onStop of anything use the shake event!
        mSensorManager.unregisterListener(mShakeDetector);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
