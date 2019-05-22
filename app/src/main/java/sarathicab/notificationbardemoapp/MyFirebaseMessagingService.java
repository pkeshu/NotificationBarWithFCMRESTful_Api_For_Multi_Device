package sarathicab.notificationbardemoapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static sarathicab.notificationbardemoapp.App.CHANNEL_ID;
import static sarathicab.notificationbardemoapp.MainActivity.FROMLOCATION;
import static sarathicab.notificationbardemoapp.MainActivity.PRICE;
import static sarathicab.notificationbardemoapp.MainActivity.TOKEN;
import static sarathicab.notificationbardemoapp.MainActivity.TOLOCATION;
import static sarathicab.notificationbardemoapp.MainActivity.TRIPID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String ACTION_SNOOZE = "";
    public static String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private String GROUP_KEY= "sarathicab.notificationbardemoapp.NOTI_GROUP";
    int SUMMARY_ID = 1;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM Token:", "Fresh fcm device token: " + token);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.FCM_TOEKN), token);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()>0){
            Log.d("kesharpaudel hello", "Message data payload: " + remoteMessage.getData());
        }
        if(remoteMessage.getData()!=null){
            Log.d("kesharpaudel hello", "Message data payload: " + remoteMessage.getData().toString());
        }
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String access = "accept";
        String detail = "detail";

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Map<String,String> msg=remoteMessage.getData();
        Log.d("wowow:",msg.get("tripFee"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent accessIntent = new Intent(this, NotificationReceiver.class);
        accessIntent.putExtra("message", access);
        accessIntent.putExtra(PRICE, msg.get("tripFee"));
        accessIntent.putExtra(FROMLOCATION, msg.get("fromLocation"));
        accessIntent.putExtra(TOLOCATION,msg.get("toLocation"));
        accessIntent.putExtra(TOKEN, msg.get("token"));
        accessIntent.putExtra(TRIPID,msg.get("tripId"));
        accessIntent.putExtra("notificationId",0);
        accessIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent accessPendIntent = PendingIntent.getBroadcast(this,
                0, accessIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent detailIntent = new Intent(this, MainActivity.class);
        PendingIntent detailPendIntent = PendingIntent.getActivity(this,
                0, detailIntent, 0);
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
        notificationLayout.setImageViewResource(R.id.circleImageView2,R.drawable.sar);
        notificationLayout.setTextViewText(R.id.trip_fee,"New Trip: Rs "+msg.get("tripFee"));
        notificationLayout.setTextViewText(R.id.to_location,"To: "+msg.get("toLocation"));
        notificationLayout.setTextViewText(R.id.from_location,"From: "+msg.get("fromLocation"));
        notificationLayout.setTextViewText(R.id.accept_notification,"Accept");
        notificationLayout.setTextViewText(R.id.detail_notification,"Details");
        notificationLayout.setOnClickFillInIntent(R.id.accept_notification,accessIntent);
        notificationLayout.setOnClickPendingIntent(R.id.accept_notification,accessPendIntent);
        notificationLayout.setOnClickFillInIntent(R.id.detail_notification,detailIntent);
        notificationLayout.setOnClickPendingIntent(R.id.detail_notification,detailPendIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.sarathi)
                .setContent(notificationLayout)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY)
                .setCustomBigContentView(notificationLayout);
        Notification summaryNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Trips")
                        //set content text to support devices running API level < 24
                        .setSmallIcon(R.drawable.sarathi)
                        //build summary info into InboxStyle template
                        .setGroup(GROUP_KEY)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();
        notificationManager.notify(0, builder.build());
        notificationManager.notify(SUMMARY_ID,summaryNotification);
    }
}
