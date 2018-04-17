package com.augustana.teamaardvark.acesaardvark;

import android.media.MediaDrm;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;

/**
 * Created by kevinbarbian on 4/17/18.
 */

public class waitTimeEventListener implements ValueEventListener {
    private DatabaseReference database;
    private RideInfo currRide;
    private TextView waitTime;
    private Timestamp ts;

    public waitTimeEventListener(DatabaseReference database, RideInfo ride, TextView field, Timestamp ts){
        this.database=database;
        currRide=ride;
        this.waitTime = field;
        this.ts =ts;
    }

    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d("MSG_EVENT",ts.toString());
        Timestamp ts1 = new Timestamp(System.currentTimeMillis()-1000);
        Log.d("MSG_TS1",ts1.toString());
        if (ts1.after(ts)) {
            Log.d("SUCCESS", "YA");
            waitTime.setText("APPROVED, Wait Time: " + String.valueOf(dataSnapshot.getValue()));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
