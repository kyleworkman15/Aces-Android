package com.augustana.teamaardvark.acesaardvark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

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
