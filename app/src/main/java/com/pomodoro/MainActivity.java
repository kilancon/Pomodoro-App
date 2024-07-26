package com.pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pomodoro.adapter.MenuViewPagerAdapter;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.AppInfo;
import com.pomodoro.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public Task task_select;
    public int remainingSeconds, mode = 0, intervalCount = 0;
    public ArrayList<AppInfo> dsApp;

    private ViewPager viewPager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //change status bar and navigation bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        window.setNavigationBarColor(getColor(R.color.light_brown));
        //
        initDsApp();
        //testing dsblock:
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Log.e("Danh sach block", databaseHelper.getAllBlockedApp().toString());
        //
        remainingSeconds = 60*this.getSharedPreferences("My_pref", Context.MODE_PRIVATE).getInt("Focus time", 25);
        viewPager = findViewById(R.id.viewP);
        navigationView = findViewById(R.id.navi);
        MenuViewPagerAdapter adapter = new MenuViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: navigationView.getMenu().findItem(R.id.mTimer).setChecked(true);
                        break;
                    case 1: navigationView.getMenu().findItem(R.id.mStat).setChecked(true);
                        break;
                    case 2: navigationView.getMenu().findItem(R.id.mConfig).setChecked(true);
                        break;
                    case 3: navigationView.getMenu().findItem(R.id.mUser).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mTimer:
                        viewPager.setCurrentItem(0); break;
                    case R.id.mStat:
                        viewPager.setCurrentItem(1); break;
                    case R.id.mConfig:
                        viewPager.setCurrentItem(2); break;
                    case R.id.mUser:
                        viewPager.setCurrentItem(3); break;
                }
                return true;
            }
        });
    }

    private void initDsApp() {
        dsApp = new ArrayList<>();
        PackageManager pm = MainActivity.this.getPackageManager();
        List<ApplicationInfo> appList = pm.getInstalledApplications(0);
        for(ApplicationInfo app : appList){
            if( pm.getLaunchIntentForPackage(app.packageName) != null ) { //not system app
                try {
                    AppInfo appxin = new AppInfo(pm.getApplicationLabel(app).toString(), app.packageName, pm.getApplicationIcon(app.packageName), 0);
                    dsApp.add(appxin);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}