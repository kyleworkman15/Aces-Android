package com.augustana.teamaardvark.acesaardvark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Handles the display of the about page including A.C.E.S purpose, the hours that A.C.E.S is available the phone number to call A.C.E.S if necessary and credits the developers,
 * creates a link to the A.C.E.S phone number in case the user wants to call
 */
public class AboutPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        //Link for phone number
        TextView phoneNumber = (TextView) findViewById(R.id.phone_text);
        Linkify.addLinks(phoneNumber, Linkify.ALL);
    }
}
