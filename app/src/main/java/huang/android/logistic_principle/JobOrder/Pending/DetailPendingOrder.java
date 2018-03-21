package huang.android.logistic_principle.JobOrder.Pending;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Home.StopLocationAdapter;
import huang.android.logistic_principle.Home.StopLocationViewerAdapter;
import huang.android.logistic_principle.Model.JobOrder.GetJobOrderResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.PushNotificationAction.PushNotificationAction;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.RequestAService.ChooseVendor;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;

public class DetailPendingOrder extends AppCompatActivity {


    ProgressBar loading;
    LinearLayout layout;
    Button changeVendorButton;
    ListView stopLocationList;
    boolean drawerOpen = false;
    int listHeight = 0;

    JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_pending);


        setTitle(R.string.pdo);

        layout=(LinearLayout)findViewById(R.id.layout);
        changeVendorButton=(Button)findViewById(R.id.dop_change_vendor_button);
        Utility.utility.setFont(changeVendorButton, Hind.BOLD,getApplicationContext());
        loading = (ProgressBar) findViewById(R.id.loading);
        stopLocationList = (ListView)findViewById(R.id.stop_location_list);

        changeVendorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });


        Intent intent = getIntent();
        String job_order = intent.getStringExtra("joid");
        String notif = intent.getStringExtra("notif");
        if (notif != null) {
            if (notif.equals(PushNotificationAction.REJECTED)) {
                Toast.makeText(getApplicationContext(),getString(R.string.msg_vendor_rejected),Toast.LENGTH_LONG).show();
                setTitle(R.string.rejected_job_order);
            }
            if (notif.equals(PushNotificationAction.RTO_JO)) {
                String value = intent.getStringExtra("value");
                Toast.makeText(getApplicationContext(),getString(R.string.msg_vendor_rto) + " " + value + " " + getString(R.string.hour),Toast.LENGTH_LONG).show();
            }
        }
        if (job_order != null) {
            getPendingOrder(job_order);
        } else {
            int index = intent.getIntExtra("index", 0);
            if (PendingOrder.jobOrders.get(index) != null) {
                jobOrder = PendingOrder.jobOrders.get(index);
            }
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
                TextView cargoNote = (TextView)findViewById(R.id.cargo_notes);
                Utility.utility.setFont(cargoNote, Hind.MEDIUM,getApplicationContext());


                Utility.utility.setTextView(vendor, jobOrder.vendor);

                final ImageView profileImage = (ImageView)findViewById(R.id.profile_image);

                Utility.utility.setTextView(vendor, jobOrder.vendor);
                Log.e("image",jobOrder.vendor);
                String imageUrl = jobOrder.vendor_image.get(0);
                if (imageUrl != null) {
                    Log.e("image",imageUrl);
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
                } else {
                    Log.e("image","nothing");
                    profileImage.setImageDrawable(getResources().getDrawable(R.drawable.order_box));
                }

                Utility.utility.setTextView(ref, "Ref No : " + jobOrder.ref.replace("\n", ""));
                Utility.utility.setTextView(joid, jobOrder.joid);

                StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(), jobOrder.routes, this);
                stopLocationList.setAdapter(stopLocationAdapter);
                ViewTreeObserver observer = stopLocationList.getViewTreeObserver();

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

                if (jobOrder.status.equals(JobOrderStatus.VENDOR_REJECT)) {
                    setTitle(R.string.rejected_job_order);
                }
                Utility.utility.setTextView(vendor_name, jobOrder.vendor);
                Utility.utility.setTextView(vendor_cp_name, jobOrder.vendor_cp_name);
                Utility.utility.setTextView(vendor_cp_phone, jobOrder.vendor_cp_phone);
                Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, this);
                Utility.utility.setTextView(principle_name, jobOrder.principle);
                Utility.utility.setTextView(principle_cp_name, jobOrder.principle_cp_name);
                Utility.utility.setTextView(principle_cp_phone, jobOrder.principle_cp_phone);
                Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
                Utility.utility.setTextView(cargoInfo, jobOrder.cargoInfo);
                Utility.utility.setTextView(epd, Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                Utility.utility.setTextView(edd, Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
                Utility.utility.setTextView(volume, jobOrder.estimate_volume);
                Utility.utility.setTextView(cargoNote, jobOrder.notes);
//            Utility.utility.setTextView(truck,jobOrder.truck);
//            Utility.utility.setTextView(truck_type,jobOrder.truck_type);
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

    public void dialog() {

        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you want to change this vendor ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        changeVendorButtonTapped();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }

    void changeVendorButtonTapped() {
        Intent chooseVendorIntent = new Intent(this, ChooseVendor.class);
        startActivityForResult(chooseVendorIntent, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name"),
                        address = data.getStringExtra("address");
                TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
                vendor_name.setText(name);
                changeVendorAPI(name);

            }
        }
    }

    void changeVendorAPI(final String newVendor) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> change = new HashMap<>();
        change.put("vendor",newVendor);
        change.put("status",JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION);
        retrofit2.Call<JSONObject> callUpdateVendor = api.updateVendor(jobOrder.joid, change);
        callUpdateVendor.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(retrofit2.Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "change vendor JO on " + jobOrder.joid + " with " + newVendor)) {
                    Toast.makeText(getApplicationContext(), "Vendor change to " + newVendor + " succeed",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });

    }

    void getPendingOrder(String joid) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        final Activity activity = this;
        Call<GetJobOrderResponse> callJO = api.getJobOrderBy(joid);
        callJO.enqueue(new Callback<GetJobOrderResponse>() {
            @Override
            public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    GetJobOrderResponse jobOrderResponse = response.body();
                    if (jobOrderResponse.jobOrders != null) {
                        jobOrder = jobOrderResponse.jobOrders.get(0);
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
                        TextView cargoNote = (TextView)findViewById(R.id.cargo_notes);
                        Utility.utility.setFont(cargoNote, Hind.MEDIUM,getApplicationContext());


//                        TextView origin = (TextView) findViewById(R.id.origin);
//                        TextView destination = (TextView) findViewById(R.id.destination);

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


                        Utility.utility.setTextView(ref, "Ref No : " + jobOrder.ref.replace("\n", ""));
                        Utility.utility.setTextView(joid, jobOrder.joid);
//                        origin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialogStopLocation(jobOrder.routes.get(0));
//                            }
//                        });
//                        Utility.utility.setTextView(origin, Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(0).distributor_code, jobOrder.routes.get(0).location, jobOrder.routes.get(0).city, jobOrder.routes.get(0).address, jobOrder.routes.get(0).warehouse_name, "", "")));
//                        destination.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialogStopLocation(jobOrder.routes.get(1));
//                            }
//                        });
//                        Utility.utility.setTextView(destination, Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(1).distributor_code, jobOrder.routes.get(1).location, jobOrder.routes.get(1).city, jobOrder.routes.get(1).address, jobOrder.routes.get(1).warehouse_name, "", "")));

                        if (jobOrder.routes.size() > 2) {
                            StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(), jobOrder.routes.subList(2, jobOrder.routes.size()), activity);
                            stopLocationList.setAdapter(stopLocationAdapter);
                            ViewTreeObserver observer = stopLocationList.getViewTreeObserver();

                            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {
                                    listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
                                }
                            });

                            stopLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dialogStopLocation(jobOrder.routes.subList(2, jobOrder.routes.size()).get(i));
                                }
                            });
                        }
                        Utility.utility.setTextView(vendor_name, jobOrder.vendor);
                        Utility.utility.setTextView(vendor_cp_name, jobOrder.vendor_cp_name);
                        Utility.utility.setTextView(vendor_cp_phone, jobOrder.vendor_cp_phone);
                        Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, activity);
                        Utility.utility.setTextView(principle_name, jobOrder.principle);
                        Utility.utility.setTextView(principle_cp_name, jobOrder.principle_cp_name);
                        Utility.utility.setTextView(principle_cp_phone, jobOrder.principle_cp_phone);
                        Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, activity);
                        Utility.utility.setTextView(cargoInfo, jobOrder.cargoInfo);
                        Utility.utility.setTextView(epd, Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                        Utility.utility.setTextView(edd, Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
                        Utility.utility.setTextView(volume, jobOrder.estimate_volume);
                        Utility.utility.setTextView(cargoNote, jobOrder.notes);
                    }

                }

            }

            @Override
            public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }
}

