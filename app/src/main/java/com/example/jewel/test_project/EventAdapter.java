package com.example.jewel.test_project;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Jewel on 3/26/2017.
 * This class is being adapted from work I did in a previous app,
 * which was designed to display passwords. Any leftover references
 * to passwords are accidental
 */

public class EventAdapter extends ArrayAdapter<ScheduleEvent> {

    public EventAdapter(Context context, int textViewResourceId, ScheduleEvent[] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        //The following code can be used to change the text color if we want a theme in the future
        //TextView tv = (TextView) view.findViewById(android.R.id.text1);
        //Changing the text color
        //tv.setTextColor(Color.argb(255,255, 255,255));

        return view;
    }
}
