package edu.augustana.aces;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by meganjanssen on 4/13/18.
 * <p>
 * Displays the wait time, estimated time of arrival, ACES logo, and cancel button
 */

public class AfterRequestRideActivity extends AppCompatActivity implements Serializable {
    private static final String TAG = "After Ride Request";
    private TextView data;  // Displays the data
    private Button cancel;
    public static final String PREFS = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_request_ride);
        data = findViewById(R.id.data);
        cancel = findViewById(R.id.cancelRide);
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm(data, userEmail);
            }
        });
        final RideInfo ride = (RideInfo) getIntent().getSerializableExtra("user");
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");
        DatabaseReference checkUserActive = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES")
                .child(email);
        DatabaseReference checkUserPending = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES")
                .child(email);
        DatabaseReference checkUserCancelled = FirebaseDatabase.getInstance().getReference().child("CANCELLED RIDES").child(ride.getEmail() + "_" + ride.getTimestamp());
        DatabaseReference checkUserCompleted = FirebaseDatabase.getInstance().getReference().child("COMPLETED RIDES").child(ride.getEmail() + "_" + ride.getTimestamp());
        Log.d("ISER", email);
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String endTime = dataSnapshot.child("endTime").getValue().toString();
                     if (endTime.equals(" ")) {
                        String waitTime = dataSnapshot.child("waitTime").getValue().toString();
                        String eta = dataSnapshot.child("eta").getValue().toString();
                        if (waitTime.equals("1000") && eta.equals(" ")) {
                            data.setText("Start: " + ride.getStart() + "\nEnd: " + ride.getEnd() + "\nETA: PENDING");
                        } else {
                            data.setText("Start: " + ride.getStart() + "\nEnd: " + ride.getEnd() + "\nETA: " + eta);
                            ride.setWaitTime(waitTime);
                            ride.setETA(eta);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        };
        checkUserActive.addValueEventListener(vel);
        checkUserPending.addValueEventListener(vel);
        checkUserCancelled.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String endTime = dataSnapshot.child("endTime").getValue().toString();
                    if (endTime.equals("Cancelled by Dispatcher")) {
                        deleteTS();
                        ride.setEndTime("Cancelled by Dispatcher");
                        Intent returnInent = new Intent().putExtra("result", "cancelled");
                        setResult(RESULT_OK, returnInent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        checkUserCompleted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    deleteTS();
                    Toast toast = Toast.makeText(AfterRequestRideActivity.this, "Thanks for using Aces!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent returnInent = new Intent().putExtra("result", "completed");
                    setResult(RESULT_OK, returnInent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        outputTS();
    }

    /**
     * Overriding the back pressed to do nothing, ensures user is on the After Request Ride Activity to see wait time
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Deletes the ride from the pending rides list in Firebase
     *
     * @param userEmail the string of the email that is signed in to A.C.E.S
     */
    public void deletePendingRide(String userEmail) {
        deleteRide(userEmail, "PENDING RIDES");
    }

    /**
     * Deletes the ride from the active rides list in Firebase
     *
     * @param userEmail the string of the email that is signed in to A.C.E.S
     */
    public void deleteActiveRide(String userEmail) {
        deleteRide(userEmail, "ACTIVE RIDES");
    }

    /**
     * Deletes the ride from the given list and sets the end time to "Cancelled by User"
     * @param userEmail the string of the email that is signed in to A.C.E.S
     * @param type the string of the type of ride being deleted
     */
    public void deleteRide(String userEmail, String type) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference cancelled = db.child("CANCELLED RIDES");
        final DatabaseReference ref = db.child(type).child(userEmail);
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RideInfo user = dataSnapshot.getValue(RideInfo.class);
                String emailTS = user.getEmail() + "_" + user.getTimestamp();
                user.setEndTime("Cancelled by User");
                cancelled.child(emailTS).setValue(user);
                ref.setValue(new RideInfo(user.getEmail(), "", "Cancelled by User", "", "", "", "", "", "", ""));
                ref.setValue(null);
                Intent returnInent = new Intent().putExtra("result", "user_cancelled");
                setResult(RESULT_OK, returnInent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(vel);
    }

    public void confirm(TextView data, String userEmail) {
        final TextView data2 = data;
        final String userEmail2 = userEmail;
        new AlertDialog.Builder(this)
                .setTitle("Cancel Ride")
                .setMessage("Are you sure you want to cancel your ride?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (data2.getText().toString().contains("PENDING"))
                            deletePendingRide(userEmail2);
                        else {
                            deleteActiveRide(userEmail2);
                        }
                        deleteTS();
                    }})
                .setNegativeButton("No", null).show();
    }

    public void outputTS() {
        final RideInfo ride = (RideInfo) getIntent().getSerializableExtra("user");
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("timestamp", ride.getTimestamp());
        editor.commit();
    }

    public void deleteTS() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}
