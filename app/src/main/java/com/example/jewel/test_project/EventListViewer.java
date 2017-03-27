package com.example.jewel.test_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Jewel on 3/26/2017.
 * This class is being adapted from work I did in a previous app,
 * which was designed to display passwords. Any leftover references
 * to passwords are accidental
 */

public class EventListViewer extends AppCompatActivity implements View.OnClickListener {

    //List of events that will be displayed
    private List<ScheduleEvent> events = new ArrayList<>();
    //List view to load events into
    private ListView listView;
    //Buttons
    Button btnManual, btnAuto;

    private String scheduleType = "", groupKey = "";
    private Schedule s;

    //for the menu
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    // Put this stuff wherever the shakes are used!
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_view);

        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);

        if (scheduleType.equals("User")) {
             s = DataManager.Instance().getUser().getSchedule();
            events = s.getAllEvents();
        } else if (scheduleType.equals("Group")) {
            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            s = DataManager.Instance().getGroups().get(groupKey).getGroupSchedule();
        }

        fillList();

        //Set the listener for clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Find the string displayed with the clicked item
                String item = ((TextView) view).getText().toString();

                //Find out which event this was based on the text
                ScheduleEvent event = findItemByString(item);

                //Send the eventID of the event to be viewed to the details view
                Bundle b = new Bundle();
                b.putString("ScheduleType", scheduleType);
                b.putString("ScheduleKey", groupKey);
                b.putString("EventID",event.getEventID());

                toEventDetails(b);
            }
        });

        btnAuto = (Button) findViewById(R.id.btn_auto_event);
        btnAuto.setOnClickListener(this);
        btnManual = (Button) findViewById(R.id.btn_manual_event);
        btnManual.setOnClickListener(this);

        //menu
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mDrawerList.bringToFront();
        mDrawerLayout.requestLayout();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

    //for menu
    private void addDrawerItems() {
        String[] navArray = {"Home", "Self", "Groups", "Friends", "About us"};
        final Class[] classArray = {Register.class, EventListViewer.class, GroupMainPageActivity.class, Register.class, Register.class};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EventListViewer.this, classArray[position]);
                Bundle b = new Bundle();
                b.putString("ScheduleType", "User");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    //for menu
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            //menu is open
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Main-Nav");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            //menu is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    private void fillList() {
        //Grab the list view
        listView = (ListView) findViewById(R.id.event_list);

        //Note: Using a custom adapter class just to change the font color...
        ArrayAdapter<ScheduleEvent> adapter = new EventAdapter(listView.getContext(),
                android.R.layout.simple_list_item_1, makeArray(events));

        listView.setAdapter(adapter);
    }

    //Helper method because the adapter needs an array of PasswordEntry, not array list
    private ScheduleEvent[] makeArray(List<ScheduleEvent> list) {
        ScheduleEvent[] array = new ScheduleEvent[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the list view
        fillList();

        //Put in the onResume of anything use the shake event!
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void toEventDetails(Bundle b) {
        Intent intent = new Intent(this, EventDetails.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void shakeResponse() {
        // Shaking generates an event in the next week or so
        String eventName = "Random Event";

        //Start of window: 3 days out
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 3);

        int startYear = now.get(Calendar.YEAR);
        int startMonth = now.get(Calendar.MONTH);
        int startDay = now.get(Calendar.DAY_OF_MONTH);

        //End of window: one after start
        now.add(Calendar.DAY_OF_MONTH, 7);

        int endYear = now.get(Calendar.YEAR);
        int endMonth = now.get(Calendar.MONTH);
        int endDay = now.get(Calendar.DAY_OF_MONTH);

        //Event duration: 1 hour
        int duration = 60;

        Calendar windowStart = new GregorianCalendar(startYear, startMonth, startDay);
        Calendar windowEnd = new GregorianCalendar(endYear, endMonth, endDay);

        //Generate an event this schedule
        ScheduleEvent event = s.findTimeInSchedule(windowStart, windowEnd, duration);

        if (event != null) { //Event Found
            event.changeName(eventName);

            //TODO: Add event to database -- connect to the right users! (also get an ID for the event)
            s.addEvent(event);
            if (scheduleType.equals("Group")) {
                DataManager.Instance().getGroups().get(groupKey).rebuildGroupSchedule();
                //TODO: add event to EACH MEMBER OF GROUP IN DATABASE
            }
        }

        fillList();
    }

    //Helper method to use displayed string to find the object
    private ScheduleEvent findItemByString(String itemToString) {
        ScheduleEvent result = null;

        for (ScheduleEvent e : events) {
            //Note: password's toString is what was displayed in the list
            if (e.toString().equalsIgnoreCase(itemToString)) {
                result = e;
                break;
            }
        }

        return result;
    }

    @Override
    public void onClick(View view) {
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
        b.putString(DataManager.GROUP_ID_KEY, groupKey);

        if (view == btnAuto) {
            Intent intent = new Intent(this, EventAutoCreate.class);
            intent.putExtras(b);
            startActivity(intent);

        } else if (view == btnManual) {
            Intent intent = new Intent(this, CreateEvent.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        //End activity if the user leaves the app
        super.onPause();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Put in the onStop of anything use the shake event!
        mSensorManager.unregisterListener(mShakeDetector);
    }

    //menu
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


