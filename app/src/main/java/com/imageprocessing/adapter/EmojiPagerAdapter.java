package com.imageprocessing.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by abhishekkumar on 12/07/17.
 */

public class EmojiPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;

    public EmojiPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments == null) {
            return (null);
        }
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}