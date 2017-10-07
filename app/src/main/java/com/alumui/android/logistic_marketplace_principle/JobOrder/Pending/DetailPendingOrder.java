package com.alumui.android.logistic_marketplace_principle.JobOrder.Pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alumui.android.logistic_marketplace_principle.JobOrder.OnProgress.OnProgressOrder;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.Utility;

import java.util.List;

public class DetailPendingOrder extends AppCompatActivity {


    ProgressBar loading;
    LinearLayout layout;

    JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_pending);


        setTitle("Order Pending");

        layout=(LinearLayout)findViewById(R.id.layout);

        loading = (ProgressBar) findViewById(R.id.loading);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        if (PendingOrder.jobOrders.get(index) != null) {
            jobOrder = PendingOrder.jobOrders.get(index);
        }
        if (jobOrder != null) {
            TextView joid = (TextView) findViewById(R.id.joid);
            TextView origin = (TextView) findViewById(R.id.origin);
            TextView destination = (TextView) findViewById(R.id.destination);
            TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
            TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
            TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
            TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
            TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
            TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
            TextView epd = (TextView) findViewById(R.id.pick_date);
            TextView edd = (TextView) findViewById(R.id.delivered_date);


            joid.setText(jobOrder.joid);
            origin.setText(jobOrder.origin);
            destination.setText(jobOrder.destination);
            vendor_name.setText(jobOrder.vendor);
            vendor_cp_name.setText(jobOrder.vendor_cp_name);
            vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
            principle_cp_name.setText(jobOrder.principle_cp_name);
            principle_cp_phone.setText(jobOrder.principle_cp_phone);
            cargoInfo.setText(jobOrder.cargoInfo);
            epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
            edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
        }
    }


}

