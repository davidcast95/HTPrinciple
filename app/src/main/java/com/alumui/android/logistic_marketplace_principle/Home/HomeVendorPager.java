package com.alumui.android.logistic_marketplace_principle.Home;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alumui.android.logistic_marketplace_principle.R;

/**
 * Created by davidwibisono on 8/22/17.
 */

public class HomeVendorPager extends Fragment {

    public HomeVendorPager() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.pager_vendor, container, false);

        return rootView;
    }
}
