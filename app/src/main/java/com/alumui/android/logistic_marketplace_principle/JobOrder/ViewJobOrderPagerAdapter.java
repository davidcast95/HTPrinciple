package com.alumui.android.logistic_marketplace_principle.JobOrder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alumui.android.logistic_marketplace_principle.R;

import java.util.List;

/**
 * Created by davidwibisono on 8/23/17.
 */

public class ViewJobOrderPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> listFragments;

    public ViewJobOrderPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.listFragments=listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }
}
