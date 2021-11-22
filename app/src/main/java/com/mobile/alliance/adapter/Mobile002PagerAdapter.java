package com.mobile.alliance.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mobile.alliance.fragment.ChangeTruckFragment;
import com.mobile.alliance.fragment.DeliveryListFragment;

public class Mobile002PagerAdapter extends FragmentStatePagerAdapter{
    private int mPageCount;

    public Mobile002PagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.mPageCount = pageCount;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChangeTruckFragment changeTruckFragment = new ChangeTruckFragment();
                return changeTruckFragment;

            case 1:
                DeliveryListFragment deliveryListFragment = new DeliveryListFragment();
                return deliveryListFragment;

            default:
                return null;
        }

    }



    @Override

    public int getCount() {

        return mPageCount;

    }

}

