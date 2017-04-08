package com.example.jewel.test_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the view now
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Register.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Check for network Connection
                boolean networkConnection = DataManager.Instance().haveConnection(getApplicationContext());

                if (networkConnection == true) {
                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError("Password must be 6 characters long");
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        //Add Username
                                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                        String emailToL = email.toLowerCase();
                                        String username = emailToL.replaceAll("\\W", "");

                                        Firebase ref = new Firebase(Config.FIREBASE_URL);

                                        ref.child("UsersNameToID").child(username).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        ref.child("UsersIDToName").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(username);

                                        // pushed
                                        goToListView();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToListView(){
        Intent i = new Intent(this, EventListViewer.class);
        Bundle b = new Bundle();
        b.putString(DataManager.SCHEDULE_TYPE_KEY, "User");
        i.putExtras(b);
        startActivity(i);
    }
}

