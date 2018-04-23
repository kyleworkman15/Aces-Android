package com.augustana.teamaardvark.acesaardvark;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "Google Maps";
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    private Button request_btn;
    private DatabaseReference mDatabase;
    private TextView pTime;
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

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    PlaceAutoComplete mPlaceArrayAdapterStart;
    PlaceAutoComplete mPlaceArrayAdapterEnd;


    //  new LatLng(41.497281, -90.539093),  new LatLng(41.507930, -90.565683));
    //private static final LatLngBounds AUGUSTANA_VIEW = new LatLngBounds(
            //new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private static final LatLngBounds AUGUSTANA_VIEW = new LatLngBounds(
            new LatLng(41.497281, -90.565683), new LatLng(41.507930, -90.539093));

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        riders = findViewById(R.id.editTextNumRiders);
        request_btn = findViewById(R.id.request_ride_btn);
        pTime = findViewById(R.id.pendingTime);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("CURRENT RIDES");
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestButtonClick(v);
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

    public void handleRequestButtonClick(View v){
        String addressFrom;
        String addressTo;
        addressFrom = "Null";
        Log.d(TAG, "END AUTO COMPLETE: "+endAutoComplete.getText() + "");
        if(isStartEndNumFilledOut()) {
            if(startAutoComplete.getText().toString().equals("Current Location")){
                try {
                    addressesFrom = geocoder.getFromLocation(marker1.getPosition().latitude, marker1.getPosition().longitude, 1);
                    addressFrom = addressesFrom.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                addressFrom = startAutoComplete.getText().toString();
            }
            addressTo = endAutoComplete.getText().toString();
            String email = (String) FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
            int rideNum = Integer.parseInt(riders.getText().toString());
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
            final RideInfo rider = new RideInfo(email, addressFrom, addressTo, rideNum, time,1000);
            mDatabase.child(email).setValue(rider);
//            waitTimeEventListener wt = new waitTimeEventListener(mDatabase,rider,pTime,ts);
            DatabaseReference checkChange = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES")
                    .child(rider.getEmail()).child("waitTime");
//            checkChange.addValueEventListener(wt);
            checkChange.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (Integer.parseInt(String.valueOf(dataSnapshot.getValue())) != 1000)
                            pTime.setText("APPROVED Wait Time: " + Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.d("MSG_CLASS",ts.toString());
            //mDatabase.child(email).child("waitTime").addValueEventListener(wt);
            Log.d(TAG, "Ride Submitted");
            startActivity(new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class));
        }
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
        mPlaceArrayAdapterStart = new PlaceAutoComplete(this, android.R.layout.simple_list_item_1,
                AUGUSTANA_VIEW, null);
        mPlaceArrayAdapterEnd = new PlaceAutoComplete(this, android.R.layout.simple_list_item_1,
                AUGUSTANA_VIEW, null);

        //Make the drawable for the Start AutoComplete
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(5);
        gd.setStroke(2, 0xFF000000);

        startAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_Start);
        startAutoComplete.setBackground(gd);
        startAutoComplete.setOnItemClickListener(mAutocompleteClickListenerStart);
        startAutoComplete.setAdapter(mPlaceArrayAdapterStart);

        endAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_End);
        endAutoComplete.setBackground(gd);
        endAutoComplete.setOnItemClickListener(mAutocompleteClickListenerEnd);
        endAutoComplete.setAdapter(mPlaceArrayAdapterEnd);



//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, locationDatabase.locations);
//        startAutoComplete = (AutoCompleteTextView)
//                findViewById(R.id.autoCompleteTextView_Start);
//        endAutoComplete = (AutoCompleteTextView)
//                findViewById(R.id.autoCompleteTextView_End);
//        startAutoComplete.setAdapter(adapter);
//        startAutoComplete.setText(locationDatabase.locations[0]);
//        startAutoComplete.dismissDropDown();
//        endAutoComplete.setAdapter(adapter);
//
//        //When user clicks on option from drop down from Start
//        startAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
//                                    long id) {
//                int indexStart = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
//                makeMarkerStart(indexStart);
//                hideKeyboard(GoogleMapsActivity.this);
//            }
//        });
//
//        //When user clicks on option from drop down from End
//        endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
//                                    long id) {
//                int indexEnd = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
//                makeMarkerEnd(indexEnd);
//                hideKeyboard(GoogleMapsActivity.this);
//            }
//        });
//
//        //When user clicks on actual field
//        startAutoComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAutoComplete.setText("");
//            }
//        });

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListenerStart
            = new AdapterView.OnItemClickListener() {
        @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                PlaceAutoComplete.PlaceAutocomplete item =  mPlaceArrayAdapterStart.getItem(pos);
                String placeId = String.valueOf(item.placeId);
                Log.i(TAG, "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackStart);
            marker1.setVisible(true);
                hideKeyboard(GoogleMapsActivity.this);

            }

        /*@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PlaceAutoComplete.PlaceAutocomplete item =  mPlaceArrayAdapter.getItem(position);
            String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }*/
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListenerEnd
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {
            PlaceAutoComplete.PlaceAutocomplete item = mPlaceArrayAdapterEnd.getItem(pos);
            String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackEnd);
            hideKeyboard(GoogleMapsActivity.this);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackStart
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            LatLng start = place.getLatLng();
            if((start.latitude > 41.497281 && start.longitude > -90.565683)&& (start.latitude < 41.507930 && start.longitude < -90.539093)) {
                marker1.setPosition(start);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));
            } else {
                startAutoComplete.setText("");
                Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Log.d(TAG, "Start Loc out of bounds");
            }

        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackEnd
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            LatLng end = place.getLatLng();
            if((end.latitude > 41.497281 && end.longitude > -90.565683)&& (end.latitude < 41.507930 && end.longitude < -90.539093)) {
                marker2.setVisible(true);
                marker2.setPosition(end);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 15));
            } else {
                endAutoComplete.setText("");
                Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Log.d(TAG, "END Loc out of bounds");
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapterStart.setGoogleApiClient(mGoogleApiClient);
        mPlaceArrayAdapterEnd.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapterStart.setGoogleApiClient(null);
        mPlaceArrayAdapterEnd.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }


//    public void makeMarkerStart(int indexOfLocation) {
//        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
//        marker1.setPosition(chosenCoordinates);
//        marker1.setTitle(locationDatabase.locations[indexOfLocation]);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenCoordinates,15));
//    }
//
//    public void makeMarkerEnd(int indexOfLocation) {
//        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
//        marker2.setVisible(true);
//        marker2.setPosition(chosenCoordinates);
//        marker2.setTitle(locationDatabase.locations[indexOfLocation]);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chosenCoordinates,15));
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng chosenCoordinates = new LatLng(41.505199, -90.550674); AUGUSTANA COORDINATES
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    if((currentLatLng.latitude > 41.497281 && currentLatLng.longitude > -90.565683)&& (currentLatLng.latitude < 41.507930 && currentLatLng.longitude < -90.539093)) {
                        marker1.setVisible(true);
                        marker1.setTitle("Current Location");
                        marker1.setPosition(currentLatLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        startAutoComplete.setText("Current Location");
                        return true;
                    } else {
                        endAutoComplete.setText("");
                        Toast toast = Toast.makeText(getBaseContext(), "You are not in the ACES service area!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        Log.d(TAG, "Current Location out of bounds");
                    }
                }
                return false;
            }
        });

        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        marker1 =  mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).visible(false));
        marker2 = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false)); // TODO: Refactor bad code here
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
    }


    //Hides the Keyboard
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
