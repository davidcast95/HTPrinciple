package com.huang.android.logistic_principle.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by davidwibisono on 8/22/17.
 */

public class HomeVendorPagerAdapter extends FragmentStatePagerAdapter {
    public HomeVendorPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new HomeVendorPager();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
