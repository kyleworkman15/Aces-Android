package com.augustana.teamaardvark.acesaardvark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class CommentsRating extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_rating);

        RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText mFeedback = (EditText) findViewById(R.id.editText2);
        Button mSendFeedback = (Button) findViewById(R.id.btnSubmit);
    }
}
