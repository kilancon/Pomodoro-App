package com.pomodoro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pomodoro.activity.ChangeAppListActivity;
import com.pomodoro.activity.MainActivity;
import com.pomodoro.R;
import com.pomodoro.model.AppInfo;

import java.util.ArrayList;

public class FragmentConfig extends Fragment {
    private EditText timer, lbreak, sbreak;
    private Button btnChangelist;
    private Switch switch1, switch3;
    public FragmentConfig() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        SharedPreferences preferences = getActivity().getSharedPreferences("My_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        timer.setText(""+preferences.getInt("Focus time", 0));
        lbreak.setText(""+preferences.getInt("Long break", 0));
        sbreak.setText(""+preferences.getInt("Short break", 0));
        timer.addTextChangedListener(new MyTextWatcher(timer));
        lbreak.addTextChangedListener(new MyTextWatcher(lbreak));
        sbreak.addTextChangedListener(new MyTextWatcher(sbreak));

        Intent intentCl =  new Intent(getActivity(), ChangeAppListActivity.class);
//        intentCl.putExtra("dsApp", ((MainActivity)getActivity()).dsApp);
        btnChangelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<AppInfo> dsApp = ((MainActivity)getActivity()).dsApp;
                Log.e("Danh sach", dsApp.toString());
                startActivity(intentCl);
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Log.e("Testing ON", "bat switch");
                }
                else Log.e("Testing OFF", "tat switch");
            }
        });
        switch3.setChecked(preferences.getBoolean("Phone blocked", false));
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                editor.putBoolean("Phone blocked", isChecked);
                editor.apply();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    private void initView(View view){
        timer = view.findViewById(R.id.editFocus);
        lbreak = view.findViewById(R.id.editLbreak);
        sbreak = view.findViewById(R.id.editSbreak);
        btnChangelist = view.findViewById(R.id.btnChangelist);
        switch1 = view.findViewById(R.id.switch1);
        switch3 = view.findViewById(R.id.switch3);
    }

    private class MyTextWatcher implements TextWatcher{
        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            SharedPreferences preferences = getActivity().getSharedPreferences("My_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(!view.isFocused()) return;
            switch (view.getId()){
                case R.id.editFocus:
                    try {
                        editor.putInt("Focus time", Integer.parseInt(charSequence.toString()));
                        editor.apply();
                        ((MainActivity)view.getContext()).remainingSeconds = 60*preferences.getInt("Focus time", 0);
                        ((MainActivity)view.getContext()).mode = 0;
                        ((MainActivity)view.getContext()).intervalCount = 0;
                        Log.e("Config focus time",  ""+preferences.getInt("Focus time", 0));
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(getContext(), "Dien vao", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.editLbreak:
                    try {
                        editor.putInt("Long break", Integer.parseInt(charSequence.toString()));
                        editor.apply();
                        ((MainActivity)view.getContext()).mode = 0;
                        ((MainActivity)view.getContext()).intervalCount = 0;
                    }
                    catch (NumberFormatException e){
                        editor.putInt("Long break", 0);
                        lbreak.setText("0");
                    }
                    break;
                case R.id.editSbreak:
                    try {
                        editor.putInt("Short break", Integer.parseInt(charSequence.toString()));
                        editor.apply();
                        ((MainActivity)view.getContext()).mode = 0;
                        ((MainActivity)view.getContext()).intervalCount = 0;
                    }
                    catch (NumberFormatException e){
                        editor.putInt("Short break", 0);
                        sbreak.setText("0");
                    }
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
