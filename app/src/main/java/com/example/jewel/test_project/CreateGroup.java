package com.example.jewel.test_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

            //TODO: ADDED NETWORK CONNECTIOn CHECK
            //Check for network Connection
            boolean networkConnection = DataManager.Instance().haveConnection(getApplicationContext());

            if (networkConnection == true) {
                DataManager.Instance().createGroupAndAddUser(name, ref);
                Toast.makeText(getApplicationContext(), "Group Successfully Created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
            }
        }

        Intent intent = new Intent(this, GroupMainPageActivity.class);
        startActivity(intent);
    }
}
