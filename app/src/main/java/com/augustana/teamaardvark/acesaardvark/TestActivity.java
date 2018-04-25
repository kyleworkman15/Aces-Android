package com.augustana.teamaardvark.acesaardvark;

import android.content.Intent;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by kevinbarbian on 3/27/18.
 */

public class TestActivity extends AppCompatActivity {
    private Button logout;
    private FirebaseAuth.AuthStateListener authStateListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //auth = FirebaseAuth.getInstance();
        logout = (Button) findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Set up the login form.
    }

}
