package huang.android.logistic_principle.Home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.security.auth.callback.Callback;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class StopLocationAdapter extends ArrayAdapter<JobOrderRouteData> {
    private Context context;
    private List<JobOrderRouteData> list;
    Runnable didDelete;

    public StopLocationAdapter(Context context, List<JobOrderRouteData> list) {
        super(context, R.layout.list_stop_location,list);
        this.context=context;
        this.list=list;
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
            view=inflater.inflate(R.layout.list_stop_location,parent,false);
        }

        EditText wareHouseTitle = (EditText)view.findViewById(R.id.warehouse_name);
        Utility.utility.setFont(wareHouseTitle, Hind.MEDIUM,getContext());
        Button deleteButton = (Button)view.findViewById(R.id.delete);
        ImageView dropIV = (ImageView)view.findViewById(R.id.drop_icon);
        ImageView pickupIV = (ImageView)view.findViewById(R.id.pickup_icon);
//        TextView locationDetails = (TextView)view.findViewById(R.id.list_location_details);

        if (list.get(position) == null) return view;
        if (list.get(position).distributor_code == null)
            Utility.utility.setEditText(wareHouseTitle,list.get(position).city + " - " +  list.get(position).warehouse_name);
        else
            Utility.utility.setEditText(wareHouseTitle,list.get(position).city + " - " + list.get(position).warehouse_name + " (" + list.get(position).distributor_code + ")");
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
                Intent intent = new Intent(getContext(), EditStopLocation.class);
                intent.putExtra("index",position);
                getContext().startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(position);
            }
        });

        return view;
    }


    public void dialog(final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_stop_location)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        list.remove(position);
                        notifyDataSetChanged();
                        didDelete.run();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void setOnItemDelete(Runnable didDelete) {
        this.didDelete = didDelete;
    }

}
