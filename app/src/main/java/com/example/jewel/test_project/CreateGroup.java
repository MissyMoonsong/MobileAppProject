package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        createButton = (Button)findViewById(R.id.btn_create_group);
        createButton.setOnClickListener(this);

        nameText = (EditText)findViewById(R.id.txt_create_group_name);
    }

    @Override
    public void onClick(View view){
        String name = nameText.getText().toString();
        if(name.length() > 0){
            Group g = new Group(name, DataManager.Instance().getNextGroupID());
            g.addMember(DataManager.Instance().getUser());
            //TODO: DATABASE THING HERE -- Use the GROUP ID for the key below
            DataManager.Instance().getGroups().put(g.getGroupID(), g);
        }

        Intent intent = new Intent(this, GroupMainPageActivity.class);
        startActivity(intent);
    }
}
