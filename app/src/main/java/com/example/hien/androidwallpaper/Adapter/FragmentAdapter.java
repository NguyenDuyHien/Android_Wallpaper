package com.example.hien.androidwallpaper.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hien.androidwallpaper.Fragment.CategoryFragment;
import com.example.hien.androidwallpaper.Fragment.RecentFragment;
import com.example.hien.androidwallpaper.Fragment.TrendingFragment;

/**
 * Created by Hien on 24/03/2018.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private Context context;

    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return TrendingFragment.getInstance();
        else if(position == 1)
            return CategoryFragment.getInstance();
        else if(position == 2)
            return RecentFragment.getInstance(context);
        else
            return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Trending";
            case 1:
                return "Category";
            case 2:
                return "Recent";
        }

        return "";
    }
}
