package huang.android.logistic_principle.Home;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;
import java.util.List;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.RequestAService.RequestAService;
import huang.android.logistic_principle.Utility;

import static android.app.Activity.RESULT_OK;


public class Home extends Fragment {

    ListView stopLocationList;
    TextView originWarning, destinationWarning, additionalStopLocation;
    EditText originEditText, destinationEditText;
    Button requestAServiceButton;
    LinearLayout addStopButton;
    ImageView splashTruck;

    RelativeLayout originIndicator, destinationIndicator;
    ImageView originIcon, destinationIcon, pickUpOrigin, dropOrigin, pickUpDestination, dropDestination;

    public static List<JobOrderRouteData> routes = new ArrayList<>();
    public static JobOrderRouteData origin, destination;
    int originIndex = 0, destinationIndex = 0;

    JobOrderData jobOrderData;

    public static int listHeight = 0;


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

        TextView logisticService = (TextView)v.findViewById(R.id.logistic_service);
        Utility.utility.setFont(logisticService, Hind.BOLD, getActivity());

        TextView originText = (TextView)v.findViewById(R.id.origin_text);
        Utility.utility.setFont(originText, Hind.LIGHT, getActivity());

        TextView destinationText = (TextView)v.findViewById(R.id.destination_text);
        Utility.utility.setFont(destinationText, Hind.LIGHT, getActivity());

        originEditText = (EditText) v.findViewById(R.id.home_origin);
        Utility.utility.setFont(originEditText,Hind.MEDIUM, getActivity());
        destinationEditText = (EditText) v.findViewById(R.id.home_destination);
        Utility.utility.setFont(destinationEditText,Hind.MEDIUM, getActivity());
        additionalStopLocation = (TextView)v.findViewById(R.id.additional_stop_location);
        Utility.utility.setFont(additionalStopLocation,Hind.LIGHT, getActivity());
        originWarning = (TextView)v.findViewById(R.id.home_origin_warning);
        Utility.utility.setFont(originWarning,Hind.LIGHT, getActivity());
        destinationWarning = (TextView)v.findViewById(R.id.home_destination_warning);
        Utility.utility.setFont(destinationWarning,Hind.LIGHT, getActivity());
        requestAServiceButton = (Button)v.findViewById(R.id.request_a_service_button);
        Utility.utility.setFont(requestAServiceButton,Hind.BOLD, getActivity());
        addStopButton = (LinearLayout) v.findViewById(R.id.add_stop_button);
        TextView addStopButtonText = (TextView)v.findViewById(R.id.add_stop_button_text);
        Utility.utility.setFont(addStopButtonText,Hind.LIGHT, getActivity());
        stopLocationList = (ListView)v.findViewById(R.id.stop_location_list);

        originIcon = (ImageView)v.findViewById(R.id.origin_icon);
        destinationIcon = (ImageView)v.findViewById(R.id.destination_icon);

        originIndicator = (RelativeLayout)v.findViewById(R.id.origin_indicator);
        destinationIndicator = (RelativeLayout)v.findViewById(R.id.destination_indicator);

        pickUpOrigin = (ImageView)v.findViewById(R.id.pickup_origin_icon);
        dropOrigin = (ImageView)v.findViewById(R.id.drop_origin_icon);

        pickUpDestination = (ImageView)v.findViewById(R.id.pickup_destination_icon);
        dropDestination = (ImageView)v.findViewById(R.id.drop_destination_icon);

        originEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originEditText.getText().toString().equals("")) {
                    Intent intent = new Intent(getActivity(), AddStopLocation.class);
                    intent.putExtra("mode", "origin");
                    startActivityForResult(intent, 100);
                } else {
                    Intent intent = new Intent(getActivity(), EditStopLocation.class);
                    intent.putExtra("source", "origin");
                    startActivityForResult(intent, 100);
                }
            }
        });

        destinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (destinationEditText.getText().toString().equals("")) {
                    Intent intent = new Intent(getActivity(), AddStopLocation.class);
                    intent.putExtra("mode", "destination");
                    startActivityForResult(intent, 200);
                } else {
                    Intent intent = new Intent(getActivity(), EditStopLocation.class);
                    intent.putExtra("source", "destination");
                    startActivityForResult(intent, 200);
                }
            }
        });

        additionalStopLocation.setVisibility(View.GONE);

        addStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStopButtonTapped();
            }
        });

        requestAServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAServiceButtonClicked();
            }
        });
        updateStopLocationButton();
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(originEditText,Utility.utility.longFormatLocation(origin.loc));
            updateStopLocationButton();
            originIndicator.setVisibility(View.GONE);
            originIcon.setVisibility(View.VISIBLE);
            if (origin.type.equals("Pick Up")) {
                originIcon.setVisibility(View.GONE);
                originIndicator.setVisibility(View.VISIBLE);
                pickUpOrigin.setVisibility(View.VISIBLE);
                dropOrigin.setVisibility(View.GONE);
            } else {
                originIcon.setVisibility(View.GONE);
                originIndicator.setVisibility(View.VISIBLE);
                pickUpOrigin.setVisibility(View.GONE);
                dropOrigin.setVisibility(View.VISIBLE);
            }
        }
        updateStopLocation();
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(destinationEditText, Utility.utility.longFormatLocation(destination.loc));
            updateStopLocationButton();
            destinationIndicator.setVisibility(View.GONE);
            destinationIcon.setVisibility(View.VISIBLE);
            if (destination.type.equals("Pick Up")) {
                destinationIcon.setVisibility(View.GONE);
                destinationIndicator.setVisibility(View.VISIBLE);
                pickUpDestination.setVisibility(View.VISIBLE);
                dropDestination.setVisibility(View.GONE);
            } else {
                destinationIcon.setVisibility(View.GONE);
                destinationIndicator.setVisibility(View.VISIBLE);
                pickUpDestination.setVisibility(View.GONE);
                dropDestination.setVisibility(View.VISIBLE);
            }
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
            requestAServiceIntent.putExtra("listHeight",listHeight);
            startActivityForResult(requestAServiceIntent,300);
        }
    }

    void addStopButtonTapped() {
        Intent intent = new Intent(getActivity(), AddStopLocation.class);
        intent.putExtra("mode","stop");
        startActivityForResult(intent,250);
    }

    void updateStopLocation() {
        if (routes.size() > 0) {
            additionalStopLocation.setVisibility(View.VISIBLE);
        } else {
            additionalStopLocation.setVisibility(View.GONE);
        }
        StopLocationAdapter stopLocationAdapter = new StopLocationAdapter(getContext(),routes);
        stopLocationAdapter.setOnItemDelete(new Runnable() {
            @Override
            public void run() {
                listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
            }
        });
        stopLocationList.setAdapter(stopLocationAdapter);
        listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
    }

    void updateStopLocationButton() {
        if (originEditText.getText().toString().equals("") || destinationEditText.getText().toString().equals("")) {
            addStopButton.setVisibility(View.GONE);
        } else {
            addStopButton.setVisibility(View.VISIBLE);
        }
    }

//    void fillSpinner() {
//        locations = new ArrayList<>();
//        for (int i=0;i<locationsSpinner.size();i++) {
//            locations.add(Utility.utility.longFormatLocation(locationsSpinner.get(i)));
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
