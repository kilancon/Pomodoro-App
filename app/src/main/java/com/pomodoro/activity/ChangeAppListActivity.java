package com.pomodoro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pomodoro.R;
import com.pomodoro.Utils;
import com.pomodoro.adapter.AppBlockAdapter;
import com.pomodoro.model.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangeAppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_app_list);
        //change status bar color
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.light_brown));
        //
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        RecyclerView rv = findViewById(R.id.lv);
        Intent intent = this.getIntent();
        ArrayList<AppInfo> dsApp ;
        AppBlockAdapter adapter = new AppBlockAdapter(this, Utils.getAppInfoList(this));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    }


}