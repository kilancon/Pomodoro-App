package com.pomodoro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.pomodoro.activity.MainActivity;
import com.pomodoro.R;
import com.pomodoro.TimerTask;
import com.pomodoro.dal.DatabaseHelper;
import com.pomodoro.model.Task;
import com.pomodoro.service.MyOverlayPhoneService;


public class FragmentTimer extends Fragment {
    private static final int FOCUS_TIME = 0;
    private static final int SHORT_BREAK = 1;
    private static final int LONG_BREAK = 2;
    private TextView tvHead, tvTimer, tvTaskname, tvEst;

    private LinearLayout layout;
    private CircularProgressIndicator progressBar;
    private Button btnStart, btnStop, btnSelect;
    private ImageButton btnSkip, btnCheck, btnClear;
    private int focustime, lbreak, sbreak;
    private Task task;
    private TimerTask timerAT;
    public FragmentTimer() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        SharedPreferences preferences = getActivity().getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        focustime = preferences.getInt("Focus time", 25);
        lbreak = preferences.getInt("Long break", 10);
        sbreak = preferences.getInt("Short break", 5);
        Log.e("Remaining time", ""+((MainActivity)getActivity()).remainingSeconds);
        // chia theo timer 1 2 3
        switch (((MainActivity)getActivity()).mode){
            case FOCUS_TIME:
                tvTimer.setText(String.format("%02d : %02d",((MainActivity)getActivity()).remainingSeconds/60 , ((MainActivity)getActivity()).remainingSeconds%60));
                progressBar.setMax(focustime*60);
                Log.e("Focus time", ""+focustime*60);
                Log.e("Remaining time", ""+((MainActivity)getActivity()).remainingSeconds);
                progressBar.setProgress(((MainActivity)getActivity()).remainingSeconds);
                break;
            case SHORT_BREAK:
                tvTimer.setText(String.format("%02d : %02d",((MainActivity)getActivity()).remainingSeconds/60 , ((MainActivity)getActivity()).remainingSeconds%60));
                progressBar.setMax(sbreak*60);
                tvHead.setText("Short break");
                progressBar.setProgress(((MainActivity)getActivity()).remainingSeconds);
                break;
            case LONG_BREAK:
                tvTimer.setText(String.format("%02d : %02d",((MainActivity)getActivity()).remainingSeconds/60 , ((MainActivity)getActivity()).remainingSeconds%60));
                progressBar.setMax(lbreak*60);
                tvHead.setText("Long break");
                progressBar.setProgress(((MainActivity)getActivity()).remainingSeconds);
                break;
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.task_select != null){
            task = mainActivity.task_select;
            layout.setVisibility(View.VISIBLE);
            btnSelect.setVisibility(View.INVISIBLE);
            btnCheck.setImageResource(task.getState()?R.drawable.btncheck2:R.drawable.btncheck);
            tvTaskname.setText(task.getName());
            tvTaskname.setPaintFlags(task.getState()?tvTaskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG:0);
            tvTaskname.setTextColor(task.getState()?getResources().getColor(R.color.light_brown):getResources().getColor(R.color.white));
            tvEst.setText(task.getCount()+"/"+task.getEst());
            tvEst.setTextColor(task.getState()?getResources().getColor(R.color.light_brown):getResources().getColor(R.color.white));
        }

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Interval", ""+mainActivity.intervalCount);
                if(mainActivity.mode != FOCUS_TIME){
                    tvHead.setText("Focus time");
                    btnStop.callOnClick();
                    mainActivity.mode = FOCUS_TIME;
                    mainActivity.remainingSeconds = focustime * 60;
                    progressBar.setMax(focustime * 60);
                    progressBar.setProgress(focustime * 60);
                    tvTimer.setText(String.format("%02d : 00", focustime));
                }
                 else if(mainActivity.intervalCount < 3){
                     mainActivity.intervalCount = mainActivity.intervalCount+1;
                    tvHead.setText("Short break");
                    btnStop.callOnClick();
                    mainActivity.mode = SHORT_BREAK;
                    mainActivity.remainingSeconds = sbreak * 60;
                    progressBar.setMax(sbreak * 60);
                    progressBar.setProgress(sbreak * 60);
                    tvTimer.setText(String.format("%02d : 00", sbreak));
                }
                else{
                    mainActivity.intervalCount = 0;
                    tvHead.setText("Long break");
                    btnStop.callOnClick();
                    mainActivity.mode = LONG_BREAK;
                    mainActivity.remainingSeconds = lbreak * 60;
                    progressBar.setMax(lbreak * 60);
                    progressBar.setProgress(lbreak * 60);
                    tvTimer.setText(String.format("%02d : 00", lbreak));
                }
            }
        });
        //
        Intent intentOverlay = new Intent(getActivity(), MyOverlayPhoneService.class);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mainActivity.mode){
                    case FOCUS_TIME:
                        boolean isPhoneBlocked = preferences.getBoolean("Phone blocked", false);
                        if(isPhoneBlocked){
                            getActivity().startService(intentOverlay);
                            btnSkip.callOnClick();
                            break;
                        }
                        timerAT = new TimerTask(mainActivity, focustime);
                        timerAT.execute();
                        break;
                    case SHORT_BREAK:
                        timerAT = new TimerTask(mainActivity, sbreak);
                        timerAT.execute();
                        break;
                    case LONG_BREAK:
                        timerAT = new TimerTask(mainActivity, lbreak);
                        timerAT.execute();
                        break;
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerAT!=null) timerAT.cancel(true);
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = getActivity().findViewById(R.id.viewP);
                viewPager.setCurrentItem(3, false);
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.task_select = null;
                layout.setVisibility(View.GONE);
                btnSelect.setVisibility(View.VISIBLE);
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.task_select.getState()) {
                    btnCheck.setImageResource(R.drawable.btncheck);
                    tvTaskname.setPaintFlags(0);
                    tvTaskname.setTextColor(getResources().getColor(R.color.white));
                    tvEst.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    btnCheck.setImageResource(R.drawable.btncheck2);
                    tvTaskname.setPaintFlags(tvTaskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTaskname.setTextColor(getResources().getColor(R.color.light_brown));
                    tvEst.setTextColor(getResources().getColor(R.color.light_brown));
                }
                mainActivity.task_select.setState(!mainActivity.task_select.getState());
                DatabaseHelper dbHelper = new DatabaseHelper(mainActivity.getBaseContext());
                dbHelper.updateTask(mainActivity.task_select);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timerAT!=null) timerAT.cancel(true);
    }

    private void initView(View view){
        tvHead = view.findViewById(R.id.tvHead);
        tvTimer = view.findViewById(R.id.tvTimer);
        progressBar = view.findViewById(R.id.progress);
        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        btnSkip = view.findViewById(R.id.btnSkip);
        btnSelect = view.findViewById(R.id.btnSelectTask);
        //task selected:
        btnClear = view.findViewById(R.id.btnClear);
        tvTaskname = view.findViewById(R.id.tvName);
        tvEst = view.findViewById(R.id.tvEst);
        btnCheck = view.findViewById(R.id.imgState);
        btnClear = view.findViewById(R.id.btnClear);
        layout = view.findViewById(R.id.selectedTask);
    }


}
