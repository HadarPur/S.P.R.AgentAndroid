package com.example.hpur.spragent.UI.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.hpur.spragent.R;
import com.example.hpur.spragent.UI.AudioActivity;
import com.example.hpur.spragent.UI.MainActivity;
import com.example.hpur.spragent.UI.VideoActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    Intent intent = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() == 0 ) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            intent = new Intent(this, MainActivity.class);
            showNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }

        // Check if message contains a data payload.
        else if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                getDataFromPush(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void showNotification(String body, String title) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_logo_full)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SPR_Chanel_2018", "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void showNotificationWithoutIntent(String body, String title) {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_logo_full)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SPR_Chanel_2018", "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void getDataFromPush(String body, String title, JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            if (json.length() > 2) {
                String consumerId = json.getString("userID");
                String type = json.getString("activityType");
                String apiKey = json.getString("apiKey");
                String sessionId = json.getString("sessionId");
                String tokenPublisher = json.getString("tokenPublisher");
                String tokenSubscriber = json.getString("tokenSubscriber");
                String tokenModerator = json.getString("tokenModerator");

                switch (type) {
                    case "AUDIO":
                        intent = new Intent(this, AudioActivity.class);
                        break;
                    case "VIDEO":
                        intent = new Intent(this, VideoActivity.class);
                        break;
                }

                intent.putExtra("apiKey", apiKey);
                intent.putExtra("sessionId", sessionId);
                intent.putExtra("tokenPublisher", tokenPublisher);
                intent.putExtra("tokenSubscriber", tokenSubscriber);
                intent.putExtra("tokenModerator", tokenModerator);
                showNotification(body, title);
            } else {
                showNotificationWithoutIntent(body, title);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}