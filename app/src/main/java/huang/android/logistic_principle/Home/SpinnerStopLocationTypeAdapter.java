package huang.android.logistic_principle.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

/**
 * Created by davidwibisono on 2/12/18.
 */

public class SpinnerStopLocationTypeAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;

    public SpinnerStopLocationTypeAdapter(Context context, List<String> list) {
        super(context, R.layout.list_stop_location,list);
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.spinner_stop_location_type,parent,false);
        }
        ImageView pickUpIcon = (ImageView)view.findViewById(R.id.pickup_icon);
        ImageView dropIcon = (ImageView)view.findViewById(R.id.drop_icon);
        if (list.get(position).equals("Pick Up")) {
            pickUpIcon.setVisibility(View.VISIBLE);
            dropIcon.setVisibility(View.GONE);
        } else if (list.get(position).equals("Drop")) {
            dropIcon.setVisibility(View.VISIBLE);
            pickUpIcon.setVisibility(View.GONE);
        }

        TextView type = (TextView)view.findViewById(R.id.type);
        type.setText(list.get(position));
        Utility.utility.setFont(type, Hind.MEDIUM,getContext());

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.spinner_stop_location_type,parent,false);
        }
        ImageView pickUpIcon = (ImageView)view.findViewById(R.id.pickup_icon);
        ImageView dropIcon = (ImageView)view.findViewById(R.id.drop_icon);
        if (list.get(position).equals("Pick Up")) {
            pickUpIcon.setVisibility(View.VISIBLE);
            dropIcon.setVisibility(View.GONE);
        } else if (list.get(position).equals("Drop")) {
            dropIcon.setVisibility(View.VISIBLE);
            pickUpIcon.setVisibility(View.GONE);
        }

        TextView type = (TextView)view.findViewById(R.id.type);
        type.setText(list.get(position));
        Utility.utility.setFont(type, Hind.MEDIUM,getContext());

        return view;
    }
}
