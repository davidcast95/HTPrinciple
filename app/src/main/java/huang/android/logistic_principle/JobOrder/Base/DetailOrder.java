package huang.android.logistic_principle.JobOrder.Base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import huang.android.logistic_principle.Chat.Chat;
import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Home.StopLocationViewerAdapter;
import huang.android.logistic_principle.JobOrder.TrackHistory;
import huang.android.logistic_principle.Maps.TrackOrderMaps;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class DetailOrder extends AppCompatActivity {
    ListView stopLocationList;
    ProgressBar loading;
    LinearLayout layout, last_status;
    TextView statusTimeTV,statusTV,notesTV,acceptDateTV, stopLocationCounter;

    public static JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates = new ArrayList<>();
    int listHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_detail_order);


        setTitle(getMenuTitle());

        layout=(LinearLayout)findViewById(R.id.layout);
        last_status=(LinearLayout)findViewById(R.id.last_check_in);
        statusTimeTV=(TextView)findViewById(R.id.last_check_in_time);
        Utility.utility.setFont(statusTimeTV, Hind.REGULAR,getApplicationContext());
        statusTV=(TextView)findViewById(R.id.last_check_in_status);
        Utility.utility.setFont(statusTV, Hind.MEDIUM,getApplicationContext());
        notesTV = (TextView)findViewById(R.id.last_check_in_notes);
        Utility.utility.setFont(notesTV, Hind.LIGHT,getApplicationContext());
        acceptDateTV = (TextView)findViewById(R.id.accept_date);
        Utility.utility.setFont(acceptDateTV, Hind.LIGHT,getApplicationContext());
        acceptDateTV.setVisibility(View.GONE);
        stopLocationList = (ListView)findViewById(R.id.stop_location_list);

        loading = (ProgressBar) findViewById(R.id.loading);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);

        jobOrder = getJobOrders(null).get(index);
        if (jobOrder != null) {
            TextView vendorInfoText = (TextView)findViewById(R.id.vendor_info_text);
            Utility.utility.setFont(vendorInfoText,Hind.MEDIUM,getApplicationContext());
            TextView vendorNameText = (TextView)findViewById(R.id.vendor_name_text);
            Utility.utility.setFont(vendorNameText,Hind.LIGHT,getApplicationContext());
            TextView vendorCPNameText = (TextView)findViewById(R.id.vendor_cp_name_text);
            Utility.utility.setFont(vendorCPNameText,Hind.LIGHT,getApplicationContext());
            TextView vendorCPText = (TextView)findViewById(R.id.vendor_cp_phone_text);
            Utility.utility.setFont(vendorCPText,Hind.LIGHT,getApplicationContext());
            TextView principleInfoText = (TextView)findViewById(R.id.principle_info_text);
            Utility.utility.setFont(principleInfoText,Hind.MEDIUM,getApplicationContext());
            TextView principleNameText = (TextView)findViewById(R.id.principle_name_text);
            Utility.utility.setFont(principleNameText,Hind.LIGHT,getApplicationContext());
            TextView principleCPNameText = (TextView)findViewById(R.id.principle_cp_name_text);
            Utility.utility.setFont(principleCPNameText,Hind.LIGHT,getApplicationContext());
            TextView principlePhoneText = (TextView)findViewById(R.id.principle_cp_phone_text);
            Utility.utility.setFont(principlePhoneText,Hind.LIGHT,getApplicationContext());

            TextView cargoInfoText = (TextView)findViewById(R.id.cargo_info_text);
            Utility.utility.setFont(cargoInfoText,Hind.MEDIUM,getApplicationContext());
            TextView pickDateText = (TextView)findViewById(R.id.pick_date_text);
            Utility.utility.setFont(pickDateText,Hind.LIGHT,getApplicationContext());
            TextView deliveredDateText = (TextView)findViewById(R.id.delivered_date_text);
            Utility.utility.setFont(deliveredDateText,Hind.LIGHT,getApplicationContext());
            TextView volText = (TextView)findViewById(R.id.vol_text);
            Utility.utility.setFont(volText,Hind.LIGHT,getApplicationContext());
            TextView hullText = (TextView)findViewById(R.id.truck_hull_no_text);
            Utility.utility.setFont(hullText,Hind.LIGHT,getApplicationContext());
            TextView trukText = (TextView)findViewById(R.id.truck_text);
            Utility.utility.setFont(trukText,Hind.LIGHT,getApplicationContext());
            TextView trukTypeText = (TextView)findViewById(R.id.truck_type_text);
            Utility.utility.setFont(trukTypeText,Hind.LIGHT,getApplicationContext());
            TextView driverNameText = (TextView)findViewById(R.id.driver_name_text);
            Utility.utility.setFont(driverNameText,Hind.LIGHT,getApplicationContext());
            TextView driverPhoneText = (TextView)findViewById(R.id.driver_phone_text);
            Utility.utility.setFont(driverPhoneText,Hind.LIGHT,getApplicationContext());
            TextView cargoDetailsText = (TextView)findViewById(R.id.cargo_details_text);
            Utility.utility.setFont(cargoDetailsText,Hind.LIGHT,getApplicationContext());
            TextView notesText = (TextView)findViewById(R.id.notes_text);
            Utility.utility.setFont(notesText,Hind.LIGHT,getApplicationContext());



            TextView vendor = (TextView)findViewById(R.id.vendor);
            Utility.utility.setFont(vendor, Hind.BOLD,getApplicationContext());
            TextView ref = (TextView)findViewById(R.id.ref_id);
            Utility.utility.setFont(ref, Hind.SEMIBOLD,getApplicationContext());
            TextView joid = (TextView) findViewById(R.id.joid);
            Utility.utility.setFont(joid, Hind.REGULAR,getApplicationContext());
            TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
            Utility.utility.setFont(vendor_name, Hind.MEDIUM,getApplicationContext());
            TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
            Utility.utility.setFont(vendor_cp_name, Hind.MEDIUM,getApplicationContext());
            TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
            Utility.utility.setFont(vendor_cp_phone, Hind.MEDIUM,getApplicationContext());
            TextView principle_name = (TextView) findViewById(R.id.principle_name);
            Utility.utility.setFont(principle_name, Hind.MEDIUM,getApplicationContext());
            TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
            Utility.utility.setFont(principle_cp_name, Hind.MEDIUM,getApplicationContext());
            TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
            Utility.utility.setFont(principle_cp_phone, Hind.MEDIUM,getApplicationContext());
            TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
            Utility.utility.setFont(cargoInfo, Hind.MEDIUM,getApplicationContext());
            TextView epd = (TextView) findViewById(R.id.pick_date);
            Utility.utility.setFont(epd, Hind.MEDIUM,getApplicationContext());
            TextView edd = (TextView) findViewById(R.id.delivered_date);
            Utility.utility.setFont(edd, Hind.MEDIUM,getApplicationContext());
            TextView volume = (TextView) findViewById(R.id.volume);
            Utility.utility.setFont(volume, Hind.MEDIUM,getApplicationContext());
            TextView truck = (TextView) findViewById(R.id.truck);
            Utility.utility.setFont(truck, Hind.MEDIUM,getApplicationContext());
            TextView truck_type = (TextView)findViewById(R.id.truck_type);
            Utility.utility.setFont(truck_type, Hind.MEDIUM,getApplicationContext());
            TextView truck_hull_no = (TextView)findViewById(R.id.truck_hull_no);
            Utility.utility.setFont(truck_hull_no, Hind.MEDIUM,getApplicationContext());
            TextView driver_nama = (TextView)findViewById(R.id.driver_name);
            Utility.utility.setFont(driver_nama, Hind.MEDIUM,getApplicationContext());
            TextView driver_phone = (TextView)findViewById(R.id.driver_phone);
            Utility.utility.setFont(driver_phone, Hind.MEDIUM,getApplicationContext());
            TextView cargoNote = (TextView)findViewById(R.id.cargo_notes);
            Utility.utility.setFont(cargoNote, Hind.MEDIUM,getApplicationContext());
            final ListView stopLocationList = (ListView)findViewById(R.id.stop_location_list);

            Utility.utility.setTextView(vendor,jobOrder.vendor);

            final ImageView profileImage = (ImageView)findViewById(R.id.profile_image);

            Utility.utility.setTextView(vendor, jobOrder.vendor);
            if (jobOrder.vendor_image.size() > 0) {
                String imageUrl = jobOrder.vendor_image.get(0);
                MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getApplicationContext());
                API api = Utility.utility.getAPIWithCookie(cookieJar);
                Call<ResponseBody> callImage = api.getImage(imageUrl);
                callImage.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                                profileImage.setImageBitmap(bm);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }

            ref.setText("Ref No : " + jobOrder.ref.replace("\n",""));
            joid.setText(jobOrder.joid);

            StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(),jobOrder.routes, this);
            stopLocationList.setAdapter(stopLocationAdapter);
            ViewTreeObserver observer = stopLocationList .getViewTreeObserver();

            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
                }
            });

            stopLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialogStopLocation(jobOrder.routes.get(i));
                }
            });

            Utility.utility.setTextView(vendor_name, jobOrder.vendor);
            Utility.utility.setTextView(vendor_cp_name,jobOrder.vendor_cp_name);
            Utility.utility.setTextView(vendor_cp_phone, jobOrder.vendor_cp_phone);
            Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, this);
            Utility.utility.setTextView(principle_name,jobOrder.principle);
            Utility.utility.setTextView(principle_cp_name,jobOrder.principle_cp_name);
            Utility.utility.setTextView(principle_cp_phone, jobOrder.principle_cp_phone);
            Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
            Utility.utility.setTextView(cargoInfo,jobOrder.cargoInfo);
            Utility.utility.setTextView(epd,Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
            Utility.utility.setTextView(edd,Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
            Utility.utility.setTextView(volume,jobOrder.estimate_volume);
            Utility.utility.setTextView(truck,jobOrder.truck);
            Utility.utility.setTextView(truck_type,jobOrder.truck_type);
            Utility.utility.setTextView(truck_hull_no,jobOrder.truck_lambung);
            Utility.utility.setTextView(driver_nama, jobOrder.driver_name);
            Utility.utility.setTextView(driver_phone,jobOrder.driver_phone);
            Utility.utility.setTextView(cargoNote, jobOrder.notes);
            Utility.utility.setDialContactPhone(driver_phone, jobOrder.driver_phone,this);

            if (jobOrder.acceptDate != "") {
                acceptDateTV.setVisibility(View.VISIBLE);
                acceptDateTV.setText(getString(R.string.confirm_vendor) + " " + Utility.formatDateFromstring(Utility.dateDBShortFormat,Utility.LONG_DATE_FORMAT,jobOrder.acceptDate));
            }
        }
    }

    public void dialogStopLocation(JobOrderRouteData route) {
        new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_stop_location_detail, null);

        TextView typeText = (TextView)promptsView.findViewById(R.id.type_text),
                locationText = (TextView)promptsView.findViewById(R.id.location_text),
                cpText = (TextView)promptsView.findViewById(R.id.cp_text),
                nameText = (TextView)promptsView.findViewById(R.id.name_text),
                phoneText = (TextView)promptsView.findViewById(R.id.phone_text),
                itemInfoText = (TextView)promptsView.findViewById(R.id.item_info_text),
                remarkText = (TextView)promptsView.findViewById(R.id.remark_text);

        Utility.utility.setFont(typeText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(locationText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(cpText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(nameText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(phoneText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(itemInfoText,Hind.LIGHT,getApplicationContext());
        Utility.utility.setFont(remarkText,Hind.LIGHT,getApplicationContext());


        TextView typeTV = (TextView)promptsView.findViewById(R.id.type),
                stopLocationTV = (TextView)promptsView.findViewById(R.id.stop_location),
                nameTV = (TextView)promptsView.findViewById(R.id.name),
                cpTV = (TextView)promptsView.findViewById(R.id.cp),
                itemTV = (TextView)promptsView.findViewById(R.id.item),
                remarkTV = (TextView)promptsView.findViewById(R.id.remark);

        ImageView pickUpIcon = (ImageView)promptsView.findViewById(R.id.pickup_icon),
                dropIcon = (ImageView)promptsView.findViewById(R.id.drop_icon);
        if (route.type.equals("Pick Up")) {
            pickUpIcon.setVisibility(View.VISIBLE);
            dropIcon.setVisibility(View.GONE);
        } else if (route.type.equals("Drop")) {
            pickUpIcon.setVisibility(View.GONE);
            dropIcon.setVisibility(View.VISIBLE);
        }

        Utility.utility.setFont(typeTV,Hind.MEDIUM,getApplicationContext());
        Utility.utility.setFont(stopLocationTV,Hind.MEDIUM,getApplicationContext());
        Utility.utility.setFont(nameTV,Hind.MEDIUM,getApplicationContext());
        Utility.utility.setFont(cpTV,Hind.MEDIUM,getApplicationContext());
        Utility.utility.setFont(itemTV,Hind.MEDIUM,getApplicationContext());
        Utility.utility.setFont(remarkTV,Hind.MEDIUM,getApplicationContext());

        Utility.utility.setTextView(typeTV,route.type);
        Utility.utility.setTextView(stopLocationTV,Utility.utility.longFormatLocation(new Location(route.distributor_code,route.location,route.city,route.address,route.warehouse_name,"","")));
        Utility.utility.setTextView(nameTV,route.nama);
        Utility.utility.setTextView(cpTV,route.phone);
        Utility.utility.setTextView(itemTV,route.item_info);
        Utility.utility.setTextView(remarkTV,route.remark);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuType(),menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_track_order:
                Intent intent = new Intent(DetailOrder.this, TrackHistory.class);
                intent.putExtra("from",getFrom());
                intent.putExtra("vendor", jobOrder.vendor);
                intent.putExtra("ref",jobOrder.ref);
                intent.putExtra("origin", Utility.utility.longFormatLocation(new Location(jobOrder.routes.get(0).distributor_code,jobOrder.routes.get(0).location,jobOrder.routes.get(0).city,jobOrder.routes.get(0).address,jobOrder.routes.get(0).warehouse_name,"","")));
                intent.putExtra("origin_type",jobOrder.routes.get(0).type);
                int lastIndex = jobOrder.routes.size()-1;
                intent.putExtra("destination", Utility.utility.longFormatLocation(new Location(jobOrder.routes.get(lastIndex).distributor_code,jobOrder.routes.get(lastIndex).location,jobOrder.routes.get(lastIndex).city,jobOrder.routes.get(lastIndex).address,jobOrder.routes.get(lastIndex).warehouse_name,"","")));
                intent.putExtra("destination_type",jobOrder.routes.get(lastIndex).type);
                intent.putExtra("driver",jobOrder.driver);
                startActivity(intent);
                break;
            case R.id.action_message:
                Intent intent1 = new Intent(this, Chat.class);
                intent1.putExtra("joid",jobOrder.joid);
                startActivity(intent1);
                break;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLastUpdate();
    }


    protected int getMenuType() {
        return R.menu.trackorderlist_titlebar;
    }

    protected String getFrom() {
        return "default";
    }

    //API
    void getLastUpdate() {
        last_status.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderUpdateResponse> callGetJOUpdate = api.getJOUpdate(jobOrder.joid);
        callGetJOUpdate.enqueue(new Callback<JobOrderUpdateResponse>() {
            @Override
            public void onResponse(Call<JobOrderUpdateResponse> call, Response<JobOrderUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    JobOrderUpdateResponse jobOrderUpdateResponse = response.body();
                    if (jobOrderUpdateResponse != null) {
                        if (jobOrderUpdateResponse.jobOrderUpdates != null) {
                            jobOrderUpdates = jobOrderUpdateResponse.jobOrderUpdates;
                            if (jobOrderUpdates.size() > 0) {
                                final JobOrderUpdateData lastJOStatus = jobOrderUpdates.get(0);
                                last_status.setVisibility(View.VISIBLE);
                                statusTimeTV.setText(Utility.formatDateFromstring(Utility.dateDBLongFormat, Utility.LONG_DATE_TIME_FORMAT, lastJOStatus.time));
                                statusTV.setText(lastJOStatus.status);
                                notesTV.setText(lastJOStatus.note);
                                ImageView button = (ImageView) findViewById(R.id.detail_active_last_map);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Double latitude = Double.valueOf(lastJOStatus.latitude), longitude = Double.valueOf(lastJOStatus.longitude);
                                        if (latitude != null || longitude != null) {
                                            Intent maps = new Intent(DetailOrder.this, TrackOrderMaps.class);
                                            maps.putExtra("longitude", longitude);
                                            maps.putExtra("latitude", latitude);
                                            Log.e("LAT", latitude + "");
                                            Log.e("LONG", longitude + "");
                                            maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(maps);
                                        } else {
                                            Context c = getApplicationContext();
                                            Toast.makeText(c, R.string.nla, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                last_status.setVisibility(View.GONE);
                            }
                        }
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

    public String getMenuTitle() {
        return "Order";
    }



}
