package com.pomodoro;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.pomodoro.model.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static long DAY_IN_MILLIS = 86400 * 1000;

    public static int processTime(int hour, int minute, int second) {
        return hour*3600 + minute*60 + second;
    }

    public static int[] reverseProcessTime(int time) {
        int[] hourMinSec = new int[3];
        hourMinSec[0] = time / 3600;
        time = time % 3600;
        hourMinSec[1] = time / 60;
        hourMinSec[2] = time % 60;
        return hourMinSec;
    }

    public static ArrayList<AppInfo> getAppInfoList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        ArrayList<AppInfo> appInfoList = new ArrayList<>();

        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                String packageName = packageInfo.packageName;
                appInfoList.add(new AppInfo(appName,packageName, appIcon ,0));
            }
        }
        return appInfoList;
    }

    public static HashMap<String, Integer> getTimeSpent(Context context, String packageName, long beginTime, long endTime) {
        UsageEvents.Event currentEvent;
        ArrayList<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, Integer> appUsageMap = new HashMap<>();

        UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);

        while (usageEvents.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);
            if(currentEvent.getPackageName().equals(packageName) || packageName == null) {
                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                        || currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
                    allEvents.add(currentEvent);
                    String key = currentEvent.getPackageName();
                    if (appUsageMap.get(key) == null)
                        appUsageMap.put(key, 0);
                }
            }
        }

        for (int i = 0; i < allEvents.size() - 1; i++) {
            UsageEvents.Event E0 = allEvents.get(i);
            UsageEvents.Event E1 = allEvents.get(i + 1);

            if (E0.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                    && E1.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED
                    && E0.getClassName().equals(E1.getClassName())) {
                int diff = (int)(E1.getTimeStamp() - E0.getTimeStamp());
                diff /= 1000;
                Integer prev = appUsageMap.get(E0.getPackageName());
                if(prev == null) prev = 0;
                appUsageMap.put(E0.getPackageName(), prev + diff);
            }
        }

        if(allEvents.size() != 0) {
            UsageEvents.Event lastEvent = allEvents.get(allEvents.size() - 1);
            if (lastEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                String currentRunningPackageName = lastEvent.getPackageName();
                int diff = (int) System.currentTimeMillis() - (int) lastEvent.getTimeStamp();
                diff /= 1000;
                Integer prev = appUsageMap.get(currentRunningPackageName);
                if (prev == null) prev = 0;
                appUsageMap.put(currentRunningPackageName, prev + diff);
                appUsageMap.put("current" + currentRunningPackageName, -1); //for notification purpose
            }
        }

        return appUsageMap;
    }
}
