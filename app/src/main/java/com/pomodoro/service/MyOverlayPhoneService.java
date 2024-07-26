package com.pomodoro.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pomodoro.R;

public class MyOverlayPhoneService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    private Handler handler;
    private int remainingsecond;
    private MediaPlayer mediaPlayer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a new WindowManager and set the layout parameters
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm1);
        // Inflate the overlay view from a layout resource file
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_phonescreen, null);
        TextView tvTimer = overlayView.findViewById(R.id.tvTimer2);
        tvTimer.setText(String.format("%02d:00",getSharedPreferences("My_pref", Context.MODE_PRIVATE).getInt("Focus time", 25)));
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                int secs = msg.arg1;
                int mm = secs/60;
                int ss = secs%60;
                tvTimer.setText(String.format("%02d:%02d", mm, ss));
            }
        };
        MyThread thread = new MyThread();
        thread.start();
        // Add the overlay view to the WindowManager
        windowManager.addView(overlayView, layoutParams);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        remainingsecond = 60*preferences.getInt("Focus time", 25);

    }

    class MyThread extends Thread{
        @Override
        public void run() {
            while(remainingsecond > 0){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                remainingsecond--;
                Message ms = handler.obtainMessage();
                ms.arg1 = remainingsecond;
                handler.sendMessage(ms);
            }
            mediaPlayer.start();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the overlay view from the WindowManager
        windowManager.removeView(overlayView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

