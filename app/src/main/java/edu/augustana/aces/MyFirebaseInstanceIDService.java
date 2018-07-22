package edu.augustana.aces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Get updated InstanceID token.
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            sendRegistrationToServer(refreshedToken);
        }
    }


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
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