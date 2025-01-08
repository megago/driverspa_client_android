/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.driverspa.gcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;
import java.util.List;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.UserSelfAssist;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.db.WashmeOrmLiteSqlHelper;
import com.driverspa.model.FareRequest;
import com.driverspa.model.NotificationId;
import com.driverspa.model.NotificationType;
import com.driverspa.model.PushData;
import com.driverspa.model.User;
import com.driverspa.util.Constants;
import com.driverspa.util.Singleton;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.NewBookingPushRequestEvent;
import com.driverspa.util.otto.NewFareRequestBidEvent;
import com.driverspa.util.otto.ws.AuthClientLogoutRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.EXTRA_WASHER_ID;
import static com.driverspa.util.Constants.PUSH_DATA;
import static com.driverspa.util.Constants.PUSH_TYPE;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "YERZHAN";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

     if(UserPreferences.isUserLoggedIn(this)) {
        User user = UserSelfAssist.getUserFromDb(UserPreferences.getUserId(BA.getContext()));
        if (user == null) {
            //If database was recreated then ask for user data again
            String userId = UserPreferences.getUserId(BA.getContext());
            if (!TextUtils.isEmpty(userId)) {
                BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
            }
          } else {
            PushData data = PushData.deserialize(message);

            int notifyId = NotificationId.getID();
            Intent intent = null;
            intent = new Intent(getApplicationContext(), ClientHomeActivity.class);
            intent.putExtra(PUSH_TYPE, data.getType());
            intent.putExtra(PUSH_DATA, data.getText());
            intent.putExtra(EXTRA_BOOKING_ID, data.getObjectId());
            intent.putExtra(EXTRA_WASHER_ID, data.getObjectId());
            intent.putExtra(Constants.EXTRA_CAMPAIGN_ID, data.getObjectId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(data.getType().contains("book") || data.getType().contains("serving") || data.getType().contains("express")) {
                BA.getEventBus().post(new NewBookingPushRequestEvent(data));
            }
            else if(data.getType().contains("bid")) {
               if(!TextUtils.isEmpty(UserPreferences.getActiveFareRequest(BA.getContext()))){
                     FareRequest fareRequest = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
                     if(fareRequest.getActgiveBids() != null){
                         List<FareRequest.BidObject> bids = fareRequest.getActgiveBids();
                         bids.add(data.getBidObject());
                     }
                     else{
                         List<FareRequest.BidObject> bids = new ArrayList<>();
                         bids.add(data.getBidObject());
                         fareRequest.setActgiveBids(bids);
                     }
                     UserPreferences.putActiveFareRequestBooking(BA.getContext(),fareRequest.serialize());
                     BA.getEventBus().post(new NewFareRequestBidEvent());
               }

               if(BA.getNotificationType() != NotificationType.NOTHING) {
                   return;
               }
            }
            else if(data != null && data.getType().contains("session_end")) {
                UserPreferences.onUserLogout(BA.getContext());
                user = null;
                WashmeOrmLiteSqlHelper dbHelper = new WashmeOrmLiteSqlHelper(BA.getContext());
                dbHelper.removeAll();
                dbHelper.close();
                BA.getEventBus().post(new UserGetSelfResponseEvent(null));
                BA.getEventBus().post(new AuthClientLogoutRequestEvent());

                if(BA.getNotificationType() != NotificationType.NOTHING) {
                    Singleton.displayToast(data.getText());
                    return;
                }
            }

            int requestID = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getActivity(BA.getContext(), requestID /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notificationBuilder = null;
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Wash!me");
            bigTextStyle.bigText(data.getText());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notificationBuilder = new NotificationCompat.Builder(BA.getContext())
                        .setSmallIcon(R.drawable.ic_launch)
                        .setContentTitle("Wash!me")
                        .setContentText(data.getText())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setStyle(bigTextStyle)
                        .build();
            } else {
                notificationBuilder = new Notification.Builder(BA.getContext())
                        .setSmallIcon(R.drawable.ic_launch)
                        .setContentTitle("Wash!me")
                        .setContentText(data.getText())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .build();
            }

            notificationBuilder.defaults = Notification.DEFAULT_ALL;
            notificationBuilder.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) BA.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /* Create or update. */
                NotificationChannel channel = new NotificationChannel("my_channel_01", "Wash!me", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

//            notificationManager.notify(notifyId /* ID of notification */, notificationBuilder);
            showNotification(this,notifyId,"Wash!me",data.getText(),intent);
        }
      }
    }

    public void showNotification(Context context, int notificationId, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-01";
        String channelName = "Wash!me channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setSmallIcon(R.drawable.ic_launch_white);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification note = mBuilder.build();
//        note.defaults = Notification.DEFAULT_ALL;
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, note);
    }
}
