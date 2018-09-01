package edu.augustana.aces;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.opengl.Visibility;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Kyle Workman
 *
 * For constructing the notification once received
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String messageId = "message_id";
        sendMessageNotification(title, message, messageId);
    }

    /**
     * Build a push notification for a chat message
     * @param title Title of the message
     * @param message The actual message
     * @param messageId The id of the message
     */
    private void sendMessageNotification(String title, String message, String messageId){
        //get the notification id
        int notificationId = buildNotificationId(messageId);

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        Intent pendingIntent = new Intent(this, Google_SignIn.class);
        pendingIntent.setAction(Intent.ACTION_MAIN);
        pendingIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        pendingIntent,
                        0
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.augie_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE);
        if (Build.VERSION.SDK_INT <= 25) {
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
        } else {
            builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        }

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());
    }


    private int buildNotificationId(String id){
        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        return notificationId;
    }

}