package edu.augustana.aces;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Kyle Workman, Kevin Barbian, Megan Janssen, Tan Nguyen, Tyler May
 *
 * Handles the display of the about page including A.C.E.S purpose, the hours that A.C.E.S is available the phone number to call A.C.E.S if necessary and credits the developers,
 * creates a link to the A.C.E.S phone number in case the user wants to call
 */
public class AboutPageActivity extends AppCompatActivity {

//    @Override
//    protected void onCreateView(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about_page);
//
//        //Link for phone number
//        TextView phoneNumber = (TextView) findViewById(R.id.phone_text);
//        Linkify.addLinks(phoneNumber, Linkify.ALL);
//
//        Button backBtn = (Button) findViewById(R.id.back_btn);
//        backBtn.setOnClickListener(back);
//    }

    private final Button.OnClickListener back = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
