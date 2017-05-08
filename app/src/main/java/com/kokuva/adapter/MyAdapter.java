package com.kokuva.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexandre on 07/05/2017.
 */

public class MyAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> array;
    public MyAdapter(FragmentManager fm) {
        super(fm);
        array = new ArrayList<Fragment>();
    }

    public void addFragment(Fragment f){
        array.add(f);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Fragment getItem(int position) {
        return array.get(position);
    }
}