package com.alumui.android.logistic_marketplace_principle.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.RequestAService.RequestAService;
import com.alumui.android.logistic_marketplace_principle.Utility;


public class Home extends Fragment {

    TextView originWarning, destinationWarning;
    EditText originEditText, destinationEditText;
    Button requestAServiceButton;
    ViewPager vendorPagerView;
    PagerAdapter vendorPagerAdapter;

    public Home() {
        // Required empty public constructor
    }

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        originEditText = (EditText)v.findViewById(R.id.home_origin);
        destinationEditText = (EditText)v.findViewById(R.id.home_destination);
        originWarning = (TextView)v.findViewById(R.id.home_origin_warning);
        destinationWarning = (TextView)v.findViewById(R.id.home_destination_warning);
        requestAServiceButton = (Button)v.findViewById(R.id.request_a_service_button);

//        // Instantiate a Vendor View Pager
//        vendorPagerView = (ViewPager)v.findViewById(R.id.home_vendor_view_pager);
//        vendorPagerAdapter = new HomeVendorPagerAdapter(this.getActivity().getSupportFragmentManager());
//        vendorPagerView.setAdapter(vendorPagerAdapter);


        requestAServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAServiceButtonClicked();
            }
        });
        return v;
    }

    //Actions
    void requestAServiceButtonClicked() {
        Boolean validate = true;
        String originField = originEditText.getText().toString(),
                destinationField = destinationEditText.getText().toString();
        if (originField.length() == 0) {
            originWarning.setText("this field must be contain a place information");
            validate = false;
        }
        if (destinationField.length() == 0) {
            validate = false;
            destinationWarning.setText("this field must be contain a place information");
        }
        if (validate) {
            Intent requestAServiceIntent = new Intent(this.getActivity(), RequestAService.class);
            requestAServiceIntent.putExtra("origin", originEditText.getText().toString());
            requestAServiceIntent.putExtra("destination", destinationEditText.getText().toString());
            startActivity(requestAServiceIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Route information is empty", Toast.LENGTH_SHORT).show();
        }
    }




}
