package com.fortlab.amber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private Context thisContext;

    private Button responseButton;
    private Button serviceStateButton;

    private static MainActivity ins;

    public static final String CHANNEL_ID = "amberServiceChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.thisContext = this;
        ins = this;
        this.responseButton = (Button) findViewById(R.id.response_button);
        this.serviceStateButton = (Button) findViewById(R.id.stop_background_service);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1001);
        }
//         TODO: Remove the below code if test passed
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1002);
//        }

        createNotificationChannel();

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("inputExtra", "Test");
        startService(serviceIntent);

        if (this.isServiceRunning(AlarmService.class)) {
            this.responseButton.setEnabled(true);
        }
        if (!this.isServiceRunning(BackgroundService.class)){
            this.serviceStateButton.setEnabled(false);
        }


    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static MainActivity  getInstace(){
        return ins;
    }

    public void enableResponseButton() {
        this.responseButton.setEnabled(true);
    }

    public void stopService(View v) {
        this.serviceStateButton.setEnabled(false);
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);
    }

    public void startAlarmService(View v) {
        Intent serviceIntent = new Intent(this, AlarmService.class);
        startService(serviceIntent);
    }

    public void sendAmberAlert(View v) {
        SmsManager smsManager = SmsManager.getDefault();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList arrayList = databaseHelper.getAllNumbers();

        ListIterator<String> iterator = arrayList.listIterator();

        while (iterator.hasNext()) {
            smsManager.sendTextMessage(iterator.next(), null, "AMBER",null,null);
        }

        Toast.makeText(this, "Alert send", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarmService(View v) {
        Intent serviceIntent = new Intent(this, AlarmService.class);
        stopService(serviceIntent);
        responseButton.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No SMS permission granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 1001) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Read contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No read contacts permission granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 1002) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Send SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Send SMS permission granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

            Intent notificationInent = new Intent(this, MainActivity.class);
            notificationInent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contacts:
                Intent contactListIntent = new Intent(getBaseContext(), ContactListActivity.class);
                startActivity(contactListIntent);
                break;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "About Selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
