package com.alumui.android.logistic_marketplace_principle.JobOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alumui.android.logistic_marketplace_principle.JobOrder.OnProgress.DetailOnProgressOrder;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.Utility;

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


        TextView joid = (TextView)findViewById(R.id.joid);
        joid.setText(DetailOnProgressOrder.jobOrder.joid);
        TextView tanggal = (TextView)findViewById(R.id.tanggal1);
        tanggal.setText(DetailOnProgressOrder.jobOrder.etd);
        TextView locationfrom = (TextView)findViewById(R.id.locationfrom);
        locationfrom.setText(DetailOnProgressOrder.jobOrder.origin);
        TextView locationto = (TextView)findViewById(R.id.locationto);
        locationto.setText(DetailOnProgressOrder.jobOrder.destination);
        acceptDateTV = (TextView)findViewById(R.id.accept_date);
        acceptDateTV.setVisibility(View.GONE);

        if (DetailOnProgressOrder.jobOrder.acceptDate != "") {
            acceptDateTV.setVisibility(View.VISIBLE);
            acceptDateTV.setText("has confirmed by vendor at " + DetailOnProgressOrder.jobOrder.acceptDate);
        }


        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);
        loading.setVisibility(View.GONE);

        trackHistoryAdapter = new TrackHistoryAdapter(getApplicationContext(),R.layout.activity_track_history_list, DetailOnProgressOrder.jobOrderUpdates);
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
