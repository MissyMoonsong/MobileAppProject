package com.example.jewel.test_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class TodaysEventsActivity extends AppCompatActivity implements View.OnClickListener  {
    //List of events that will be displayed
    private List<ScheduleBlock> blocks = new ArrayList<>();
    //List view to load events into
    private ListView listView;

    private String scheduleType = "", groupKey = "";
    private Schedule s;

    //for the menu
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event);

        Firebase.setAndroidContext(this);

        scheduleType = getIntent().getExtras().getString(DataManager.SCHEDULE_TYPE_KEY);

        if (scheduleType.equals("User")) {
            s = DataManager.Instance().getUser().getSchedule();
        } else if (scheduleType.equals("Group")) {
            groupKey = getIntent().getExtras().getString(DataManager.GROUP_ID_KEY);
            Group g = DataManager.Instance().getGroups().get(groupKey);
            if (g != null){
                s = g.getGroupSchedule();
            } else{
                Toast.makeText(getApplicationContext(), "Invalid Group", Toast.LENGTH_LONG).show();

                Intent i = new Intent(this, GroupMainPageActivity.class);
                startActivity(i);
            }
        }

        blocks = s.getBlocksForToday();

        fillList();

        //Set the listener for clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Find the string displayed with the clicked item
                String item = ((TextView) view).getText().toString();

                //Find out which event this block belongs to
                ScheduleEvent event = findItemByString(item).getParentEvent();

                //Send the eventID of the event to be viewed to the details view
                Bundle b = new Bundle();
                b.putString(DataManager.SCHEDULE_TYPE_KEY, scheduleType);
                b.putString(DataManager.GROUP_ID_KEY, groupKey);
                b.putString(DataManager.EVENT_ID_KEY,event.getEventID());

                toEventDetails(b);
            }
        });


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
    }

    //for menu
    private void addDrawerItems() {
        mAdapter = DrawerData.makeDrawerAdapter(this);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TodaysEventsActivity.this, DrawerData.classArray[position]);
                intent.putExtras(DrawerData.makeBundleForEventView());
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
        listView = (ListView) findViewById(R.id.block_list);

        //Note: Using a custom adapter class just to change the font color...
        ArrayAdapter<ScheduleBlock> adapter = new BlockAdapter(listView.getContext(),
                android.R.layout.simple_list_item_1, makeArray(blocks));

        listView.setAdapter(adapter);
    }

    //Helper method because the adapter needs an array of PasswordEntry, not array list
    private ScheduleBlock[] makeArray(List<ScheduleBlock> list) {
        ScheduleBlock[] array = new ScheduleBlock[list.size()];

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
    }

    @Override
    public void onClick(View v){
        //Nothing to do
    }

    public void toEventDetails(Bundle b) {
        Intent intent = new Intent(this, EventDetails.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    //Helper method to use displayed string to find the object
    private ScheduleBlock findItemByString(String itemToString) {
        ScheduleBlock result = null;

        for (ScheduleBlock b : blocks) {
            //Note: password's toString is what was displayed in the list
            if (b.toString().equalsIgnoreCase(itemToString)) {
                result = b;
                break;
            }
        }

        return result;
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
