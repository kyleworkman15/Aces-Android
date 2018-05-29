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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by meganjanssen on 4/13/18.
 * <p>
 * Displays the wait time, estimated time of arrival, ACES logo, and cancel button
 */

public class AfterRequestRideActivity extends AppCompatActivity {
    private static final String TAG = "After Ride Request";
    private TextView minutes;  // Displays the wait time in minutes
    private TextView ETA;       // Displays the estimated time the ride will arrive
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_request_ride);
        minutes = findViewById(R.id.wait_time);
        cancel = findViewById(R.id.cancelRide);
        ETA = findViewById(R.id.ETA);
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (minutes.getText().toString().equals("Estimated Wait Time: PENDING"))
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
        Log.d("ISER", email);
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String endTime = dataSnapshot.child("endTime").getValue().toString();
                    if (endTime.equals("Cancelled by Dispatcher")) {
                        Toast.makeText(AfterRequestRideActivity.this, "Requested ride cancelled by dispatcher", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
                    } else if (endTime.equals(" ")) {
                        int waitTime = Integer.parseInt(String.valueOf(dataSnapshot.child("waitTime").getValue()));
                        if (waitTime != 1000)
                            minutes.setText("Wait Time: " + waitTime + " minutes");

                        // if ETA has already been set, then set the text field
                        String checkETA = (String) dataSnapshot.child("eta").getValue();
                        if (!checkETA.equals(" ")) {
                            ETA.setText("ETA: " + checkETA);
                        }
                    } else {
                        Toast.makeText(AfterRequestRideActivity.this, "Thanks for using Aces!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES").child(userEmail);
        db.setValue(null);
        startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
    }

    /**
     * Deletes the ride from the active rides list in Firebase
     *
     * @param userEmail the string of the email that is signed in to A.C.E.S
     */
    public void deleteActiveRide(String userEmail) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES").child(userEmail);
        db.setValue(null);
        startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
    }


}
