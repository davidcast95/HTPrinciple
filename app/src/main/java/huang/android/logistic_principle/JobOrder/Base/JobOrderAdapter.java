package huang.android.logistic_principle.JobOrder.Base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UTFDataFormatException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class JobOrderAdapter extends ArrayAdapter<JobOrderData> {
    private Context context;
    private List<JobOrderData> list;

    public JobOrderAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.fragment_job_order_list,parent,false);
        }


        final ImageView profileImage = (ImageView)view.findViewById(R.id.profile_image);
        TextView vendor = (TextView)view.findViewById(R.id.vendor);
        Utility.utility.setFont(vendor, Hind.BOLD,getContext());
        TextView stopLocationCounter = (TextView)view.findViewById(R.id.stop_location_count);
        Utility.utility.setFont(stopLocationCounter, Hind.LIGHT,getContext());
        TextView joid = (TextView)view.findViewById(R.id.joid);
        Utility.utility.setFont(joid, Hind.REGULAR,getContext());
        TextView origin = (TextView) view.findViewById(R.id.origin);
        Utility.utility.setFont(origin, Hind.MEDIUM,getContext());
        TextView destination = (TextView)view.findViewById(R.id.destination);
        Utility.utility.setFont(destination, Hind.MEDIUM,getContext());
        TextView ref = (TextView)view.findViewById(R.id.ref_id);
        Utility.utility.setFont(ref, Hind.SEMIBOLD,getContext());
        LinearLayout truckdetailll = (LinearLayout)view.findViewById(R.id.truck_detail_view);
        TextView truckdetail = (TextView)view.findViewById(R.id.truck_detail);
        Utility.utility.setFont(truckdetail, Hind.REGULAR,getContext());
        TextView rejectedJO = (TextView)view.findViewById(R.id.rejected_jo);
        Utility.utility.setFont(truckdetail, Hind.LIGHT,getContext());


        JobOrderData jobOrder = list.get(position);
        if (jobOrder.vendor_image.size() > 0) {
            String imageUrl = jobOrder.vendor_image.get(0);
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(context);
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

        if (jobOrder.status.equals(JobOrderStatus.VENDOR_REJECT)) {
            rejectedJO.setVisibility(View.VISIBLE);
        } else {
            rejectedJO.setVisibility(View.GONE);
        }
        Utility.utility.setTextView(vendor,jobOrder.vendor);
        Utility.utility.setTextView(ref,"Ref No : " + jobOrder.ref.replace("\n",""));

        int lastIndex = jobOrder.routes.size()-1;
        Utility.utility.setTextView(origin,Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(0).distributor_code,jobOrder.routes.get(0).location,jobOrder.routes.get(0).city,jobOrder.routes.get(0).address,jobOrder.routes.get(0).warehouse_name,"","")));
        Utility.utility.setTextView(destination,Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(lastIndex).distributor_code,jobOrder.routes.get(lastIndex).location,jobOrder.routes.get(lastIndex).city,jobOrder.routes.get(lastIndex).address,jobOrder.routes.get(lastIndex).warehouse_name,"","")));

        ImageView pickUpOrigin = (ImageView)view.findViewById(R.id.pickup_origin_icon);
        ImageView dropOrigin = (ImageView)view.findViewById(R.id.drop_origin_icon);
        if (jobOrder.routes.get(0).type.equals("Pick Up")) {
            pickUpOrigin.setVisibility(View.VISIBLE);
        } else {
            dropOrigin.setVisibility(View.VISIBLE);
        }

        ImageView pickUpDestination = (ImageView)view.findViewById(R.id.pickup_destination_icon);
        ImageView dropDestination = (ImageView)view.findViewById(R.id.drop_destination_icon);
        if (jobOrder.routes.get(lastIndex).type.equals("Pick Up")) {
            pickUpDestination.setVisibility(View.VISIBLE);
        } else {
            dropDestination.setVisibility(View.VISIBLE);
        }


        if (jobOrder.routes.size() > 2) {
            stopLocationCounter.setVisibility(View.VISIBLE);
            Utility.utility.setTextView(stopLocationCounter, (jobOrder.routes.size() - 2) + " " + getContext().getString(R.string.stop_location));
        }
        else {
            stopLocationCounter.setVisibility(View.GONE);
            Utility.utility.setTextView(stopLocationCounter, "");
        }

        Utility.utility.setTextView(joid,jobOrder.joid);
        if (list.get(position).truck == null || list.get(position).truck_type == null) {
            truckdetailll.setVisibility(View.GONE);
        } else {
            Utility.utility.setTextView(truckdetail, list.get(position).truck + " (" + list.get(position).truck_type + ")");
        }
        return view;
    }

//    @Override
//    public void add(@Nullable JobOrderData object) {
//        if (list.size() == 0) {
//            super.add(object);
//        } else {
//            boolean isPlaced = false;
//            for (int i = 0; i < list.size() && !isPlaced; i++) {
//                int offset = Utility.utility.stringToDate(object.modified).compareTo(Utility.utility.stringToDate(list.get(i).modified));
//                if (offset > 0) {
//                    isPlaced = true;
//                    list.add(i,object);
//                }
//            }
//            if (!isPlaced) {
//                list.add(object);
//            }
//        }
//    }

}
