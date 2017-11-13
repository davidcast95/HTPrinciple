package com.huang.android.logistic_principle.RequestAService;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic_principle.Home.Home;
import com.huang.android.logistic_principle.Home.SearchLocation;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderCreationResponse;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.Model.Principle.PrincipleContactPersonData;
import com.huang.android.logistic_principle.Model.Principle.PrincipleContactPersonResponse;
import com.huang.android.logistic_principle.Model.Truck.TruckTypeResponse;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.ServiceAPI.API;
import com.huang.android.logistic_principle.Utility;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RequestAService extends AppCompatActivity {

    LinearLayout chooseVendor, vendorInfo, changeVendor;
    TextView originWarning, destinationWarning, refIDWarning, vendorName, vendorAddress;
    EditText  refID, principleName, principleCP, loadDateEditText, unloadDateEditText, cargoInfoEditText, notesEditText, volumeTextEdit;

    EditText originEditText, destinationEditText;
    LinearLayout originDetails, destinationDetails;
    Button chooseVendorButton, changeVendorButton, requestButton;
    Spinner principleDropdown, trucktypeDropdown, metricDropdown;
    CheckBox strictCheckbox;

    List<PrincipleContactPersonData> cps = null;

    String[] principleCPs = new String[]{ "Create a new" };
    String[] metrics = new String[]{ "m3", "ton" };
    List<String> truckTypes = new ArrayList<>();

    Calendar calendar = Calendar.getInstance();

    int originIndex = 0, destinationIndex = 0;

    final int LOAD_DATE_PICKER_DIALOG = 0;
    final int UNLOAD_DATE_PICKER_DIALOG = 1;
    int datePickerState = -1;
    boolean insertedCP = false, isRequestButtonClick = false;

    Date load_date, unload_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_aservice);


        chooseVendor = (LinearLayout)findViewById(R.id.ras_choose_vendor);
        vendorInfo = (LinearLayout)findViewById(R.id.ras_vendor_information);
        changeVendor = (LinearLayout)findViewById(R.id.ras_change_vendor);

        originEditText = (EditText) findViewById(R.id.ras_origin);
        destinationEditText = (EditText) findViewById(R.id.ras_destination);
        originDetails = (LinearLayout) findViewById(R.id.home_origin_details) ;
        destinationDetails = (LinearLayout) findViewById(R.id.home_destination_details) ;

        originWarning = (TextView)findViewById(R.id.ras_origin_warning);
        destinationWarning = (TextView)findViewById(R.id.ras_destination_warning);
        refIDWarning = (TextView)findViewById(R.id.ras_ref_id_warning);
        refID = (EditText)findViewById(R.id.ras_ref_id);
        vendorName = (TextView)findViewById(R.id.ras_vendor_name);
        vendorAddress = (TextView)findViewById(R.id.ras_vendor_address);
        principleName = (EditText) findViewById(R.id.ras_principle_name);
        principleCP = (EditText) findViewById(R.id.ras_principle_cp);
        loadDateEditText = (EditText)findViewById(R.id.ras_cargo_load_date);
        unloadDateEditText = (EditText)findViewById(R.id.ras_cargo_unload_date);
        cargoInfoEditText = (EditText)findViewById(R.id.ras_cargo_info);
        notesEditText = (EditText)findViewById(R.id.ras_cargo_notes);
        volumeTextEdit = (EditText)findViewById(R.id.ras_volume);
        strictCheckbox = (CheckBox)findViewById(R.id.ras_strict);


        chooseVendorButton = (Button)findViewById(R.id.ras_choose_vendor_button);
        changeVendorButton = (Button)findViewById(R.id.ras_change_vendor_button);
        requestButton = (Button)findViewById(R.id.ras_request_button);

        principleDropdown = (Spinner)findViewById(R.id.ras_principle_dropdown);
        ArrayAdapter<String> principleDropdownAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, principleCPs);
        principleDropdown.setAdapter(principleDropdownAdapter);

        trucktypeDropdown = (Spinner)findViewById(R.id.ras_truck_type_dropdown);

        metricDropdown = (Spinner)findViewById(R.id.ras_volume_metric);
        ArrayAdapter<String> volumeMetricsDropdownAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, metrics);
        metricDropdown.setAdapter(volumeMetricsDropdownAdapter);

        cargoInfoEditText.setVerticalScrollBarEnabled(true);
        notesEditText.setVerticalScrollBarEnabled(true);

        originIndex = getIntent().getIntExtra("originIndex",0);
        destinationIndex = getIntent().getIntExtra("destinationIndex",0);

        chooseVendorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVendorButtonClicked();
            }
        });
        changeVendorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVendorButtonClicked();
            }
        });

        originEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestAService.this, SearchLocation.class);
                intent.putExtra("mode","origin");
                startActivityForResult(intent,100);
            }
        });

        destinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestAService.this, SearchLocation.class);
                intent.putExtra("mode","destination");
                startActivityForResult(intent,200);
            }
        });

        originEditText.setText(Utility.utility.formatLocation(Home.origin));
        destinationEditText.setText(Utility.utility.formatLocation(Home.destination));

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestButtonClicked();
            }
        });

        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateDateEditText();
            }
        };

        loadDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerState = LOAD_DATE_PICKER_DIALOG;
                DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAService.this, datePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                if (unload_date != null) {
                    datePickerDialog.getDatePicker().setMaxDate(unload_date.getTime());
                }
                datePickerDialog.show();
            }
        });

        unloadDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerState = UNLOAD_DATE_PICKER_DIALOG;
                DatePickerDialog datePickerDialog = new DatePickerDialog(RequestAService.this, datePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                if (load_date == null) {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                } else {
                    datePickerDialog.getDatePicker().setMinDate(load_date.getTime());
                }
                datePickerDialog.show();
            }
        });



        setTitle(R.string.request_a_service);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCPs();
        fetchTruckTypes();
        updateStateUI();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Actions
    void chooseVendorButtonClicked() {
        Intent chooseVendorIntent = new Intent(this, ChooseVendor.class);
        startActivityForResult(chooseVendorIntent, 1);
    }
    void requestButtonClicked() {
        isRequestButtonClick = true;
        updateStateUI();
    }

    boolean validateLocation() {
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


        return validate;
    }

    void updateStateUI() {
        String vendor = vendorName.getText().toString();
        if (!validateLocation()) return;

        if (refID.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill reference number", Toast.LENGTH_SHORT).show();
        }
        else if (vendor.equals("")) {
            chooseVendor.setVisibility(View.VISIBLE);
            vendorInfo.setVisibility(View.GONE);
            changeVendor.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please choose vendor first", Toast.LENGTH_SHORT).show();
        } else {
            chooseVendor.setVisibility(View.GONE);
            vendorInfo.setVisibility(View.VISIBLE);
            changeVendor.setVisibility(View.VISIBLE);
            String name = principleName.getText().toString(),
                    cp = principleCP.getText().toString();
            if (name.equals("") || cp.equals("")) {
                Toast.makeText(getApplicationContext(), "Please fill Contact Person", Toast.LENGTH_SHORT).show();
            } else {
                String loadDate = loadDateEditText.getText().toString(),
                        unloadDate = unloadDateEditText.getText().toString(),
                        cargoInfo = cargoInfoEditText.getText().toString(),
                        notes = notesEditText.getText().toString();
                if (loadDate.equals("") || unloadDate.equals("") || cargoInfo.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill Cargo Information", Toast.LENGTH_SHORT).show();
                } else {
                    String origin = originEditText.getText().toString(),
                            dest = destinationEditText.getText().toString();

                    String phoneCP = this.principleCP.getText().toString(), nameCP = principleName.getText().toString(), ref = refID.getText().toString();
                    String volume = this.volumeTextEdit.getText().toString() + " " + this.metricDropdown.getSelectedItem().toString();
                    String principleDropdown = this.principleDropdown.getSelectedItem().toString();
                    String truckTypeDropdown = this.trucktypeDropdown.getSelectedItem().toString();
                    Boolean strict = this.strictCheckbox.isChecked();
                    if (volume.equals("")) {
                        Toast.makeText(getApplicationContext(),"Volume cannot be empty",Toast.LENGTH_SHORT).show();
                    } else if (principleDropdown.equals("Create a new") && insertedCP == false) {
                        insertedCP = false;
                        insertNewCP(nameCP, phoneCP);
                    } else if (isRequestButtonClick) {
                        sendRequestAService(ref, name, vendor, Utility.utility.dateToFormatDatabase(load_date), Utility.utility.dateToFormatDatabase(unload_date), origin, cargoInfo, notes, dest,volume, truckTypeDropdown, strict);
                    }
                }
            }
        }
    }

    void updateDateEditText() {
        String myFormat = "EEE, d MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (datePickerState == LOAD_DATE_PICKER_DIALOG) {
            load_date = calendar.getTime();
            loadDateEditText.setText(sdf.format(calendar.getTime()));
        } else if (datePickerState == UNLOAD_DATE_PICKER_DIALOG) {
            unload_date = calendar.getTime();
            unloadDateEditText.setText(sdf.format(calendar.getTime()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name"),
                        address = data.getStringExtra("address");
                vendorName.setText(name);
                vendorAddress.setText(address);
                updateStateUI();
            }
        }
        if (requestCode == 100) {
            originEditText.setText(Utility.utility.formatLocation(Home.origin));
        }
        if (requestCode == 200) {
            destinationEditText.setText(Utility.utility.formatLocation(Home.destination));
        }
    }


    //API Connectivity
    void fetchCPs() {
        //create client to get cookies from OkHttp
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        String name = Utility.utility.getLoggedName(this);
        okHttpClient.cookieJar(cookieJar);
        OkHttpClient client = okHttpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        API api = retrofit.create(API.class);
        Call<PrincipleContactPersonResponse> callCP = api.getPrincipleCP("[[\"Principle Contact Person\", \"principle\", \"=\", \"" + name + "\"]]");

        final Activity thisActivity = this;

        callCP.enqueue(new Callback<PrincipleContactPersonResponse>() {
            @Override
            public void onResponse(Call<PrincipleContactPersonResponse> call, Response<PrincipleContactPersonResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    PrincipleContactPersonResponse cpResponse = response.body();
                    if (cpResponse.cps == null) {
                        return;
                    }
                    cps = cpResponse.cps;
                    principleCPs = new String[cps.size() + 1];
                    for (int i = 0; i < cps.size(); i++) {
                        principleCPs[i] = cps.get(i).name + " (" + cps.get(i).phone + ")";
                    }
                    principleCPs[cps.size()] = "Create a new";
                    principleDropdown = (Spinner) findViewById(R.id.ras_principle_dropdown);
                    ArrayAdapter<String> principleDropdownAdapter = new ArrayAdapter<String>(thisActivity, R.layout.support_simple_spinner_dropdown_item, principleCPs);
                    principleDropdown.setAdapter(principleDropdownAdapter);
                    principleDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (cps == null) {
                                return;
                            }
                            if (i >= cps.size()) {
                                principleName.setText("");
                                principleCP.setText("");
                            } else {
                                principleName.setText(cps.get(i).name);
                                principleCP.setText(cps.get(i).phone);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<PrincipleContactPersonResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }

    void fetchTruckTypes() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<TruckTypeResponse> callTruckType = api.getTruckType();

        final Activity thisActivity = this;

        callTruckType.enqueue(new Callback<TruckTypeResponse>() {
            @Override
            public void onResponse(Call<TruckTypeResponse> call, Response<TruckTypeResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    truckTypes = new ArrayList<String>();
                    for (int i=0;i<response.body().truckTypes.size();i++)
                        truckTypes.add(response.body().truckTypes.get(i).name);
                    ArrayAdapter<String> truckTypeAdapter = new ArrayAdapter<String>(thisActivity, R.layout.support_simple_spinner_dropdown_item, truckTypes);
                    trucktypeDropdown.setAdapter(truckTypeAdapter);
                }
            }

            @Override
            public void onFailure(Call<TruckTypeResponse> call, Throwable t) {

            }
        });
    }

    void sendRequestAService(String ref_id, String principleCP, final String vendor, String load_date, String unload_date, String origin, String cargoInfo, String notes, String destination, String volume, String truckType, Boolean strict) {
        String name = Utility.utility.getLoggedName(this);

        JobOrderData jobOrderData = new JobOrderData();
        jobOrderData.docstatus = 1;
        jobOrderData.ref = ref_id;
        jobOrderData.status = JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION;
        jobOrderData.principle = name;

        jobOrderData.principle_cp = principleCP + " (" + name + ")";
        jobOrderData.vendor = vendor;
        jobOrderData.etd = load_date;
        jobOrderData.eta = unload_date;

        jobOrderData.origin = Home.origin.name;
        jobOrderData.origin_address = Home.origin.address;
        jobOrderData.origin_city = Home.origin.city;
        jobOrderData.origin_code = Home.origin.code;
        jobOrderData.origin_warehouse = Home.origin.warehouse;


        jobOrderData.destination = Home.destination.name;
        jobOrderData.destination_address = Home.destination.address;
        jobOrderData.destination_city = Home.destination.city;
        jobOrderData.destination_code = Home.destination.code;
        jobOrderData.destination_warehouse = Home.destination.warehouse;

        jobOrderData.cargoInfo = cargoInfo;
        jobOrderData.notes = notes;
        jobOrderData.estimate_volume = volume;
        jobOrderData.suggest_truck_type = truckType;
        jobOrderData.strict = strict ? 1 : 0;

        String a = new Gson().toJson(jobOrderData);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderCreationResponse> jobOrderResponseCall = api.submitJobOrder(jobOrderData);
        final Activity activity = this;
        jobOrderResponseCall.enqueue(new Callback<JobOrderCreationResponse>() {
            @Override
            public void onResponse(Call<JobOrderCreationResponse> call, Response<JobOrderCreationResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    Toast.makeText(activity.getApplicationContext(),"Your request has been sent and wait to be approved by Vendor",Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            }

            @Override
            public void onFailure(Call<JobOrderCreationResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }

    void insertNewCP(String name, String phone) {
        PrincipleContactPersonData cpData = new PrincipleContactPersonData();
        cpData.name = name;
        cpData.phone = phone;
        cpData.principle = Utility.utility.getLoggedName(this);
        String json = new Gson().toJson(cpData);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JSONObject> insertCPResponseCall = api.insertPrincipleCP(cpData);
        final Activity activity = this;
        insertCPResponseCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    insertedCP = true;
                    updateStateUI();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }


}
