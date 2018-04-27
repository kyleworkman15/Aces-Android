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

/**
 * Created by meganjanssen on 4/13/18.
 */

public class AfterRequestRideActivity extends AppCompatActivity {
    private static final String TAG = "After Ride Request";
    private TextView minutes;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_request_ride);
        minutes = findViewById(R.id.wait_time);
        cancel = findViewById(R.id.cancelRide);
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".",",");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("CURRENT RIDES").child(userEmail);
                Log.d("MSG",userEmail);
                db.setValue(null);
                if (minutes.getText().toString().equals("Wait Time: PENDING"))
                    startActivity(new Intent(AfterRequestRideActivity.this, GoogleMapsActivity.class));
                else {
                    Toast toast = Toast.makeText(getBaseContext(), "Cannot cancel an active ride", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".",",");
        DatabaseReference checkUser = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES")
                .child(email).child("waitTime");
        Log.d("ISER",email);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (Integer.parseInt(String.valueOf(dataSnapshot.getValue())) != 1000)
                        minutes.setText("Wait Time: " + String.valueOf(dataSnapshot.getValue()) + " minutes");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
    @Override
    public void onBackPressed() { }

}
