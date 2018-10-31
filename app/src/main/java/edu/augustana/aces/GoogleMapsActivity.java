package edu.augustana.aces;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kyle Workman, Kevin Barbian, Megan Janssen, Tan Nguyen, Tyler May
 *
 * Creates the Google Map
 */

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, Serializable {
    private static final String TAG = "Google Maps";
    LatLng augustanaCoordinates = new LatLng(41.505199, -90.550674);

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    private int count = 0;
    private GoogleMap mMap;
    private Button request_btn;
    private DatabaseReference mDatabase;
    private DatabaseReference db;
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
    private ProgressBar spinner;
    PlaceAutocompleteFragment autocompleteFragment;
    private TextView tv;
    private LocationDatabase locDB = new LocationDatabase();
    private Context context;
    private ImageView favStart;
    private ImageView favEnd;
    private HashMap<String, double[]> favorites = new HashMap<>();
    private boolean isActivated = false;
    private String estWaitTime;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    Geocoder geocoder;

    //This is just for the GOOGLEPLACES
    private static final LatLngBounds AUGUSTANA_VIEW = new LatLngBounds(
            new LatLng(41.497281, -90.565683), new LatLng(41.507930, -90.539093));

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        disableUI();
        setContentView(R.layout.activity_map);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        tv = findViewById(R.id.estWaitTime);
        db =  FirebaseDatabase.getInstance().getReference();
        context = this;
        getFavorites();
        favStart = findViewById(R.id.favStart);
        favStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorites.containsKey(chosenPlaceStart.name)) {
                    Toast toast = Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    favorites.remove(chosenPlaceStart.name);
                    removeFavoriteFromDropDownAndDatabase(chosenPlaceStart.name);
                    saveFavorites();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        favStart.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off, getApplicationContext().getTheme()));
                    } else {
                        favStart.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off));
                    }
                } else {
                    displayPopUpForFavorite(true);
                }
                if (chosenPlaceStart.name.equals(chosenPlaceEnd.name)) {
                    setStar(chosenPlaceEnd.name, favEnd);
                }
            }
        });
        favEnd = findViewById(R.id.favEnd);
        favEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorites.containsKey(chosenPlaceEnd.name)) {
                    Toast toast = Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    favorites.remove(chosenPlaceEnd.name);
                    removeFavoriteFromDropDownAndDatabase(chosenPlaceEnd.name);
                    saveFavorites();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        favEnd.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off, getApplicationContext().getTheme()));
                    } else {
                        favEnd.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off));
                    }
                } else {
                    displayPopUpForFavorite(false);
                }
                if (chosenPlaceStart.name.equals(chosenPlaceEnd.name)) {
                    setStar(chosenPlaceStart.name, favStart);
                }
            }
        });
        mDatabase = db.child("PENDING RIDES");

        checkOnline();
        checkRideInProgress();
        checkCancelledRide();
        locationListener();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        numRiders = (Spinner) findViewById(R.id.picker);
        request_btn = findViewById(R.id.request_ride_btn);
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        Log.d("MSG",String.valueOf(flag));

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

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    }

    public void displayPopUpForFavorite(final boolean isStart) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to Favorites");
        builder.setMessage("Enter a nickname for the location. The location will be saved in the drop down box.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) {
                            return src;
                        }
                        if (src.toString().matches("[a-zA-Z0-9 ]+")) {
                            return src;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < src.length(); i++) {
                                char c = src.charAt(i);
                                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ' || (c >= '0' && c <= '9')) {
                                    sb.append(c);
                                }
                            }
                            return sb.toString();
                        }
                    }
                }
        });
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String favorite = input.getText().toString();
                if (isStart) {
                    String oldNameStr = chosenPlaceStart.name;
                    String[] oldName = chosenPlaceStart.name.split(" - ");
                    chosenPlaceStart.name = favorite + " - " + oldName[oldName.length - 1];
                    favorites.put(chosenPlaceStart.name, new double[]{chosenPlaceStart.latitude, chosenPlaceStart.longitude});
                    addFavoriteToDropDownAndDatabase(chosenPlaceStart.name, chosenPlaceStart.latitude, chosenPlaceStart.longitude);
                    saveFavorites();
                    startAutoComplete.setText("Start: " + chosenPlaceStart.name);
                    markerStart.setTitle("Start: " + chosenPlaceStart.name);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        favStart.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on, getApplicationContext().getTheme()));
                    } else {
                        favStart.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on));
                    }
                    if (oldNameStr.equals(chosenPlaceEnd.name)) {
                        chosenPlaceEnd.name = chosenPlaceStart.name;
                        setStar(chosenPlaceEnd.name, favEnd);
                        endAutoComplete.setText("End: " + chosenPlaceEnd.name);
                        markerEnd.setTitle("End: " + chosenPlaceEnd.name);
                    }
                } else {
                    String oldNameStr = chosenPlaceEnd.name;
                    String[] oldName = chosenPlaceEnd.name.split(" - ");
                    chosenPlaceEnd.name = favorite + " - " + oldName[oldName.length - 1];
                    favorites.put(chosenPlaceEnd.name, new double[]{chosenPlaceEnd.latitude, chosenPlaceEnd.longitude});
                    addFavoriteToDropDownAndDatabase(chosenPlaceEnd.name, chosenPlaceEnd.latitude, chosenPlaceEnd.longitude);
                    saveFavorites();
                    endAutoComplete.setText("Start: " + chosenPlaceEnd.name);
                    markerEnd.setTitle("Start: " + chosenPlaceEnd.name);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        favEnd.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on, getApplicationContext().getTheme()));
                    } else {
                        favEnd.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on));
                    }
                    if (oldNameStr.equals(chosenPlaceStart.name)) {
                        chosenPlaceStart.name = chosenPlaceEnd.name;
                        setStar(chosenPlaceStart.name, favStart);
                        startAutoComplete.setText("Start: " + chosenPlaceStart.name);
                        markerStart.setTitle("Start: " + chosenPlaceStart.name);
                    }
                }
                Toast toast = Toast.makeText(getBaseContext(), "Added to favorites", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void waitTimeListener() {
        isActivated = true;
        DatabaseReference ref = db.child("EST WAIT TIME");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                estWaitTime = dataSnapshot.child("estimatedWT").getValue().toString();
                if(flag.equals("ON")) {
                    String text = "Estimated Wait Time: " + estWaitTime + " minutes";
                    tv.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void locationListener() {
        DatabaseReference ref = db.child("LOCATIONS");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locDB = new LocationDatabase();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    double lat = (double) snapshot.child("lat").getValue();
                    double lng = (double) snapshot.child("long").getValue();
                    locDB.addLocation(name, lat, lng);
                } for (String key : favorites.keySet()) {
                    double[] arr = favorites.get(key);
                    locDB.addLocation(key, arr[0], arr[1]);
                }
                updateDropDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Disables UI until done checking for an active ride
     */
    public void disableUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Checks if Aces is online/offline
     */
    public void checkOnline() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("STATUS");
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
                    tv.setText("ACES Offline");
                } else {
                    if (!isActivated) {
                        waitTimeListener();
                    } else {
                        String text = "Estimated Wait Time: " + estWaitTime + " minutes";
                        tv.setText(text);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sending the ride data to the database after user requests ride
     * @param v
     */
    public void handleRequestButtonClick(View v) {
        Log.d(TAG, "END AUTO COMPLETE: " + endAutoComplete.getText() + "");
        if (checkAllConstraints()) { //checks if all fields are filled out
            String email = (String) FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
            String start = chosenPlaceStart.name;
            String end = chosenPlaceEnd.name;
            if (favorites.containsKey(start)) {
                String arr[] = start.split(" - ");
                start = arr[arr.length-1];
            } if (favorites.containsKey(end)) {
                String arr[] = end.split(" - ");
                end = arr[arr.length-1];
            }
            rideNum = numRiders.getSelectedItem().toString().replace("Number of Riders: ", "");

            //ServerValue.TIMESTAMP - send to Firebase - will convert to firebase timestamp
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String time = new SimpleDateFormat("M/d/yyyy h:mm aaa").format(ts);
            String token = FirebaseInstanceId.getInstance().getToken();

            ride = new RideInfo(email, end, " ", " ", rideNum, start, time, "1000", ts.getTime(), token,
                    " ");
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
            favStart.setVisibility(View.GONE);
            favEnd.setVisibility(View.GONE);
            markerStart.setVisible(false);
            markerEnd.setVisible(false);
            chosenPlaceStart = new MyPlace("", 0, 0);
            chosenPlaceEnd = new MyPlace("", 0, 0);
            LatLng currentLatLng = new LatLng(augustanaCoordinates.latitude, augustanaCoordinates.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            if (data.getStringExtra("result").equals("cancelled")) {
                showAlert("Ride Cancelled", "Requested ride cancelled by dispatcher.");
            }
            ride = new RideInfo("", "", "", "", "", "", "", "", 0, "", "");
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String name = place.getName().toString().replaceAll("\\.", "");
                LatLng latlng = place.getLatLng();
                if (ACESConfiguration.isInACESBoundary(latlng)) {
                    chosenPlaceStart = new MyPlace(name, latlng.latitude, latlng.longitude);
                    startAutoComplete.setText("Start: " + name);
                    markerStart.setPosition(latlng);
                    markerStart.setTitle("Start: " + name);
                    markerStart.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                    setStar(chosenPlaceStart.name, favStart);
                } else {
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
                String name = place.getName().toString().replaceAll("\\.", "");
                LatLng latlng = place.getLatLng();
                if (ACESConfiguration.isInACESBoundary(latlng)) {
                    endAutoComplete.setText("End: " + name);
                    chosenPlaceEnd = new MyPlace(name, latlng.latitude, latlng.longitude);
                    markerEnd.setPosition(latlng);
                    markerEnd.setTitle("End: " + name);
                    markerEnd.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                    setStar(chosenPlaceEnd.name, favEnd);
                } else {
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

        //Make the drawable for the Start AutoComplete
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(10);
        gd.setStroke(1, 0xFFA9A9A9);

        startAutoComplete = findViewById(R.id.autoCompleteTextView_Start);
        startAutoComplete.setBackground(gd);
        startAutoComplete.setOnFocusChangeListener(changedStart);
        startAutoComplete.setOnDismissListener(dismissStart);
        startAutoComplete.setOnItemClickListener(databaseCompleteStart);

        endAutoComplete = findViewById(R.id.autoCompleteTextView_End);
        endAutoComplete.setBackground(gd);
        endAutoComplete.setOnFocusChangeListener(changedEnd);
        endAutoComplete.setOnDismissListener(dismissEnd);
        endAutoComplete.setOnItemClickListener(databaseCompleteEnd);

        startAutoComplete.setCursorVisible(false);
        endAutoComplete.setCursorVisible(false);
        updateDropDown();
    }

    private AutoCompleteTextView.OnDismissListener dismissStart = new AutoCompleteTextView.OnDismissListener() {
        @Override
        public void onDismiss() {
            String text = startAutoComplete.getText().toString();
            if(text.equals("") || text.contains("Start: ")) { } else {
                Toast toast = Toast.makeText(getBaseContext(), "Please select a location from the drop down.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                hideKeyboard(GoogleMapsActivity.this);
            }
            startAutoComplete.clearFocus();
        }
    };

    private AutoCompleteTextView.OnDismissListener dismissEnd = new AutoCompleteTextView.OnDismissListener() {
        @Override
        public void onDismiss() {
            String text = endAutoComplete.getText().toString();
            if(text.equals("") || text.contains("End: ")) { } else {
                Toast toast = Toast.makeText(getBaseContext(), "Please select a location from the drop down.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                hideKeyboard(GoogleMapsActivity.this);
            }
            endAutoComplete.clearFocus();
        }
    };

    private AutoCompleteTextView.OnFocusChangeListener changedStart = new AutoCompleteTextView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                startAutoComplete.setText("");
            } else {
                if (!chosenPlaceStart.name.equals("")) {
                    startAutoComplete.setText("Start: " + chosenPlaceStart.name);
                } else {
                    startAutoComplete.setText("");
                }
            }
        }
    };

    private AutoCompleteTextView.OnFocusChangeListener changedEnd = new AutoCompleteTextView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                endAutoComplete.setText("");
            } else {
                if (!chosenPlaceEnd.name.equals("")) {
                    endAutoComplete.setText("End: " + chosenPlaceEnd.name);
                } else {
                    endAutoComplete.setText("");
                }
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
                if (name.equals("My Location")) {
                    spinner.setVisibility(View.VISIBLE);
                    Location loc = getLastKnownLocation();
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    if (ACESConfiguration.isInACESBoundary(latLng)) {
                        Geocoder geocoder;
                        List<Address> addresses = new ArrayList<>();
                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.w("My Current loction", "Cannot get Address!");
                        }
                        spinner.setVisibility(View.GONE);
                        String address = addresses.get(0).getAddressLine(0);
                        address = address.replaceAll(", Rock Island", "");
                        address = address.replaceAll(", IL", "");
                        address = address.replaceAll(" 61201", "");
                        address = address.replaceAll(", USA", "");
                        address = address.replaceAll("\\.", "");
                        if (address.toLowerCase().equals("unnamed road ")) {
                            spinner.setVisibility(View.GONE);
                            startAutoComplete.setText("");
                            startAutoComplete.dismissDropDown();
                            Toast toast = Toast.makeText(getBaseContext(), "Unknown Location", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            chosenPlaceStart = new MyPlace(address, latLng.latitude, latLng.longitude);
                            setStar(chosenPlaceStart.name, favStart);
                        }
                    } else {
                        spinner.setVisibility(View.GONE);
                        startAutoComplete.setText("");
                        startAutoComplete.dismissDropDown();
                        Toast toast = Toast.makeText(getBaseContext(), "Location Out of Bounds", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    double[] latlng = locDB.getPlaces().get(name);
                    chosenPlaceStart = new MyPlace(name, latlng[0], latlng[1]);
                    if (favorites.containsKey(chosenPlaceStart.name)) {
                        setStar(chosenPlaceStart.name, favStart);
                    } else {
                        favStart.setVisibility(View.GONE);
                    }
                }
                LatLng coords = new LatLng(chosenPlaceStart.latitude, chosenPlaceStart.longitude);
                if (!coords.equals(new LatLng(0,0))) {
                    startAutoComplete.setText("Start: " + chosenPlaceStart.name);
                    markerStart.setPosition(coords);
                    markerStart.setTitle("Start: " + chosenPlaceStart.name);
                    markerStart.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
                    hideKeyboard(GoogleMapsActivity.this);
                }
                startAutoComplete.clearFocus();
            }
        }
    };

    public void setStar(String name, ImageView view) {
        view.setVisibility(View.VISIBLE);
        if (!favorites.containsKey(name)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off, getApplicationContext().getTheme()));
            } else {
                view.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_off));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on, getApplicationContext().getTheme()));
            } else {
                view.setImageDrawable(getResources().getDrawable(R.drawable.btn_star_big_on));
            }
        }
    }

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
                double[] latlng = locDB.getPlaces().get(name);
                chosenPlaceEnd = new MyPlace(name, latlng[0], latlng[1]);
                if (favorites.containsKey(chosenPlaceEnd.name)) {
                    setStar(chosenPlaceEnd.name, favEnd);
                } else {
                    favEnd.setVisibility(View.GONE);
                }
                LatLng coords = new LatLng(chosenPlaceEnd.latitude, chosenPlaceEnd.longitude);
                if (!coords.equals(new LatLng(0, 0))) {
                    endAutoComplete.setText("End: " + chosenPlaceEnd.name);
                    markerEnd.setPosition(coords);
                    markerEnd.setTitle("End: " + name);
                    markerEnd.setVisible(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
                    hideKeyboard(GoogleMapsActivity.this);
                }
                endAutoComplete.clearFocus();
            }
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
                incrementCount();
                RideInfo ride = dataSnapshot.getValue(RideInfo.class);
                if (ride != null) {
                    Intent intent = new Intent(GoogleMapsActivity.this, AfterRequestRideActivity.class);
                    intent.putExtra("user", ride);
                    startActivityForResult(intent, 1);
                }
                if (count == 2) {
                    spinner.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        DatabaseReference pendingRide = FirebaseDatabase.getInstance().getReference().child("PENDING RIDES").child(email);
        pendingRide.addListenerForSingleValueEvent(valEventListener);
        DatabaseReference activeRide = FirebaseDatabase.getInstance().getReference().child("ACTIVE RIDES").child(email);
        activeRide.addListenerForSingleValueEvent(valEventListener);
    }

    final public void incrementCount() {
        count++;
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
            Log.d("TSFOUND", timestamp + "");
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
        editor.remove("timestamp");
        editor.commit();
    }

    public void updateDropDown() {
        List<String> startList = new ArrayList<>();
        startList.add("My Location");
        startList.addAll(Arrays.asList(locDB.getNames()));
        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, startList);
        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, Arrays.asList(locDB.getNames()));
        startAutoComplete.setAdapter(startAdapter);
        endAutoComplete.setAdapter(endAdapter);
    }

    public void addFavoriteToDropDownAndDatabase(String name, double lat, double lng) {
        locDB.addLocation(name, lat, lng);
        updateDropDown();
    }

    public void removeFavoriteFromDropDownAndDatabase(String name) {
        locDB.removeLocation(name);
        updateDropDown();
    }

    public void getFavorites() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE);
        String favs = sharedPref.getString("favorites", "none");
        String[] places = favs.split(",");
        for (String place : places) {
            String[] arr = place.split(":");
            if (arr.length == 3) {
                favorites.put(arr[0], new double[]{Double.parseDouble(arr[1]), Double.parseDouble(arr[2])});
            }
        }
    }

    public void saveFavorites() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (favorites.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String key : favorites.keySet()) {
                double[] arr = favorites.get(key);
                builder.append(key + ":" + arr[0] + ":" + arr[1] + ",");
            }
            editor.putString("favorites", builder.toString());
            Log.d("Map", "Saved favorites: " + builder.toString());
        } else {
            editor.putString("favorites", "none");
            Log.d("Map", "Saved favorites: " + "none");
        }
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