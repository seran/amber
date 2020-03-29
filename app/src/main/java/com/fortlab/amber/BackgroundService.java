package com.fortlab.amber;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static com.fortlab.amber.MainActivity.CHANNEL_ID;

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    private Runnable myTask = new Runnable() {
        @Override
        public void run() {
            Log.d("AMBER","Service started");
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Amber").setContentText("Receiver activated..")
                    .setContentIntent(pendingIntent).build();

            startForeground(1, notification);
        }

        return START_NOT_STICKY;
    }
}
