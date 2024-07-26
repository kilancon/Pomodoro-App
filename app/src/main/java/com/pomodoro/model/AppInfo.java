package com.pomodoro.model;

import android.graphics.drawable.Drawable;

public class AppInfo implements Comparable<AppInfo>{
    private String appName;
    private String packageName;
    private Drawable icon;
    private long timeUsage;

    public AppInfo(){}

    public AppInfo(String appName, String packageName, Drawable icon, long timeUsage) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.timeUsage = timeUsage;
    }

    public String getTime(){
        int sec = (int) (timeUsage/1000);
        int hh = sec/3600;
        int mm = (sec%3600)/60;
        return String.format("%02dh %02dm",hh,mm); //phai sua lai
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(long timeUsage) {
        this.timeUsage = timeUsage;
    }

    @Override
    public int compareTo(AppInfo appInfo) {
        return (int) (appInfo.timeUsage - timeUsage);
    }
}