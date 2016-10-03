package com.mbientlab.metawear.starter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by milosberka on 30.8.2016.
 */


public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Breeds", "Image"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getCount() {
        return PAGE_COUNT;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainActivityFragment();
            case 1:
                return MapFragmentActivity.newInstance("", "");
            default:
                return new MainActivityFragment();
        }
    }

    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}