package edu.augustana.aces;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Creates the Google Map
 */

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, Serializable {
    private static final String TAG = "Google Maps";
    LatLng augustanaCoordinates = new LatLng(41.505199, -90.550674);

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    private Button request_btn;
    private DatabaseReference mDatabase;
    Marker markerStart;
    Marker markerEnd;
    MyPlace chosenPlaceStart = new MyPlace("", 0, 0);
    MyPlace chosenPlaceEnd = new MyPlace("", 0, 0);
    LocationManager locationManager;
    InstantComplete startAutoComplete;
    InstantComplete endAutoComplete;
    PlaceAutoComplete mPlaceArrayAdapterStart;
    PlaceAutoComplete mPlaceArrayAdapterEnd;
    Spinner numRiders;
    private String rideNum;
    private String flag;
    private String message;
    public static final String PREFS = "PrefsFile";
    private RideInfo ride;
    PlaceAutocompleteFragment autocompleteFragment;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        checkRideInProgress();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        numRiders = (Spinner) findViewById(R.id.picker);
        request_btn = findViewById(R.id.request_ride_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES");
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("STATUS");
        Log.d("MSG",String.valueOf(flag));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = dataSnapshot.child("FLAG").getValue().toString();
                String customMsg = dataSnapshot.child("MESSAGE").getValue().toString();
                if (flag.equals("OFF")) {
                    if (customMsg.equals("")) {
                        message = "--------------Hours--------------\nFall Term: 7pm - 2am\nWinter Term: 6pm - 2am\nSpring Term: 7pm - 2am";
                    } else {
                        message = customMsg + "\n\n--------------Hours--------------\nFall Term: 7pm - 2am\nWinter Term: 6pm - 2am\nSpring Term: 7pm - 2am";
                    }
                    showAlert("ACES Offline", message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("OFF")){
                    showAlert("ACES Offline", message);
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
        checkCancelledRide();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    }

    /**
     * Sending the ride data to the database after user requests ride
     * @param v
     */
    public void handleRequestButtonClick(View v) {
        Log.d(TAG, "END AUTO COMPLETE: " + endAutoComplete.getText() + "");
        if (checkAllConstraints()) { //checks if all fields are filled out
            String email = (String) FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
            String end = chosenPlaceEnd.name;
            rideNum = numRiders.getSelectedItem().toString().replace("Number of Riders: ", "");
            String start = chosenPlaceStart.name;
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String time = new SimpleDateFormat("M/d/yyyy h:mm aaa").format(ts);

            ride = new RideInfo(email, end, " ", " ", rideNum, start, time, "1000", ts.getTime() + "");
            mDatabase.child(email).setValue(ride);

            Log.d("date", time);
            Log.d("MSG_CLASS", ts.toString());
            Log.d(TAG, "Ride Submitted");
            Intent intent = new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class);
            intent.putExtra("user", ride);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            startAutoComplete.setText("");
            endAutoComplete.setText("");
            startAutoComplete.dismissDropDown();
            endAutoComplete.dismissDropDown();
            numRiders.setSelection(0);
            markerStart.setVisible(false);
            markerEnd.setVisible(false);
            chosenPlaceStart = new MyPlace("", 0, 0);
            chosenPlaceEnd = new MyPlace("", 0, 0);
            LatLng currentLatLng = new LatLng(augustanaCoordinates.latitude, augustanaCoordinates.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            if (data.getStringExtra("result").equals("cancelled")) {
                showAlert("Ride Cancelled", "Requested ride cancelled by dispatcher.");
            }
            ride = new RideInfo("", "", "", "", "", "", "", "", "");
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String name = place.getName().toString();
                LatLng latlng = place.getLatLng();
                if (ACESConfiguration.isInACESBoundary(latlng)) {
                    startAutoComplete.setText(name);
                    chosenPlaceStart = new MyPlace(name, latlng.latitude, latlng.longitude);
                    markerStart.setPosition(latlng);
                    markerStart.setTitle("Start: " + name);
                    markerStart.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                } else {
                    startAutoComplete.setText("");
                    startAutoComplete.dismissDropDown();
                    Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String name = place.getName().toString();
                LatLng latlng = place.getLatLng();
                if (ACESConfiguration.isInACESBoundary(latlng)) {
                    endAutoComplete.setText(name);
                    chosenPlaceEnd = new MyPlace(name, latlng.latitude, latlng.longitude);
                    markerEnd.setPosition(latlng);
                    markerEnd.setTitle("End: " + name);
                    markerEnd.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                } else {
                    endAutoComplete.setText("");
                    endAutoComplete.dismissDropDown();
                    Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Checks the start and end autocompletes to make sure that they are not empty and match where the marker is on the
     * map before requesting the ride.
     * @return true if all constraints are satisfied
     */
    public boolean checkAllConstraints() {
        //Checks Start is filled out
        if ((chosenPlaceEnd.latitude == chosenPlaceStart.latitude && chosenPlaceEnd.longitude == chosenPlaceStart.longitude) ||
                chosenPlaceEnd.name.equals(chosenPlaceStart.name)) {
            Toast toast = Toast.makeText(getBaseContext(), "Please Select 2 Unique Locations", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else if (startAutoComplete.getText().toString().equals("")) {
            Toast toast = Toast.makeText(getBaseContext(), "Please Select a Start Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "Start not filled");
            return false;
        } else if (endAutoComplete.getText().toString().equals("")) {
            Toast toast = Toast.makeText(getBaseContext(), "Please Select an End Location", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.d(TAG, "End not filled");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates the Number of Riders drop down with # ranging from 1-7
     */
    public void numRidersSetUp() {
        String[] numbers = new String[]{"Number of Riders: 1", "Number of Riders: 2", "Number of Riders: 3", "Number of Riders: 4",
                "Number of Riders: 5", "Number of Riders: 6", "Number of Riders: 7"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_center, numbers);
        numRiders.setAdapter(adapter);
    }

    /**
     * Creates the Start and End autocompletetextviews
     */
    public void autoCompleteSetUp() {
//        mPlaceArrayAdapterStart = new PlaceAutoComplete(this, android.R.layout.simple_list_item_1,
//                AUGUSTANA_VIEW, null);
//        mPlaceArrayAdapterEnd = new PlaceAutoComplete(this, android.R.layout.simple_list_item_1,
//                AUGUSTANA_VIEW, null);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LocationDatabase.getNames());

        //Make the drawable for the Start AutoComplete
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(10);
        gd.setStroke(1, 0xFFA9A9A9);

        startAutoComplete = (InstantComplete)
                findViewById(R.id.autoCompleteTextView_Start);
        startAutoComplete.setBackground(gd);
        startAutoComplete.setOnFocusChangeListener(changedStart);
        startAutoComplete.setOnDismissListener(dismissStart);
        startAutoComplete.setOnItemClickListener(databaseCompleteStart);
        startAutoComplete.setAdapter(adapter);

        endAutoComplete = (InstantComplete)
                findViewById(R.id.autoCompleteTextView_End);
        endAutoComplete.setBackground(gd);
        endAutoComplete.setOnFocusChangeListener(changedEnd);
        endAutoComplete.setOnDismissListener(dismissEnd);
        endAutoComplete.setOnItemClickListener(databaseCompleteEnd);
        endAutoComplete.setAdapter(adapter);
    }

    private AutoCompleteTextView.OnDismissListener dismissStart = new AutoCompleteTextView.OnDismissListener() {
        @Override
        public void onDismiss() {
            hideKeyboard(GoogleMapsActivity.this);
            startAutoComplete.clearFocus();
        }
    };

    private AutoCompleteTextView.OnDismissListener dismissEnd = new AutoCompleteTextView.OnDismissListener() {
        @Override
        public void onDismiss() {
            hideKeyboard(GoogleMapsActivity.this);
            endAutoComplete.clearFocus();
        }
    };

    private AutoCompleteTextView.OnFocusChangeListener changedStart = new AutoCompleteTextView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                startAutoComplete.setText("");
            } else {
                startAutoComplete.setText(chosenPlaceStart.name);
            }
        }
    };

    private AutoCompleteTextView.OnFocusChangeListener changedEnd = new AutoCompleteTextView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                endAutoComplete.setText("");
            } else {
                endAutoComplete.setText(chosenPlaceEnd.name);
            }
        }
    };

    /**
     * Adapter for the start autocomplete
     */
    private AdapterView.OnItemClickListener databaseCompleteStart
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String name = adapterView.getItemAtPosition(i).toString();
            if (name.equals("Enter an Address")) {
                try {
                    startAutoComplete.setText("");
                    AutocompleteFilter filter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(new LatLngBounds(new LatLng(ACESConfiguration.LAT1, ACESConfiguration.LONG1), new LatLng(ACESConfiguration.LAT2, ACESConfiguration.LONG2)))
                            .setFilter(filter)
                            .build(GoogleMapsActivity.this);
                    startActivityForResult(intent, 2);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            } else {
                double[] latlng = LocationDatabase.getPlaces().get(name);
                chosenPlaceStart = new MyPlace(name, latlng[0], latlng[1]);
            }
            LatLng coords = new LatLng(chosenPlaceStart.latitude, chosenPlaceStart.longitude);
            if (!coords.equals(new LatLng(0,0))) {
                markerStart.setPosition(coords);
                markerStart.setTitle("Start: " + name);
                markerStart.setVisible(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
                hideKeyboard(GoogleMapsActivity.this);
            }
            startAutoComplete.clearFocus();
        }
    };

    /**
     * Adapter for the end autocomplete
     */
    private AdapterView.OnItemClickListener databaseCompleteEnd
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String name = adapterView.getItemAtPosition(i).toString();
            if (name.equals("Enter an Address")) {
                endAutoComplete.setText("");
                try {
                    AutocompleteFilter filter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(new LatLngBounds(new LatLng(ACESConfiguration.LAT1, ACESConfiguration.LONG1), new LatLng(ACESConfiguration.LAT2, ACESConfiguration.LONG2)))
                            .setFilter(filter)
                            .build(GoogleMapsActivity.this);
                    startActivityForResult(intent, 3);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            } else {
                double[] latlng = LocationDatabase.getPlaces().get(name);
                chosenPlaceEnd = new MyPlace(name, latlng[0], latlng[1]);
            }
            LatLng coords = new LatLng(chosenPlaceEnd.latitude, chosenPlaceEnd.longitude);
            if (!coords.equals(new LatLng(0,0))) {
                markerEnd.setPosition(coords);
                markerEnd.setTitle("End: " + name);
                markerEnd.setVisible(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
                hideKeyboard(GoogleMapsActivity.this);
            }
            endAutoComplete.clearFocus();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        //mPlaceArrayAdapterStart.setGoogleApiClient(mGoogleApiClient);
        //mPlaceArrayAdapterEnd.setGoogleApiClient(mGoogleApiClient);
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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng currentLatLng = new LatLng(augustanaCoordinates.latitude, augustanaCoordinates.longitude);
        markerStart = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).visible(false));
        markerEnd = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).visible(false)); // TODO: Refactor bad code here
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
        startActivity(new Intent(GoogleMapsActivity.this, Google_SignIn.class));
    }

    /**
     * Checks the firebase database to see if email of the requester is in the current ride list
     */
    public void checkRideInProgress() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

        ValueEventListener valEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RideInfo ride = dataSnapshot.getValue(RideInfo.class);
                if (ride != null) {
                    Intent intent = new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class);
                    intent.putExtra("user", ride);
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

    public void checkCancelledRide() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sharedPref.contains("timestamp")) {
            String timestamp = sharedPref.getString("timestamp", "timestamp");
            Log.d("TSFOUND", timestamp);
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CANCELLED RIDES").child(email + "_" + timestamp);
            ValueEventListener vel = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        String endTime = dataSnapshot.child("endTime").getValue().toString();
                        if (endTime.equals("Cancelled by Dispatcher")) {
                            deleteTS();
                            showAlert("Ride Cancelled", "Requested ride cancelled by dispatcher.");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(vel);
        }
    }

    public void deleteTS() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public void showAlert(String title, String msg) {
        AlertDialog alert = new AlertDialog.Builder(GoogleMapsActivity.this).create();
        alert.setTitle(title);
        alert.setMessage(msg);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alert.show();
    }

}