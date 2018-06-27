package com.augustana.teamaardvark.acesaardvark;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
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

        Button backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(back);
    }

    private Button.OnClickListener back = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
