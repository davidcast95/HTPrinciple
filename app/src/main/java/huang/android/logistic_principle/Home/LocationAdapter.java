package huang.android.logistic_principle.Home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

import java.util.Collection;
import java.util.List;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class LocationAdapter extends ArrayAdapter<Location> {
    private Context context;
    private List<Location> list;
    private int layout;

    public LocationAdapter(Context context, int layout, List<Location> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
        this.layout = layout;
    }


    @Nullable
    @Override
    public Location getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(layout,parent,false);
        }

        TextView wareHouseTitle = (TextView)view.findViewById(R.id.list_warehouse_name);
        Utility.utility.setFont(wareHouseTitle, Hind.REGULAR,getContext());
        TextView locationDetails = (TextView)view.findViewById(R.id.list_location_details);
        Utility.utility.setFont(locationDetails, Hind.MEDIUM,getContext());

        if (list.get(position) == null) return view;
        if (list.get(position).code == null)
            Utility.utility.setTextView(wareHouseTitle,list.get(position).warehouse);
        else
            Utility.utility.setTextView(wareHouseTitle,list.get(position).warehouse + " (" + list.get(position).code + ")");
        if (list.get(position).pic == null)
            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).phone);
        if (list.get(position).phone == null)
            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city);
        else
            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).pic + " - " + list.get(position).phone);


        return view;
    }
}
