package huang.android.logistic_principle.Base;

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
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

/**
 * Created by davidwibisono on 2/12/18.
 */

public class SpinnerStringAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;

    public SpinnerStringAdapter(Context context, List<String> list) {
        super(context, R.layout.list_stop_location,list);
        this.context=context;
        this.list = list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.spinner_item,parent,false);
        }

        TextView type = (TextView)view.findViewById(R.id.text);
        type.setText(list.get(position));
        Utility.utility.setFont(type, Hind.MEDIUM,getContext());

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.spinner_item,parent,false);
        }

        TextView type = (TextView)view.findViewById(R.id.text);
        type.setText(list.get(position));
        Utility.utility.setFont(type, Hind.MEDIUM,getContext());

        return view;
    }
}