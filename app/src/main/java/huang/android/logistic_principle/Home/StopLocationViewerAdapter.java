package huang.android.logistic_principle.Home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class StopLocationViewerAdapter extends ArrayAdapter<JobOrderRouteData> {
    private Context context;
    private List<JobOrderRouteData> list;
    Runnable didDelete;
    private Activity activity;

    public StopLocationViewerAdapter(Context context, List<JobOrderRouteData> list, Activity activity) {
        super(context, R.layout.list_stop_location_viewer,list);
        this.context=context;
        this.list=list;
        this.activity=activity;
    }


    @Nullable
    @Override
    public JobOrderRouteData getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_stop_location_viewer,parent,false);
        }

        TextView wareHouseTitle = (TextView) view.findViewById(R.id.warehouse_name);
        Utility.utility.setFont(wareHouseTitle, Hind.MEDIUM,getContext());

        ImageView dropIV = (ImageView)view.findViewById(R.id.drop_icon);
        ImageView pickupIV = (ImageView)view.findViewById(R.id.pickup_icon);
//        TextView locationDetails = (TextView)view.findViewById(R.id.list_location_details);

        if (list.get(position) == null) return view;
        if (list.get(position).distributor_code == null)
            Utility.utility.setTextView(wareHouseTitle,list.get(position).city + " - " + list.get(position).warehouse_name);
        else
            Utility.utility.setTextView(wareHouseTitle,list.get(position).city + " - " + list.get(position).warehouse_name + " (" + list.get(position).distributor_code + ")");
//        if (list.get(position).phone == null)
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).phone);
//        if (list.get(position).phone == null)
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city);
//        else
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).pic + " - " + list.get(position).phone);

        dropIV.setVisibility(View.GONE);
        pickupIV.setVisibility(View.GONE);
        if (list.get(position).type.equals("Drop")) {
            dropIV.setVisibility(View.VISIBLE);
        } else if (list.get(position).type.equals("Pick Up")) {
            pickupIV.setVisibility(View.VISIBLE);
        }

        wareHouseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStopLocation(list.get(position));
            }
        });

        return view;
    }


    public void dialogStopLocation(JobOrderRouteData route) {
        new AlertDialog.Builder(activity);
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.dialog_stop_location_detail, null);


        TextView typeText = (TextView)promptsView.findViewById(R.id.type_text),
                locationText = (TextView)promptsView.findViewById(R.id.location_text),
                cpText = (TextView)promptsView.findViewById(R.id.cp_text),
                nameText = (TextView)promptsView.findViewById(R.id.name_text),
                phoneText = (TextView)promptsView.findViewById(R.id.phone_text),
                itemInfoText = (TextView)promptsView.findViewById(R.id.item_info_text),
                remarkText = (TextView)promptsView.findViewById(R.id.remark_text);

        Utility.utility.setFont(typeText,Hind.LIGHT,getContext());
        Utility.utility.setFont(locationText,Hind.LIGHT,getContext());
        Utility.utility.setFont(cpText,Hind.LIGHT,getContext());
        Utility.utility.setFont(nameText,Hind.LIGHT,getContext());
        Utility.utility.setFont(phoneText,Hind.LIGHT,getContext());
        Utility.utility.setFont(itemInfoText,Hind.LIGHT,getContext());
        Utility.utility.setFont(remarkText,Hind.LIGHT,getContext());


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

        Utility.utility.setFont(typeTV,Hind.MEDIUM,getContext());
        Utility.utility.setFont(stopLocationTV,Hind.MEDIUM,getContext());
        Utility.utility.setFont(nameTV,Hind.MEDIUM,getContext());
        Utility.utility.setFont(cpTV,Hind.MEDIUM,getContext());
        Utility.utility.setFont(itemTV,Hind.MEDIUM,getContext());
        Utility.utility.setFont(remarkTV,Hind.MEDIUM,getContext());

        Utility.utility.setTextView(typeTV,route.type);
        Utility.utility.setTextView(stopLocationTV,Utility.utility.longFormatLocation(new Location(route.distributor_code,route.location,route.city,route.address,route.warehouse_name,"","")));
        Utility.utility.setTextView(nameTV,route.nama);
        Utility.utility.setTextView(cpTV,route.phone);
        Utility.utility.setTextView(itemTV,route.item_info);
        Utility.utility.setTextView(remarkTV,route.remark);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
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



}
