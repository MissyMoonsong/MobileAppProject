package com.example.jewel.test_project;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jewel on 3/26/2017.
 * This class is being adapted from work I did in a previous app,
 * which was designed to display passwords. Any leftover references
 * to passwords are accidental
 */

public class EventListViewer extends AppCompatActivity {
    //List of events that will be displayed
    List<ScheduleEvent> events = new ArrayList<>();
    //List view to load events into
    ListView listView;

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
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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
        //New Password button
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       // fab.setOnClickListener(new View.OnClickListener() {
          //  @Override
           // public void onClick(View view) {
           //     goToEventCreator();
          //  }
       // });
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

    private void goToEventCreator(){
        //Intent intent = new Intent(this, CreatePassword.class);
        //startActivity(intent);
    }
    @Override
    public void onPause(){
        //End activity if the user leaves the app
        super.onPause();
        finish();
    }
}
