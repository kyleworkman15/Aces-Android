package edu.augustana.aces;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.opengl.Visibility;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;

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

        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";
        try{
            notificationData = remoteMessage.getData().toString();
            //notificationTitle = remoteMessage.getNotification().getTitle();
            //notificationBody = remoteMessage.getNotification().getBody();
        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
        }
        Log.d(TAG, "onMessageReceived: data: " + notificationData);
        //Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
        //Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);


        String dataType = remoteMessage.getData().get("data_type");
        if(dataType.equals("direct_message")){
            Log.d(TAG, "onMessageReceived: new incoming message.");
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String messageId = remoteMessage.getData().get("message_id");
            sendMessageNotification(title, message, messageId);
        }
    }

    /**
     * Build a push notification for a chat message
     * @param title
     * @param message
     */
    private void sendMessageNotification(String title, String message, String messageId){
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        //get the notification id
        int notificationId = buildNotificationId(messageId);

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(this, Google_SignIn.class);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.augie_icon)
                //.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        //R.drawable.aces_image))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                //.setColor(getColor(R.color.blue1))
                .setAutoCancel(true)
                //.setSubText(message)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());

    }


    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");

        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationId: id: " + id);
        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return notificationId;
    }

}