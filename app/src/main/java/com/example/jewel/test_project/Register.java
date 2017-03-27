package com.example.jewel.test_project;

import android.content.res.Configuration;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import android.view.Menu;
import android.view.MenuItem;

public class Register extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private Button buttonSwitchLogin;
    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    //for the menu
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        buttonSwitchLogin = (Button) findViewById(R.id.buttonSwitchToLogin);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        buttonSwitchLogin.setOnClickListener(this);


        //menu

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerList.bringToFront();
        mDrawerLayout.requestLayout();
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void addDrawerItems() {
        String[] navArray = {"Home", "Self", "Groups", "Friends", "About us" };
        final Class[] classArray = {Register.class, EventListViewer.class,GroupMainPageActivity.class, Register.class,Register.class};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Register.this, classArray[position]);
                Bundle b = new Bundle();
                b.putString("ScheduleType", "User");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            //menu is open
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.nav_title);
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

    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(Register.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(Register.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignup) {
            //calling register method on click
            registerUser();

            Intent i = new Intent(this, EventListViewer.class);
            Bundle b = new Bundle();
            b.putString("ScheduleType", "User");
            i.putExtras(b);
            startActivity(i);
        } else if (view == buttonSwitchLogin) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

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