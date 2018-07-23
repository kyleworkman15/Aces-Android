package edu.augustana.aces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Kyle Workman
 *
 * For managing the token refresh for the push notifications
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    /**
     * Called if the InstanceID token is updated.
     */
    @Override
    public void onTokenRefresh() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            sendRegistrationToServer(refreshedToken);
        }
    }

    /**
     * Persist token in the database.
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("PENDING RIDES")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","))
                .child("token")
                .setValue(token);
    }
}