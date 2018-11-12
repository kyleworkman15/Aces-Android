package edu.augustana.aces;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Aces {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    public interface UpdateNow {
        void update();
    }

    private UpdateNow updateActivity;
    private Context context;

    public Aces(Activity activity, Context context) {
        this.updateActivity = (UpdateNow) activity;
        this.context = context;
        onCreate();
    }

    public void onCreate() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);


        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=edu.augustana.aces");

        mFirebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        mFirebaseRemoteConfig.fetch(3600) // fetch every hour
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        updateActivity.update();
                    }
                });
    }
}
