package com.augustana.teamaardvark.acesaardvark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by meganjanssen on 4/13/18.
 * <p>
 * Displays the wait time, estimated time of arrival, ACES logo, and cancel button
 */

public class AfterRequestRideActivity extends AppCompatActivity implements Serializable {
    private static final String TAG = "After Ride Request";
    private TextView data;  // Displays the data
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_request_ride);
        data = findViewById(R.id.data);
        cancel = findViewById(R.id.cancelRide);
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");
        final RideInfo ride = (RideInfo) getIntent().getSerializableExtra("user");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getText().toString().contains("PENDING"))
                    deletePendingRide(userEmail);
                else {
                    deleteActiveRide(userEmail);
                }

            }
        });

        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");
        DatabaseReference checkUserActive = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES")
                .child(email);
        DatabaseReference checkUserPending = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES")
                .child(email);
        DatabaseReference checkUserCancelled = FirebaseDatabase.getInstance().getReference().child("CANCELLED RIDES");
        DatabaseReference checkUserCompleted = FirebaseDatabase.getInstance().getReference().child("COMPLETED RIDES");
        Log.d("ISER", email);
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String endTime = dataSnapshot.child("endTime").getValue().toString();
                     if (endTime.equals(" ")) {
                        String waitTime = dataSnapshot.child("waitTime").getValue().toString();
                        String eta = dataSnapshot.child("eta").getValue().toString();
                        if (waitTime.equals("1000")) {
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
                if (dataSnapshot.hasChild(ride.getEmail() + "_" + ride.getTimestamp())) {
                    String endTime = dataSnapshot.child(ride.getEmail() + "_" + ride.getTimestamp()).child("endTime").getValue().toString();
                    if (endTime.equals("Cancelled by Dispatcher")) {
                        Toast toast = Toast.makeText(AfterRequestRideActivity.this, "Requested ride cancelled by dispatcher", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
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
                if (dataSnapshot.hasChild(ride.getEmail() + "_" + ride.getTimestamp())) {
                    Toast toast = Toast.makeText(AfterRequestRideActivity.this, "Thanks for using Aces!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
                ref.setValue(null);
                startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(vel);
    }
}
