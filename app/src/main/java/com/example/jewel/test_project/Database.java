package com.example.jewel.test_project;

import com.firebase.client.Firebase;

/**
 * Created by allis on 3/25/2017.
 */

public class Database extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
