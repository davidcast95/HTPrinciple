package com.alumui.android.logistic_marketplace_principle.JobOrder.OnProgress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alumui.android.logistic_marketplace_principle.JobOrder.TrackHistory;
import com.alumui.android.logistic_marketplace_principle.Maps.TrackOrderMaps;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.alumui.android.logistic_marketplace_principle.Model.MyCookieJar;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.alumui.android.logistic_marketplace_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOnProgressOrder extends AppCompatActivity {

    ProgressBar loading;
    LinearLayout layout, last_status;
    TextView statusTimeTV,statusTV,notesTV, acceptDateTV;

    public static JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_on_progress_order);


        setTitle("Order On Progress");

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
        if (OnProgressOrder.jobOrders.get(index) != null) {
            jobOrder = OnProgressOrder.jobOrders.get(index);
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
            if (jobOrder.acceptDate != "") {
                acceptDateTV.setVisibility(View.VISIBLE);
                acceptDateTV.setText("has confirmed by vendor at " + jobOrder.acceptDate);
            }
        }
        getLastUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trackorderlist_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_track_order) {
            Intent intent = new Intent(this, TrackHistory.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
