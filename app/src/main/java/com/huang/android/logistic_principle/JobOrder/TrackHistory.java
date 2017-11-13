package com.huang.android.logistic_principle.JobOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huang.android.logistic_principle.JobOrder.Base.DetailOrder;
import com.huang.android.logistic_principle.JobOrder.OnProgress.DetailOnProgressOrder;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.Utility;

public class TrackHistory extends AppCompatActivity {

    ListView lv;
    TrackHistoryAdapter trackHistoryAdapter;
    ProgressBar loading;
    LinearLayout layout;
    TextView acceptDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setTitle(R.string.tracking_history);
        setContentView(R.layout.activity_track_history);

        String from,to;
        Intent intent = getIntent();
        from = intent.getStringExtra("origin");
        to = intent.getStringExtra("destination");

        TextView joid = (TextView)findViewById(R.id.joid);
        joid.setText(DetailOrder.jobOrder.joid);
        TextView tanggal = (TextView)findViewById(R.id.tanggal1);
        tanggal.setText(DetailOrder.jobOrder.etd);
        TextView locationfrom = (TextView)findViewById(R.id.locationfrom);
        locationfrom.setText(from);
        TextView locationto = (TextView)findViewById(R.id.locationto);
        locationto.setText(to);
        acceptDateTV = (TextView)findViewById(R.id.accept_date);
        acceptDateTV.setVisibility(View.GONE);

        if (DetailOnProgressOrder.jobOrder.acceptDate != "") {
            acceptDateTV.setVisibility(View.VISIBLE);
            acceptDateTV.setText("has confirmed by vendor at " + DetailOrder.jobOrder.acceptDate);
        }


        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);
        loading.setVisibility(View.GONE);

        trackHistoryAdapter = new TrackHistoryAdapter(getApplicationContext(),R.layout.activity_track_history_list, DetailOrder.jobOrderUpdates);
        lv.setAdapter(trackHistoryAdapter);
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
