package com.augustana.teamaardvark.acesaardvark;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "Google Maps";
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    private Button request_btn;
    private DatabaseReference mDatabase;
    LatLngBounds augustanaBounds;
    MarkerOptions mo;
    Marker marker1;
    Marker marker2;
    List<Address> addressesFrom;
    List<Address> addressesTo;
    LocationManager locationManager;
    AutoCompleteTextView startAutoComplete;
    AutoCompleteTextView endAutoComplete;
    EditText numRiders;
    LocationDatabase locationDatabase;
    Geocoder geocoder;
    private EditText riders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        riders = findViewById(R.id.editTextNumRiders);
        request_btn = findViewById(R.id.request_ride_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "END AUTO COMPLETE: "+endAutoComplete.getText() + "");
                if(isStartEndNumFilledOut()) {
                    try {
                        addressesFrom = geocoder.getFromLocation(marker1.getPosition().latitude, marker1.getPosition().longitude, 1);
                        addressesTo = geocoder.getFromLocation(marker2.getPosition().latitude, marker2.getPosition().longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String email = (String) FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
                    int numRiders = Integer.parseInt(riders.getText().toString());
                    String addressFrom = addressesFrom.get(0).getAddressLine(0);
//                String addressFromClean = addressFrom.substring(0, addressFrom.indexOf(','));
                    String addressTo = addressesTo.get(0).getAddressLine(0);
                    //               String addressToClean = addressTo.substring(0, addressTo.indexOf(','));
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
                    RideInfo rider = new RideInfo(email, addressFrom, addressTo, numRiders, time);
                    mDatabase.child(email).setValue(rider);
                    Log.d(TAG, "Ride Submitted");
                    startActivity(new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class));
                }
            }
        });


        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);

        autoCompleteSetUp();
        numRidersSetUp();

    }

    public boolean isStartEndNumFilledOut(){
        if(startAutoComplete.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getBaseContext(), "Please fill out Start Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Log.d(TAG, "Start not filled");
            return false;
        }
        if(endAutoComplete.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getBaseContext(), "Please fill out End Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "End not filled");
            return false;
        }
        if(numRiders.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getBaseContext(), "Please fill out Number Of Riders", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "Num not filled");
            return false;
        }
        return true;
    }

    public void numRidersSetUp(){
        numRiders = (EditText) findViewById(R.id.editTextNumRiders);
        numRiders.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && Integer.parseInt(s.toString()) == 0){
                    Toast toast = Toast.makeText(getBaseContext(), "Need 1 or more rider(s) to request a ride", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    numRiders.setText("1");
                }
                if(s.length()>0 && Integer.parseInt(s.toString()) > 7){
                    Toast toast = Toast.makeText(getBaseContext(), "Only 7 riders can ride in ACES at a time", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    numRiders.setText("");
                }
            }
        });
    }

    public void autoCompleteSetUp(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, locationDatabase.locations);
        startAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_Start);
        endAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_End);
        startAutoComplete.setAdapter(adapter);
        startAutoComplete.setText(locationDatabase.locations[0]);
        startAutoComplete.dismissDropDown();
        endAutoComplete.setAdapter(adapter);

        //When user clicks on option from drop down from Start
        startAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                int indexStart = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
                makeMarkerStart(indexStart);
                hideKeyboard(GoogleMapsActivity.this);
            }
        });

        //When user clicks on option from drop down from End
        endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                int indexEnd = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
                makeMarkerEnd(indexEnd);
                hideKeyboard(GoogleMapsActivity.this);
            }
        });

        //When user clicks on actual field
        startAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutoComplete.setText("");
            }
        });

    }

    public void makeMarkerStart(int indexOfLocation) {
        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
        marker1.setPosition(chosenCoordinates);
        marker1.setTitle(locationDatabase.locations[indexOfLocation]);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenCoordinates,15));
    }

    public void makeMarkerEnd(int indexOfLocation) {
        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
        marker2.setVisible(true);
        marker2.setPosition(chosenCoordinates);
        marker2.setTitle(locationDatabase.locations[indexOfLocation]);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenCoordinates,15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng chosenCoordinates = new LatLng(41.505199, -90.550674); AUGUSTANA COORDINATES
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        locationDatabase.setCurrentLatitude(currentLatLng.latitude);
        locationDatabase.setCurrentLongitude(currentLatLng.longitude);
        marker1 =  mMap.addMarker(new MarkerOptions().position(currentLatLng).title(locationDatabase.locations[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        marker2 = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false)); // TODO: Refactor bad code here
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));




//        augustanaBounds = new LatLngBounds(new LatLng(41.497304, -90.546406), new LatLng(41.507601, -90.556957));
//        mMap.setLatLngBoundsForCameraTarget(augustanaBounds);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            ActivityCompat.requestPermissions(GoogleMapsActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(GoogleMapsActivity.this,Google_SignIn.class));     }

}
