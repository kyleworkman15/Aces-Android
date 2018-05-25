package com.augustana.teamaardvark.acesaardvark;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
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

/**
 * Creates the Google Map
 */

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "Google Maps";
    LatLng augustanaCoordinates = new LatLng(41.505199, -90.550674);

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    private Button request_btn;
    private DatabaseReference mDatabase;
    Marker marker1;
    Marker marker2;
    List<Address> addressesFrom;
    LocationManager locationManager;
    AutoCompleteTextView startAutoComplete;
    AutoCompleteTextView endAutoComplete;
    PlaceAutoComplete mPlaceArrayAdapterStart;
    PlaceAutoComplete mPlaceArrayAdapterEnd;
    Spinner numRiders;
    private int rideNum;
    private String flag;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    Geocoder geocoder;

    //This is just for the GOOGLEPLACES
    private static final LatLngBounds AUGUSTANA_VIEW = new LatLngBounds(
            new LatLng(41.497281, -90.565683), new LatLng(41.507930, -90.539093));

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        checkRideInProgress();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        numRiders = (Spinner) findViewById(R.id.spinner1);
        request_btn = findViewById(R.id.request_ride_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES");
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("STATUS").child("FLAG");
        Log.d("MSG",String.valueOf(flag));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("OFF")){
                    startActivity(new Intent(GoogleMapsActivity.this,OfflineActivity.class));
                    //finish();
                }
                else {
                    handleRequestButtonClick(v);
                }
            }
        });

        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && isPermissionGranted()) {
            requestLocation();
        }
        if (!isLocationEnabled())
            showAlert(1);

        autoCompleteSetUp();
        numRidersSetUp();

    }

    /**
     * Sending the ride data to the database after user requests ride
     * @param v
     */
    public void handleRequestButtonClick(View v) {
        String addressFrom;
        String addressTo;
        addressFrom = "Null";
        Log.d(TAG, "END AUTO COMPLETE: " + endAutoComplete.getText() + "");
        if (checkAllConstraints()) { //checks if all fields are filled out
            //For Current Location
            if (startAutoComplete.getText().toString().equals("Current Location")) {
                try {
                    addressesFrom = geocoder.getFromLocation(marker1.getPosition().latitude, marker1.getPosition().longitude, 1);
                    addressFrom = addressesFrom.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //For all other locations
            else {
                addressFrom = startAutoComplete.getText().toString();
            }
            addressTo = endAutoComplete.getText().toString();

            //cutting down the addresses to send to the database
            addressTo = addressTo.replace(", Rock Island, IL", "");
            addressTo = addressTo.replace(", Moline, IL", "");
            addressFrom = addressFrom.replace(", Moline, IL", "");
            addressFrom = addressFrom.replace(", Rock Island, IL", "");
            Log.d("AddressTo", addressTo);
            Log.d("addressFrom", addressFrom);

            rideNum = Integer.parseInt(numRiders.getSelectedItem().toString());
            String email = (String) FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String time = new SimpleDateFormat("MMM d hh:mm aaa").format(ts);

            final RideInfo ride = new RideInfo(email, addressTo, " ", " ", rideNum, addressFrom, time, 1000);
            mDatabase.child(email).setValue(ride);

            Log.d("date", time);
            Log.d("MSG_CLASS", ts.toString());
            Log.d(TAG, "Ride Submitted");
            Intent intent = new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Checks the start and end autocompletes to make sure that they are not empty and match where the marker is on the
     * map before requesting the ride.
     * @return true if all constraints are satisfied
     */
    public boolean checkAllConstraints() {
        //Checks Start is filled out
        if (startAutoComplete.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getBaseContext(), "Please fill out Start Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "Start not filled");
            return false;
        }
        //Checks End is filled out
        if (endAutoComplete.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getBaseContext(), "Please fill out End Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "End not filled");
            return false;
        }
        if(!marker1.isVisible()||!marker2.isVisible()){
            Toast toast = Toast.makeText(getBaseContext(), "Please choose a location from the drop down lists", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        //Checks Start location matches with marker1
        if (!startAutoComplete.getText().toString().contains(marker1.getTitle())) {
            Toast toast = Toast.makeText(getBaseContext(), "Please choose a Start location from the drop down list", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "Start doesn't match marker");
            return false;
        }
        //Checks End location matches with marker2
        if (!endAutoComplete.getText().toString().contains(marker2.getTitle())) {
            Toast toast = Toast.makeText(getBaseContext(), "Please choose an End location from the drop down list", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "End doesn't match marker");
            return false;
        }

        return true;
    }

    /**
     * Creates the Number of Riders drop down with # ranging from 1-7
     */
    public void numRidersSetUp() {
        numRiders.setDropDownWidth(160);
        String[] numbers = new String[]{"1", "2", "3", "4", "5", "6", "7"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        numRiders.setAdapter(adapter);
    }

    /**
     * Creates the Start and End autocompletetextviews
     */
    public void autoCompleteSetUp() {
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
    }

    /**
     * Adapter for the start autocomplete
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListenerStart
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {
            PlaceAutoComplete.PlaceAutocomplete item = mPlaceArrayAdapterStart.getItem(pos);
            String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackStart);
            marker1.setVisible(true);
            hideKeyboard(GoogleMapsActivity.this);

        }

    };

    /**
     * Adapter for the end autocomplete
     */
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
            handlePlaceDetailsCallback(places,marker1,startAutoComplete);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackEnd
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            handlePlaceDetailsCallback(places,marker2,endAutoComplete);
        }
    };

    private void handlePlaceDetailsCallback(PlaceBuffer places, Marker marker, AutoCompleteTextView autoCompleteTextView) {
        if (!places.getStatus().isSuccess()) {
            Log.e(TAG, "Place query did not complete. Error: " +
                    places.getStatus().toString());
            return;
        }
        // Selecting the first object buffer.
        final Place place = places.get(0);
        LatLng coordinates = place.getLatLng();
        if (ACESConfiguration.isInACESBoundary(coordinates)) {
            marker.setVisible(true);
            marker.setPosition(coordinates);
            marker.setTitle(place.getName().toString());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
        } else {
            autoCompleteTextView.setText("");
            Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "END Loc out of bounds");
        }

    }

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location currentLocation = getLastKnownLocation();
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    if (ACESConfiguration.isInACESBoundary(currentLatLng)) {
                        marker1.setVisible(true);
                        marker1.setTitle("Current Location");
                        marker1.setPosition(currentLatLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        startAutoComplete.setText("Current Location");
                        return true;
                    } else {
                        endAutoComplete.setText("");
                        Toast toast = Toast.makeText(getBaseContext(), "You are not in the ACES service area!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        Log.d(TAG, "Current Location out of bounds");
                    }
                }
                return false;
            }
        });

        LatLng currentLatLng = new LatLng(augustanaCoordinates.latitude, augustanaCoordinates.longitude);
        marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).visible(false));
        marker2 = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false)); // TODO: Refactor bad code here
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * Signs out the user if the back is pressed
     */
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(GoogleMapsActivity.this, Google_SignIn.class));
    }

    /**
     * Checks the firebase database to see if email of the requester is in the current ride list
     */
    public void checkRideInProgress() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", ",");

        ValueEventListener valEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RideInfo ride = dataSnapshot.getValue(RideInfo.class);
                if (ride != null) {
                    Intent intent = new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference currentRide = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES").child(email);
        currentRide.addListenerForSingleValueEvent(valEventListener);
        DatabaseReference activeRide = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES").child(email);
        activeRide.addListenerForSingleValueEvent(valEventListener);


    }

    // https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }
        return bestLocation;
    }
}
