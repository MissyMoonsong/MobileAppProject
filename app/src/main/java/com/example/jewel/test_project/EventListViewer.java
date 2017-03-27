package com.example.jewel.test_project;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jewel on 3/26/2017.
 * This class is being adapted from work I did in a previous app,
 * which was designed to display passwords. Any leftover references
 * to passwords are accidental
 */

public class EventListViewer extends AppCompatActivity implements View.OnClickListener{

    //List of events that will be displayed
    List<ScheduleEvent> events = new ArrayList<>();
    //List view to load events into
    ListView listView;

    //Buttons
    Button btnManual, btnAuto;

    //for the menu
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_view);

        //TODO: PUT GETTING THE RIGHT SCHEDULE HERE
        String type = getIntent().getExtras().getString("ScheduleType");

        if(type.equals("User")){
            Schedule s = DataManager.Instance().getUser().getSchedule();
            events = s.getAllEvents();
        } else if (type.equals("Group")){
            String key = getIntent().getExtras().getString("ScheduleKey");
            Schedule s = DataManager.Instance().getGroups().get(key).getGroupSchedule();
        } else if (type.equals("Friend")){
            //TODO: Friend schedule
        }

        fillList();

        //Set the listener for clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Find the string displayed with the clicked item
                String item = ((TextView) view).getText().toString();

                //Find out which password this was based on the text
                ScheduleEvent event = findItemByString(item);

                //Send the eventID of the event to be viewed to the details view
                Bundle b = new Bundle();
                b.putInt("EventID", event.getEventID());

                goToEventDetails(b);
            }
        });

        btnAuto = (Button)findViewById(R.id.btn_auto_event);
        btnAuto.setOnClickListener(this);
        btnManual = (Button)findViewById(R.id.btn_manual_event);
        btnManual.setOnClickListener(this);

        //menu
        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    //for menu
    private void addDrawerItems() {
        String[] osArray = {"Home", "Self", "Groups", "Friends", "About us" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EventListViewer.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
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


    private void fillList(){
        //Grab the list view
        listView = (ListView) findViewById(R.id.event_list);

        //Note: Using a custom adapter class just to change the font color...
        ArrayAdapter<ScheduleEvent> adapter = new EventAdapter(listView.getContext(),
                android.R.layout.simple_list_item_1, makeArray(events));

        listView.setAdapter(adapter);
    }

    //Helper method because the adapter needs an array of PasswordEntry, not array list
    private ScheduleEvent[] makeArray(List<ScheduleEvent> list){
        ScheduleEvent[] array = new ScheduleEvent[list.size()];

        for(int i =0; i < list.size(); i++){
            array[i] = list.get(i);
        }

        return array;
    }

    @Override
    public void onResume(){
        super.onResume();
        //Refresh the list view
        fillList();
    }

    //Helper method to use displayed string to find the object
    private ScheduleEvent findItemByString(String itemToString){
        ScheduleEvent result = null;

        for(ScheduleEvent e : events){
            //Note: password's toString is what was displayed in the list
            if(e.toString().equalsIgnoreCase(itemToString)){
                result = e;
                break;
            }
        }

        return result;
    }

    //Bundle should have the 4 values for the detail view
    private void goToEventDetails(Bundle b){
        //TODO: make the view to see an individual event
        //Intent intent = new Intent(this, PasswordDetails.class);
        //intent.putExtras(b);
        //startActivity(intent);
    }

    @Override
    public void onClick(View view){
        if(view == btnAuto){
            //TODO: Go to Auto-generate event actviity
            //Intent intent = new Intent(this, PasswordDetails.class);
            //intent.putExtras(b);
            //startActivity(intent);

        } else if (view == btnManual) {
            //TODO: Go to manual-create even activity
            Intent intent = new Intent(this, CreateEvent.class);
            //intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public void onPause(){
        //End activity if the user leaves the app
        super.onPause();
        finish();
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


