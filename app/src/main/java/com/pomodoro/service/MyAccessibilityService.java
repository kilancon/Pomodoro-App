package com.pomodoro.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.BlockedApp;

import java.util.ArrayList;


public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        DatabaseHelper dbhelper = new DatabaseHelper(this);
        ArrayList<BlockedApp> dsblock = dbhelper.getAllBlockedApp();
        for(BlockedApp app: dsblock){
            if(packageName.equals(app.getPackageName())) {
                //start overlay service
                performGlobalAction(GLOBAL_ACTION_HOME);
                Intent intentov = new Intent(this, MyOverlayAppService.class);
                startService(intentov);
                Log.e("Blocked", packageName);
            }
        }

        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e("Accessibility event", event.getPackageName().toString());
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.e("Interrupted", "testing");
    }

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);
        Log.e("Connected", "testing");
    }

}
