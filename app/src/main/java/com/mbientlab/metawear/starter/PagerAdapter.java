package com.mbientlab.metawear.starter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by milosberka on 30.8.2016.
 */


public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Monitor", "Map"};
    FragmentManager fragm;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        fragm = fm;
    }

    public int getCount() {
        return PAGE_COUNT;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainActivityFragment();
            case 1:
                //SupportMapFragment mapFragment = (SupportMapFragment) fragm.findFragmentById(R.id.map);
                //return mapFragment;
                return MapFragmentActivity.newInstance(fragm, "");
            default:
                return new MainActivityFragment();
        }
    }

    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}