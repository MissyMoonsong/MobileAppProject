package com.example.jewel.test_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        view = (TextView)findViewById(R.id.activity2_text);
        fillInfo();
    }

    //TODO: This is just a method to test calculations!!! Remove it later!!!
    private void fillInfo(){
        String text = "Results for the following schedule: \n";
        text += DataManager.Instance().getUser().getSchedule().toString();

        view.setText(text);
    }
}
