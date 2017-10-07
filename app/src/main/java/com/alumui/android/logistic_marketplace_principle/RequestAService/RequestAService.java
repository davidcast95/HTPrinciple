package com.alumui.android.logistic_marketplace_principle.RequestAService;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderCreationResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderStatus;
import com.alumui.android.logistic_marketplace_principle.Model.MyCookieJar;
import com.alumui.android.logistic_marketplace_principle.Model.Principle.PrincipleContactPersonData;
import com.alumui.android.logistic_marketplace_principle.Model.Principle.PrincipleContactPersonResponse;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.alumui.android.logistic_marketplace_principle.Utility;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    TextView originWarning, destinationWarning, vendorName, vendorAddress;
    EditText originEditText, destinationEditText, principleName, principleCP, loadDateEditText, unloadDateEditText, cargoInfoEditText, notesEditText;
    Button chooseVendorButton, changeVendorButton, requestButton;
    Spinner principleDropdown;

    List<PrincipleContactPersonData> cps = null;

    String[] principleCPs = new String[]{ "Create a new" };

    Calendar calendar = Calendar.getInstance();

    final int LOAD_DATE_PICKER_DIALOG = 0;
    final int UNLOAD_DATE_PICKER_DIALOG = 1;
    int datePickerState = -1;
    boolean insertedCP = false;

    Date load_date, unload_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_aservice);


        Intent intent = getIntent();
        String origin = intent.getStringExtra("origin"),
                destination = intent.getStringExtra("destination");
        chooseVendor = (LinearLayout)findViewById(R.id.ras_choose_vendor);
        vendorInfo = (LinearLayout)findViewById(R.id.ras_vendor_information);
        changeVendor = (LinearLayout)findViewById(R.id.ras_change_vendor);

        originEditText = (EditText)findViewById(R.id.ras_origin);
        destinationEditText = (EditText)findViewById(R.id.ras_destination);
        originWarning = (TextView)findViewById(R.id.ras_origin_warning);
        destinationWarning = (TextView)findViewById(R.id.ras_destination_warning);
        vendorName = (TextView)findViewById(R.id.ras_vendor_name);
        vendorAddress = (TextView)findViewById(R.id.ras_vendor_address);
        principleName = (EditText) findViewById(R.id.ras_principle_name);
        principleCP = (EditText) findViewById(R.id.ras_principle_cp);
        loadDateEditText = (EditText)findViewById(R.id.ras_cargo_load_date);
        unloadDateEditText = (EditText)findViewById(R.id.ras_cargo_unload_date);
        cargoInfoEditText = (EditText)findViewById(R.id.ras_cargo_info);
        notesEditText = (EditText)findViewById(R.id.ras_cargo_notes);


        chooseVendorButton = (Button)findViewById(R.id.ras_choose_vendor_button);
        changeVendorButton = (Button)findViewById(R.id.ras_change_vendor_button);
        requestButton = (Button)findViewById(R.id.ras_request_button);

        principleDropdown = (Spinner)findViewById(R.id.ras_principle_dropdown);
        ArrayAdapter<String> principleDropdownAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, principleCPs);
        principleDropdown.setAdapter(principleDropdownAdapter);

        fetchCPs();
        cargoInfoEditText.setVerticalScrollBarEnabled(true);
        notesEditText.setVerticalScrollBarEnabled(true);

        originEditText.setText(origin);
        destinationEditText.setText(destination);

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
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });


        updateStateUI();

        setTitle(R.string.request_a_service);
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
        updateStateUI();
    }

    void updateStateUI() {
        String vendor = vendorName.getText().toString();
        if (vendor.equals("")) {
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

                    String phoneCP = this.principleCP.getText().toString(), nameCP = principleName.getText().toString();
                    String principleDropdown = this.principleDropdown.getSelectedItem().toString();
                    if (principleDropdown.equals("Create a new") && insertedCP == false) {
                        insertedCP = false;
                        insertNewCP(nameCP, phoneCP);
                    } else {
                        sendRequestAService(name, vendor, Utility.utility.dateToFormatDatabase(load_date), Utility.utility.dateToFormatDatabase(unload_date), origin, cargoInfo, notes, dest);
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

    void sendRequestAService(String principleCP, String vendor, String load_date, String unload_date, String origin, String cargoInfo, String notes, String destination) {
        String name = Utility.utility.getLoggedName(this);

        JobOrderData jobOrderData = new JobOrderData();
        jobOrderData.docstatus = 1;
        jobOrderData.status = JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION;
        jobOrderData.principle = name;

        jobOrderData.principle_cp = principleCP + " (" + name + ")";
        jobOrderData.vendor = vendor;
        jobOrderData.etd = load_date;
        jobOrderData.eta = unload_date;
        jobOrderData.origin = origin;
        jobOrderData.destination = destination;
        jobOrderData.cargoInfo = cargoInfo;
        jobOrderData.notes = notes;

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
