package com.pomodoro.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pomodoro.fragment.FragmentConfig;
import com.pomodoro.fragment.FragmentStat;
import com.pomodoro.fragment.FragmentTimer;
import com.pomodoro.fragment.FragmentUser;

public class MenuViewPagerAdapter extends FragmentStatePagerAdapter  {

    public MenuViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentTimer();
            case 1: return new FragmentStat();
            case 2: return new FragmentConfig();
            case 3: return new FragmentUser();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
