package com.pomodoro.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.pomodoro.R;

public class MyOverlayAppService extends Service {
    private WindowManager windowManager;
    private View overlayView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a new WindowManager and set the layout parameters
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Inflate the overlay view from a layout resource file
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_app, null);
        ImageButton btnClose = overlayView.findViewById(R.id.btnX);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Click roi", "....");
                stopService(intent);
            }
        });
        // Add the overlay view to the WindowManager
        windowManager.addView(overlayView, layoutParams);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public boolean stopService(Intent name) {
        Log.e("Stopppp", "...");
        windowManager.removeView(overlayView);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

