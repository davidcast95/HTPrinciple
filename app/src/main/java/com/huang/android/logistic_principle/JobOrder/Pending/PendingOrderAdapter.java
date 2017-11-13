package com.huang.android.logistic_principle.JobOrder.Pending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic_principle.JobOrder.Base.JobOrderAdapter;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import com.huang.android.logistic_principle.R;

import java.util.List;


public class PendingOrderAdapter extends JobOrderAdapter {
    public PendingOrderAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout, list);
    }
}
