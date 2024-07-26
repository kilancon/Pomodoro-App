package com.pomodoro.model;

public class BlockedApp {
    private String packageName;
    private boolean isLocked;

    public BlockedApp(String packageName, boolean isLocked) {
        this.packageName = packageName;
        this.isLocked = isLocked;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    public String toString() {
        return "BlockedApp{" +
                "packageName='" + packageName + '\'' +
                ", isLocked=" + isLocked +
                '}';
    }
}
