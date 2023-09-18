package edu.augustana.aces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class AcesRemoteConfig {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";

    public interface RemoteConfigChangeListener {
        void onCreate(@Nullable Bundle savedInstanceState);

        void updateRemoteConfig();
    }

    public static void initialize(final RemoteConfigChangeListener updateActivity) {

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder()
                .build());

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=edu.augustana.aces");

        mFirebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        mFirebaseRemoteConfig.fetch(3600) // fetch every hour
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activate();
                        }
                        updateActivity.updateRemoteConfig();
                    }
                });
    }
}
