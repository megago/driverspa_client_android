package com.driverspa.service;

import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.NotificationId;
import com.driverspa.model.BookInfo;
import com.driverspa.util.Functions;
import com.driverspa.util.L;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.SmsRecoveryReceivedEvent;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;

/**
 * Created by Yerzhan Tanatov on 18/12/15.
 */
public class Service extends android.app.Service {
    public final static String NOTIFICATION = "NOTIFICATION";

//    private final int UPDATE_INTERVAL = 15*60*1000; //15 minutes
    private final int UPDATE_INTERVAL = 1000; //1 second
    private Timer timer = new Timer();
    private Handler mHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // code to execute when the service is first created
        startForeground(1,new Notification());
        BA.getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            BA.getEventBus().unregister(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
           timer.scheduleAtFixedRate(new TimerTask() {
               @Override
               public void run() {
                   mHandler.post(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               smsParsing();
                           } catch (Exception e) {
                               L.d("Some exception in service" + e.getMessage());
                           }
                       }
                   });
               }

           }, 0, UPDATE_INTERVAL);

        return START_STICKY;
    }

    private void smsParsing(){
        long smsLastSentTime = UserPreferences.getRecoverySmsSentTime(BA.getContext());
        if(smsLastSentTime > 0) {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = Service.this.getContentResolver().query(uriSms, null, null, null, null);
            Cursor cursor1 = getContentResolver().query(uriSms, new String[]{"_id", "thread_id", "address", "person", "date", "protocol", "read", "status", "type", "reply_path_present", "subject", "body", "service_center", "locked"}, null, null, null);
            String[] columns = new String[]{"_id", "thread_id", "address", "person", "date", "protocol", "read", "status", "type", "reply_path_present", "subject", "body", "service_center", "locked"};
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                while (true) {
                    String phone = cursor1.getString(cursor1.getColumnIndex(columns[2])).replaceAll(" ", "");
                    String body = cursor1.getString(cursor1.getColumnIndex(columns[11]));
                    String date = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                    if (Long.parseLong(date) > smsLastSentTime) {
                        if (body.contains("Wash!me code")) {
                            try {
                                String parseBody = Functions.Split(body, ":", 1);
                                BA.getEventBus().post(new SmsRecoveryReceivedEvent(parseBody.trim()));
                                break;
                            } catch (Exception e) {
                                Log.d("YERZHAN", "exception " + e.getMessage());
                            }
                        }
                    }
                    if (!cursor1.moveToNext()) break;
                }
            }
        }
    }

    private void generateLocalBooksNotification(){
//        LocalBookingList localList = LocalBookingList.deserialize(UserPreferences.getLocalBookingList(BA.getContext()));
//        if(localList != null && localList.getBooks() != null){
//            for(BookInfo book : localList.getBooks()){
//                if(book.getStatus().equals(Constants.APPROVED)
////                        && book.getServerTime().getTime() > book.getEndTime().getTime()
//                        ){
////                    showAdminNotification(book,"Время мойки клиента "+book.getClientKey()+" закончен, необходимо завершить услугу");
//                }
//            }
//        }
    }

    private void stopService() {
        if (timer != null) timer.cancel();
    }
}
