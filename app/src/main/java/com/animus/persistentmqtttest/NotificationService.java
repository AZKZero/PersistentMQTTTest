package com.animus.persistentmqtttest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {
    public static final String TAG = "serv";
    /**
     * Channel ID for notification Oreo+
     */
    public static final String CHANNEL_ID="MQTTPersistenceTest";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: started");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(9, showNotification("X", "X Running", getApplicationContext())); //Anything Other Than 0
        }
        ConnectionManager.makeConnectionHolder(getApplicationContext());
        ConnectionManager.subscribe("test");
        return START_STICKY;
    }

    /**
     * <p>Used in the foreground service.<br>The notification must show as long as the service is running.</p>
     * @param title String for the title
     * @param message String for the message
     * @param context String for the title
     * @return The Notification Object
     */
    private static Notification showNotification(String title, String message, Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "mqttPersistenceTest",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for Persistence Test of MQTT");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setColorized(true)
//                .setVisibility()
                .setColor(context.getResources().getColor(R.color.colorPrimary)); //Notification Color
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 6, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        return mBuilder.build();
    }
}
