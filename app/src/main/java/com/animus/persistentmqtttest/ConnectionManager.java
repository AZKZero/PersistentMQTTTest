package com.animus.persistentmqtttest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class ConnectionManager {

    public static MqttAndroidClient mqat;
    public static PahoMqttClient pqat;
    private static Context context;
    private static Notification mBuilder;
    public static Thread connThread, toastThread;
    public static String temp;
    public static NotificationManager notificationManager;
    public static JSONObject RECEIVED_JSON;
    public static String locationString, dateTimeString, dateString, timeString;
    public static boolean isMade = false;
    public static int notiId = 0;

    /**
     * This function initializes the mqttClient Object which controls the mqtt
     *
     * @param givenCont A Context Object
     */
    public static void makeConnectionHolder(final Context givenCont) {
        if (!isMade) {
            context = givenCont;
            Intent homeIntent = new Intent(givenCont, MainActivity.class);
            pqat = new PahoMqttClient();
            WifiManager wm = (WifiManager) givenCont.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo in = wm.getConnectionInfo();
            String xh = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            Log.d("andro", xh); // Device Specific Client ID (has to be unique)
            mqat = pqat.getMqttClient(givenCont.getApplicationContext(), ConnectionCredentials.BROKER_URL, xh);
            mqat.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Log.d("msg_top", s);
                    temp = mqttMessage.toString();
                    final int chunkSize = 2048;
                    for (int i = 0; i < mqttMessage.toString().length(); i += chunkSize) {
                        Log.d("response", mqttMessage.toString().substring(i, Math.min(mqttMessage.toString().length(), i + chunkSize)));
                    }

                    if (s.contains("YOUR_TOPIC")) {  //You can use equals if you like
                        //Do Some Stuff

                    }

                    if (s.contains("test")) {

                        context.sendBroadcast(new Intent().setAction("YOUR_FILTER").putExtra("YOUR_DATA_KEY", "YOUR_DATA"));

                        Toast.makeText(context, "Message Arrived", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            isMade = true;
        }
    }

    /**
     * Subscribes to a topic, using a separate thread
     * @param topic String topic
     */
    public static void subscribe(final String topic) {
        connThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mqat.isConnected()) {
                            pqat.subscribe(mqat, topic, 1);
                            return;
                        }
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException ex) {
                        continue;
                    }
                }
            }
        });
        connThread.setName("conn");
        connThread.start();
    }

    /**
     * Publishes Using a Separate Thread
     * @param message String message
     * @param topic String topic
     * @param retain boolean retain
     */
    public static void publish(final String message, final String topic, final boolean retain) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        if (mqat.isConnected()) {
                            pqat.publishMessage(mqat, message, 1, topic, retain);
                            break;
                        }
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }
}
