package huang.android.logistic_principle.JobOrder.Pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

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


        setTitle("Pending Order Detail");

        layout=(LinearLayout)findViewById(R.id.layout);

        loading = (ProgressBar) findViewById(R.id.loading);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        if (PendingOrder.jobOrders.get(index) != null) {
            jobOrder = PendingOrder.jobOrders.get(index);
        }
        if (jobOrder != null) {
            TextView vendor = (TextView)findViewById(R.id.vendor);
            TextView ref = (TextView)findViewById(R.id.ref_id);
            TextView joid = (TextView) findViewById(R.id.joid);
            TextView origin = (TextView) findViewById(R.id.origin);
            TextView destination = (TextView) findViewById(R.id.destination);
            TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
            TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
            TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
            TextView principle_name = (TextView) findViewById(R.id.principle_name);
            TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
            TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
            TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
            TextView epd = (TextView) findViewById(R.id.pick_date);
            TextView edd = (TextView) findViewById(R.id.delivered_date);
            TextView volume = (TextView) findViewById(R.id.volume);
            TextView truck = (TextView) findViewById(R.id.truck);
            TextView truck_type = (TextView)findViewById(R.id.truck_type);


            vendor.setText(jobOrder.vendor);
            ref.setText("Ref No : " + jobOrder.ref.replace("\n",""));
            joid.setText(jobOrder.joid);
            origin.setText(Html.fromHtml(Utility.utility.formatLocation(new Location(jobOrder.origin_code,jobOrder.origin,jobOrder.origin_city,jobOrder.origin_address,jobOrder.origin_warehouse,"",""))));
            destination.setText(Html.fromHtml(Utility.utility.formatLocation(new Location(jobOrder.destination_code,jobOrder.destination,jobOrder.destination_city,jobOrder.destination_address,jobOrder.destination_warehouse,"",""))));

            vendor_name.setText(jobOrder.vendor);
            vendor_cp_name.setText(jobOrder.vendor_cp_name);
            vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
            Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, this);
            principle_name.setText(jobOrder.principle);
            principle_cp_name.setText(jobOrder.principle_cp_name);
            principle_cp_phone.setText(jobOrder.principle_cp_phone);
            Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
            cargoInfo.setText(jobOrder.cargoInfo);
            epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
            edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
            volume.setText(jobOrder.estimate_volume);
            truck.setText(jobOrder.truck);
            truck_type.setText(jobOrder.truck_type);
        }
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

