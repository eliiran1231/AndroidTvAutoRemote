package com.example.androidtvremote.logic;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import  androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.androidtvremote.ui.RemoteFragment;
import com.example.androidtvremote.ui.AppsFragment;
import com.example.androidtvremote.ui.AutoFragment;

public class PagerAdapter extends FragmentStateAdapter {
    private int numOfTabs;

    public PagerAdapter(@NonNull FragmentManager fragmentActivity, @NonNull Lifecycle lifecycle, int numOfTabs){
        super(fragmentActivity,lifecycle);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
     public Fragment createFragment(int position){
        if(position == 1) return new AutoFragment();
        if(position == 2) return new AppsFragment();
        return new RemoteFragment();
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }


}
