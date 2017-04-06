package com.example.jewel.test_project;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Jewel on 4/5/2017.
 */

public class BlockAdapter  extends ArrayAdapter<ScheduleBlock> {
    public BlockAdapter(Context context, int textViewResourceId, ScheduleBlock[] objects) {
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
