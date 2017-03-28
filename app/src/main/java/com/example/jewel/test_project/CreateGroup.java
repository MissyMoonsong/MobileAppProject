package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

public class CreateGroup extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    EditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Firebase.setAndroidContext(this);

        createButton = (Button)findViewById(R.id.btn_create_group);
        createButton.setOnClickListener(this);

        nameText = (EditText)findViewById(R.id.txt_create_group_name);
    }

    @Override
    public void onClick(View view){
        String name = nameText.getText().toString();
        if(name.length() > 0){
            //Creating firebase object
            Firebase ref = new Firebase(Config.FIREBASE_URL);
            DataManager.Instance().createGroupAndAddUser(name, ref);
        }

        Intent intent = new Intent(this, GroupMainPageActivity.class);
        startActivity(intent);
    }
}
