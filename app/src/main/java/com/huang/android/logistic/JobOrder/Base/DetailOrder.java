package com.huang.android.logistic.JobOrder.Base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic.Maps.TrackOrderMaps;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.ServiceAPI.API;
import com.huang.android.logistic.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class DetailOrder extends AppCompatActivity {
    ProgressBar loading;
    LinearLayout layout, last_status;
    TextView statusTimeTV,statusTV,notesTV,acceptDateTV;

    public JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_detail_order);


        setTitle("Order Done");

        layout=(LinearLayout)findViewById(R.id.layout);
        last_status=(LinearLayout)findViewById(R.id.last_check_in);
        statusTimeTV=(TextView)findViewById(R.id.last_check_in_time);
        statusTV=(TextView)findViewById(R.id.last_check_in_status);
        notesTV = (TextView)findViewById(R.id.last_check_in_notes);
        acceptDateTV = (TextView)findViewById(R.id.accept_date);
        acceptDateTV.setVisibility(View.GONE);

        loading = (ProgressBar) findViewById(R.id.loading);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);

        jobOrder = getJobOrders(null).get(index);
        if (jobOrder != null) {
            TextView ref = (TextView)findViewById(R.id.ref_id);
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
            TextView volume = (TextView) findViewById(R.id.volume);
            TextView truck = (TextView) findViewById(R.id.truck);
            TextView truck_type = (TextView)findViewById(R.id.truck_type);

            ref.setText("Ref No : " + jobOrder.ref);
            joid.setText(jobOrder.joid);
            origin.setText(Utility.utility.formatLocation(new Location(jobOrder.origin_code,jobOrder.origin,jobOrder.origin_city,jobOrder.origin_address,jobOrder.origin_warehouse,"")));
            destination.setText(Utility.utility.formatLocation(new Location(jobOrder.destination_code,jobOrder.destination,jobOrder.destination_city,jobOrder.destination_address,jobOrder.destination_warehouse,"")));
            vendor_name.setText(jobOrder.vendor);
            vendor_cp_name.setText(jobOrder.vendor_cp_name);
            vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
            Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, this);
            principle_cp_name.setText(jobOrder.principle_cp_name);
            principle_cp_phone.setText(jobOrder.principle_cp_phone);
            Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
            cargoInfo.setText(jobOrder.cargoInfo);
            epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
            edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
            volume.setText(jobOrder.estimate_volume);
            truck.setText(jobOrder.truck);
            truck_type.setText(jobOrder.truck_type);
            if (jobOrder.acceptDate != "") {
                acceptDateTV.setVisibility(View.VISIBLE);
                acceptDateTV.setText("has confirmed by vendor at " + jobOrder.acceptDate);
            }
        }
        getLastUpdate();
    }


    //API
    void getLastUpdate() {
        last_status.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderUpdateResponse> callGetJOUpdate = api.getJOUpdate("[[\"Job Order Update\",\"job_order\",\"=\",\""+jobOrder.joid+"\"]]");
        callGetJOUpdate.enqueue(new Callback<JobOrderUpdateResponse>() {
            @Override
            public void onResponse(Call<JobOrderUpdateResponse> call, Response<JobOrderUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    JobOrderUpdateResponse jobOrderUpdateResponse = response.body();
                    jobOrderUpdates = jobOrderUpdateResponse.jobOrderUpdates;
                    if (jobOrderUpdates.size() > 0) {
                        final JobOrderUpdateData lastJOStatus = jobOrderUpdates.get(0);
                        last_status.setVisibility(View.VISIBLE);
                        statusTimeTV.setText(lastJOStatus.time);
                        statusTV.setText(lastJOStatus.status);
                        notesTV.setText(lastJOStatus.note);
                        ImageView button = (ImageView)findViewById(R.id.detail_active_last_map);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Double latitude = Double.valueOf(lastJOStatus.latitude), longitude = Double.valueOf(lastJOStatus.latitude);
                                if (latitude!= null || longitude!=null){
                                    Intent maps = new Intent(getApplicationContext(), TrackOrderMaps.class);
                                    maps.putExtra("longitude", longitude );
                                    maps.putExtra("latitude", latitude );
                                    Log.e("LAT",latitude+"");
                                    Log.e("LONG",longitude+"");
                                    maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(maps);
                                }
                                else {
                                    Context c = getApplicationContext();
                                    Toast.makeText(c,R.string.nla, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        last_status.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }

    public List<JobOrderData> getJobOrders(List<JobOrderData> jobOrderDatas) {
        return jobOrderDatas;
    }
}
