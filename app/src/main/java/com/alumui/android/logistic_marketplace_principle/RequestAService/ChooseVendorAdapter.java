package com.alumui.android.logistic_marketplace_principle.RequestAService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alumui.android.logistic_marketplace_principle.Model.Vendor.VendorData;
import com.alumui.android.logistic_marketplace_principle.R;

import java.util.List;

/**
 * Created by davidwibisono on 8/22/17.
 */

public class ChooseVendorAdapter extends BaseAdapter {

    private Context mContext;
    public List<VendorData> vendors;
    private ImageView profilPicture;
    private TextView nameTextView, addressTextView;


    public ChooseVendorAdapter(Context c,List<VendorData> vendors) {
        mContext = c;
        this.vendors = vendors;
    }



    @Override
    public int getCount() {
        return this.vendors.size();
    }

    @Override
    public VendorData getItem(int i) {
        return vendors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View listVendor = null;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listVendor = new View(mContext);
        listVendor = inflater.inflate(R.layout.list_vendor, viewGroup, false);
        profilPicture = (ImageView) listVendor.findViewById(R.id.list_vendor_profil_picture);
        nameTextView = (TextView) listVendor.findViewById(R.id.list_vendor_name);
        addressTextView = (TextView) listVendor.findViewById(R.id.list_vendor_address);

        nameTextView.setText(vendors.get(i).name);
        addressTextView.setText(vendors.get(i).address);

        return listVendor;
    }
}
