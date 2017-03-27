package com.example.jewel.test_project;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by Jewel on 3/27/2017.
 */

public class DrawerData {
    public static final String[] navArray = {"Home", "Self", "Groups", "About us"};
    public static final Class[] classArray = {LoginActivity.class, EventListViewer.class, GroupMainPageActivity.class, AboutUs.class};

    private DrawerData(){

    }

    public static ArrayAdapter<String> makeDrawerAdapter(Context c){
        return new ArrayAdapter<>(c, android.R.layout.simple_list_item_1, navArray);
    }

    public static Bundle makeBundleForEventView(){
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, "User");

        return b;
    }

}
