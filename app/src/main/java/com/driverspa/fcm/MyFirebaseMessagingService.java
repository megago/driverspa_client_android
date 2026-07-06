package com.driverspa.fcm;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.EXTRA_CAMPAIGN_ID;
import static com.driverspa.util.Constants.EXTRA_WASHER_ID;
import static com.driverspa.util.Constants.PUSH_DATA;
import static com.driverspa.util.Constants.PUSH_TYPE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.model.Device;
import com.driverspa.model.NotificationId;
import com.driverspa.model.PushData;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.NewBookingPushRequestEvent;
import com.driverspa.util.otto.NewFareRequestBidEvent;
import com.driverspa.util.otto.NewReviewPushRequestEvent;
import com.driverspa.util.otto.RemoveBookingPushRequestEvent;
import com.driverspa.util.otto.ws.PushRequestEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Replaces the old GCM listener service. Receives FCM data messages,
 * deserializes the JSON "message" field into a PushData, posts the
 * matching Otto event so any currently-foreground fragments can react
 * in-process, and posts a system notification that opens
 * ClientHomeActivity routed by push type when tapped.
 *
 * Created by Yerzhan Tanatov on 08.01.2025.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    public static final String DEFAULT_CHANNEL_ID = "default";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "Push received from=" + remoteMessage.getFrom() + " data=" + data);

        // This is the client app — only act on pushes when a client is signed in;
        // otherwise there is nowhere useful to route, so drop it silently rather
        // than waking an unauthenticated UI.
        if (!UserPreferences.isUserLoggedIn(this)) {
            return;
        }

        // Backend sends the actual payload in a "message" string (legacy
        // GCM contract) — keep parsing that for backwards compatibility,
        // but also accept individual fields directly in the data map if
        // the backend ever switches to native FCM data messages.
        String messageJson = data.get("message");
        PushData pushData;
        if (!TextUtils.isEmpty(messageJson)) {
            try {
                pushData = PushData.deserialize(messageJson);
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse push 'message' as JSON, falling back to raw data map", e);
                pushData = pushDataFromMap(data);
            }
        } else {
            pushData = pushDataFromMap(data);
        }

        if (pushData == null || TextUtils.isEmpty(pushData.getType())) {
            Log.w(TAG, "Push dropped: no usable type");
            return;
        }

        dispatchEvent(pushData);
        sendNotification(pushData);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM token refreshed: " + token);
        Context ctx = getApplicationContext();
        UserPreferences.putPushToken(ctx, token);
        // Register the device with the backend as soon as the token is known.
        // This is the most reliable moment (independent of any Activity's
        // lifecycle / Play-Services timing), so the server always ends up with
        // a token-bearing device record.
        registerDeviceWithBackend(ctx, token);
    }

    /**
     * Posts the device (with its FCM token) to the backend, but only when a
     * client is signed in. Mirrors the payload built in MainActivity / SplashActivity.
     */
    private void registerDeviceWithBackend(Context ctx, String token) {
        if (TextUtils.isEmpty(token) || !UserPreferences.isUserLoggedIn(ctx)) {
            return;
        }
        String deviceId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        BA.getEventBus().post(new PushRequestEvent(deviceId,
                new Device(Build.MODEL, "android", token, "fcm")));
    }

    private PushData pushDataFromMap(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        PushData pd = new PushData(data.get("object_id"));
        pd.setType(data.get("type"));
        pd.setText(data.get("text"));
        pd.setCarwashId(data.get("carwash_id"));
        return pd;
    }

    /**
     * Mirror the dispatch contract used previously by the GCM listener so
     * foreground fragments keep their in-process reactions.
     */
    private void dispatchEvent(PushData pushData) {
        String type = pushData.getType();
        try {
            if (type.contains("book") || type.contains("serving") || type.contains("express")) {
                BA.getEventBus().post(new NewBookingPushRequestEvent(pushData));
            } else if (type.contains("bid")) {
                BA.getEventBus().post(new NewFareRequestBidEvent());
            } else if (type.contains("review")) {
                BA.getEventBus().post(new NewReviewPushRequestEvent(pushData));
            } else if (type.contains("cancel") || type.contains("removed")) {
                BA.getEventBus().post(new RemoveBookingPushRequestEvent(pushData));
            }
        } catch (Exception e) {
            // Otto can fan-out exceptions from handlers; don't let one
            // dead subscriber kill the whole push path.
            Log.w(TAG, "Otto dispatch failed for type=" + type, e);
        }
    }

    private void sendNotification(PushData pushData) {
        Intent intent = new Intent(getApplicationContext(), ClientHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PUSH_TYPE, pushData.getType());
        intent.putExtra(PUSH_DATA, pushData.getText());
        intent.putExtra(EXTRA_BOOKING_ID, pushData.getObjectId());
        intent.putExtra(EXTRA_WASHER_ID, pushData.getObjectId());
        intent.putExtra(EXTRA_CAMPAIGN_ID, pushData.getObjectId());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, NotificationId.getID(), intent, flags);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String text = pushData.getText() == null ? "" : pushData.getText();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("DriverSpa")
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NotificationId.getID(), builder.build());
        }
    }
}
