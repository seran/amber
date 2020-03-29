package com.fortlab.amber;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private Ringtone ringtone;

    private AudioManager audioManager;

    private int currentVolume;

    @Override
    public void onCreate() {
        super.onCreate();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

//        ringtone.play();
        Log.d("AMBER","Alarm Sounds started..");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        this.currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        ringtone.play();
        MainActivity.getInstace().enableResponseButton();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) / 4;
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, this.currentVolume, 0);
        ringtone.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
