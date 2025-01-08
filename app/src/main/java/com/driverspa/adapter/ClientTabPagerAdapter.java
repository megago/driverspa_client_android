package com.driverspa.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentStatePagerAdapter;

import com.driverspa.client.fragment.ClientBooksFragment;
import com.driverspa.client.fragment.ClientFavouriteWashersFragment;
import com.driverspa.client.fragment.ClientMapInTabFragment;
import com.driverspa.client.fragment.ClientNearByWashersFragment;
import com.driverspa.client.fragment.ClientNotificationFragment;

/**
 * Created by Yerzhan Tanatov on 19/10/15.
 */

public class ClientTabPagerAdapter extends FragmentStatePagerAdapter {

    public ClientTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Fragment promo washers
                return new ClientMapInTabFragment();
            case 1:
                return  new ClientNearByWashersFragment();
            case 2:
                //Fragment books
                return new ClientBooksFragment();
            case 3:
                //Fragment favourite washers
                return new ClientNotificationFragment();
            case 4:
                //Fragment favourite washers
                return new ClientFavouriteWashersFragment();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 5; //No of Tabs
    }
}
