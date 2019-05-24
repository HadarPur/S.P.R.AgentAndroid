package com.example.hpur.spragent.UI.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
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
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
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
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_full);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_logo_full)
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        int notificationId = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void getDataFromPush(String body, String title, JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");
            String consumerId = data.getString("userID");
            String type = data.getString("activityType");
            String apiKey = data.getString("apiKey");
            String sessionId = data.getString("sessionId");
            String tokenPublisher = data.getString("tokenPublisher");
            String tokenSubscriber = data.getString("tokenSubscriber");

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

            showNotification(body, title);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}