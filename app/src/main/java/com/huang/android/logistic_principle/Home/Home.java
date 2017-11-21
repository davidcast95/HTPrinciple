package com.huang.android.logistic_principle.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic_principle.MainActivity;
import com.huang.android.logistic_principle.Model.Location.Location;
import com.huang.android.logistic_principle.Model.Location.LocationCreation;
import com.huang.android.logistic_principle.Model.Location.LocationResponse;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.RequestAService.RequestAService;
import com.huang.android.logistic_principle.ServiceAPI.API;
import com.huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Home extends Fragment {

    TextView originWarning, destinationWarning;
    EditText originEditText, destinationEditText;
    Button requestAServiceButton;

    public static Location origin, destination;
    int originIndex = 0, destinationIndex = 0;



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
        originEditText = (EditText) v.findViewById(R.id.home_origin);
        destinationEditText = (EditText) v.findViewById(R.id.home_destination);
        originWarning = (TextView)v.findViewById(R.id.home_origin_warning);
        destinationWarning = (TextView)v.findViewById(R.id.home_destination_warning);
        requestAServiceButton = (Button)v.findViewById(R.id.request_a_service_button);

        originEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchLocation.class);
                intent.putExtra("mode","origin");
                startActivityForResult(intent,100);
            }
        });

        destinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchLocation.class);
                intent.putExtra("mode","destination");
                startActivityForResult(intent,200);
            }
        });

        requestAServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAServiceButtonClicked();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(originEditText,Utility.utility.formatLocation(origin));
        }
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(destinationEditText, Utility.utility.formatLocation(destination));
        }
    }

    //Actions
    void requestAServiceButtonClicked() {
        Boolean validate = true;
        String originField = originEditText.getText().toString(),
                destinationField = destinationEditText.getText().toString();
        if (originField.length() == 0) {
            Utility.utility.setTextView(originWarning,"this field must be contain a place information");
            validate = false;
        }
        if (destinationField.length() == 0) {
            validate = false;
            Utility.utility.setTextView(destinationWarning,"this field must be contain a place information");
        }

        if (originField.equals(destinationField)) {
            validate = false;
            Toast.makeText(getActivity().getApplicationContext(),"Origin and destination cannot be the same",Toast.LENGTH_SHORT).show();
        }



        if (validate) {
            Intent requestAServiceIntent = new Intent(this.getActivity(), RequestAService.class);
            requestAServiceIntent.putExtra("originIndex",originIndex);
            requestAServiceIntent.putExtra("destinationIndex",destinationIndex);
            startActivityForResult(requestAServiceIntent,300);
        }
    }



//    void fillSpinner() {
//        locations = new ArrayList<>();
//        for (int i=0;i<locationsSpinner.size();i++) {
//            locations.add(Utility.utility.formatLocation(locationsSpinner.get(i)));
//        }
//        locations.add("Create a new");
//        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,locations);
//        originEditText.setAdapter(locationAdapter);
//        destinationEditText.setAdapter(locationAdapter);
//        updateSpinnerUI();
//    }

//    void updateSpinnerUI() {
//        originDetails.setVisibility(View.GONE);
//        destinationDetails.setVisibility(View.GONE);
//        if (originEditText.getSelectedItem().toString().equals("Create a new")) {
//            originDetails.setVisibility(View.VISIBLE);
//        }
//        if (destinationEditText.getSelectedItem().toString().equals("Create a new")) {
//            destinationDetails.setVisibility(View.VISIBLE);
//        }
//    }

//    void getLocation() {
//        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getActivity());
//        API api = Utility.utility.getAPIWithCookie(cookieJar);
//        String principle = Utility.utility.getLoggedName(this.getActivity());
//        Call<LocationResponse> locationResponseCall = api.getLocation("[[\"Location\",\"principle\",\"=\",\""+principle+"\"]]");
//        locationResponseCall.enqueue(new Callback<LocationResponse>() {
//            @Override
//            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
//                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
//                    locationsSpinner = response.body().locations;
////                    fillSpinner();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LocationResponse> call, Throwable t) {
//                fillSpinner();
//            }
//        });
//    }

//    void addLocation(final Location newLocation) {
//        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getActivity());
//        API api = Utility.utility.getAPIWithCookie(cookieJar);
//        HashMap<String,String> locationJSON = new HashMap<>();
//        locationJSON.put("kota",newLocation.city);
//        locationJSON.put("alamat",newLocation.address);
//        locationJSON.put("nama_gudang",newLocation.warehouse);
//        locationJSON.put("phone",newLocation.phone);
//        locationJSON.put("kode_distributor",newLocation.code);
//        locationJSON.put("principle",Utility.utility.getLoggedName(this.getActivity()));
//        locationsSpinner.add(newLocation);
//        Call<LocationCreation> locationResponseCall = api.createLocation(locationJSON);
//        locationResponseCall.enqueue(new Callback<LocationCreation>() {
//            @Override
//            public void onResponse(Call<LocationCreation> call, Response<LocationCreation> response) {
//                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
//                    for (int i=0;i<locationsSpinner.size();i++) {
//                        if (locationsSpinner.get(i).city.equals(newLocation.city) && locationsSpinner.get(i).warehouse.equals(newLocation.warehouse) && locationsSpinner.get(i).city.equals(newLocation.city) && locationsSpinner.get(i).code.equals(newLocation.code) && locationsSpinner.get(i).address.equals(newLocation.address)) {
//                            locationsSpinner.get(i).name = response.body().newLocation.name;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LocationCreation> call, Throwable t) {
//
//            }
//        });
//    }

}
