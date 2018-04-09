package com.augustana.teamaardvark.acesaardvark;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    LatLngBounds augustanaBounds;
    MarkerOptions mo;
    Marker marker1;
    Marker marker2;
    LocationManager locationManager;
    AutoCompleteTextView startAutoComplete;
    AutoCompleteTextView endAutoComplete;
    LocationDatabase locationDatabase;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, locationDatabase.locations);
        AutoCompleteTextView startAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_Start);
        AutoCompleteTextView endAutoComplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView_End);
        startAutoComplete.setAdapter(adapter);
        endAutoComplete.setAdapter(adapter);

        startAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                int indexStart = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
               makeMarkerStart(indexStart);
            }
        });

        endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                int indexEnd = Arrays.asList(locationDatabase.locations).indexOf(parent.getItemAtPosition(pos));
                makeMarkerEnd(indexEnd);
            }
        });

    }

    public void makeMarkerStart(int indexOfLocation ){
        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
        marker1.setPosition(chosenCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chosenCoordinates));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
    }

    public void makeMarkerEnd(int indexOfLocation ){
        LatLng chosenCoordinates = new LatLng(locationDatabase.latitude[indexOfLocation], locationDatabase.longitude[indexOfLocation]);
        marker2.setPosition(chosenCoordinates);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        augustanaBounds = new LatLngBounds(new LatLng(41.497304, -90.546406), new LatLng(41.507601, -90.556957));
//        mMap.setLatLngBoundsForCameraTarget(augustanaBounds);
        marker1 =  mMap.addMarker(mo);
        marker2 = mMap.addMarker(mo);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker1.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

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
