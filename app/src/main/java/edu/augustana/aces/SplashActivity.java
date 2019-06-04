package edu.augustana.aces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by Kyle Workman
 */
public class SplashActivity extends AppCompatActivity implements AcesRemoteConfig.RemoteConfigChangeListener {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(FirebaseAuth.getInstance().getCurrentUser());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(SplashActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        AcesRemoteConfig.initialize(this);
    }

    public void updateRemoteConfig() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            String cloudVersion = remoteConfig.getString(KEY_CURRENT_VERSION).replace(".", "");
            String currentVersion = getAppVersion(this).replace(".", "");
            int cloudNum = Integer.parseInt(cloudVersion);
            int currentNum = Integer.parseInt(currentVersion);
            final String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            if (currentNum < cloudNum) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Required Update")
                        .setMessage("Please update the app to continue using ACES.")
                        .setCancelable(false)
                        .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        redirectStore(updateUrl);
                                        finish();
                                    }
                                }).create();
                dialog.show();
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                checkNetwork();
            } else {
                startActivity(new Intent(this, Google_SignIn.class));
                finish();
            }
        }
    }

    private void checkNetwork() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            startActivity(new Intent(SplashActivity.this, GoogleMapsActivity.class));
            finish();
        } else {
            displayNoConnectionMsg();
        }
    }

    private void displayNoConnectionMsg() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Connection Error")
                .setMessage("Please check your internet connection")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkNetwork();
                    }
                });
        dialog.show();
    }

    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }

        return result;
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}