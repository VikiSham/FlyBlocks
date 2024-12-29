package com.example.flyblocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity implements android.hardware.SensorEventListener {
    FrameLayout frm;
    PlayView playView;
    String direction="right";

    Button bLeft,bRight,bPause;
    boolean flag=true;
    TextView tvscore,tvlive;

    AlertDialog.Builder adb;
    AlertDialog ad;

    private SensorManager senSensorManager;
    private float deltax=0,deltay=0,deltaz=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        frm=findViewById(R.id.frm);
        bRight=findViewById(R.id.bright);
        bLeft=findViewById(R.id.bleft);
        bPause=findViewById(R.id.bpause);
        tvscore=findViewById(R.id.tvScore);
        tvlive=findViewById(R.id.tvLive);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senSensorManager.registerListener(this, senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


    }

    BroadcastReceiver battaryReciver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("TAG", "onReceive: ACTION_BATTERY_LOW");
            if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {

                Toast.makeText(context, "Low Battery!!!",Toast.LENGTH_SHORT).show();
            }}
    };

    BroadcastReceiver phoneReciver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Toast.makeText(context, "Ringing", Toast.LENGTH_SHORT).show();
            playView.stop();
            bPause.setBackgroundResource(R.drawable.play);
        }
        if(state.equals(TelephonyManager.CALL_STATE_IDLE))
            Toast.makeText(context, "STATE_IDLE", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {

            int w=frm.getWidth();
            int h=frm.getHeight();
            playView=new PlayView(this,w,h,scoreLiveHandler);
            frm.addView(playView);
        }
        registerReceiver(battaryReciver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(phoneReciver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
    }

    public void pause(View view) {
        flag = playView.doPause();
        if (!flag)
            bPause.setBackgroundResource(R.drawable.play);
        else
            bPause.setBackgroundResource(R.drawable.pause);

    }

    @Override
    protected void onDestroy() {

        playView.stop();
        unregisterReceiver(battaryReciver);
        unregisterReceiver(phoneReciver);
        senSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    public void changeDirection(View view) {
        direction=view.getTag().toString();
        playView.changeDirection(direction);
    }


    // משתנה שדרכו נקבל הודעה מתוך view כשיש הוספת ניקוד, בכתיבת פעולה יש לבחור אופציה ראשונה Handler android.os
    private Handler scoreLiveHandler=new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int score=msg.getData().getInt("score"); // מקבל מידע מתוך view כמו ב intent לפי המפתח אם אין ערך לקבל יכניס 0
            int life=msg.getData().getInt("life"); // מקבל מידע מתוך view כמו ב intent לפי המפתח אם אין ערך לקבל יכניס 0
            tvscore.setText("Score: "+score);
            tvlive.setText("   "+life);

            if(life==0)
            {
                playView.stop();
                adb=new AlertDialog.Builder(PlayActivity.this);
                adb.setTitle("Game Over!!!");
                adb.setMessage("\nYour total score is: "+score);
                adb.setIcon(R.drawable.gameover);
                adb.setCancelable(false);

                adb.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //btnPause.setBackgroundResource(R.drawable.play);
                        playView.newGame();
                    }
                });
                adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                });
                ad=adb.create();
                ad.show();
            }

        }

        };

    // Sensor event from ACCELEROMETER
    // get deltaX and deltaY and move the image accordingly
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            deltax = sensorEvent.values[0];
            deltay = sensorEvent.values[1];
            deltaz = sensorEvent.values[2];

            /*ivMain.setX(ivMain.getX() - deltax);
            if(ivMain.getY() + deltay<1200) // screen heigth
                ivMain.setY(ivMain.getY() + deltay);*/
            if(playView!=null)
                playView.pedMove(deltax);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}