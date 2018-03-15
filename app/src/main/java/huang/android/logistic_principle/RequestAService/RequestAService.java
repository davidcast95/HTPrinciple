package huang.android.logistic_principle.RequestAService;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import huang.android.logistic_principle.Base.SpinnerStringAdapter;
import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Home.AddStopLocation;
import huang.android.logistic_principle.Home.EditStopLocation;
import huang.android.logistic_principle.Home.Home;
import huang.android.logistic_principle.Home.SearchLocation;
import huang.android.logistic_principle.Home.StopLocationAdapter;
import huang.android.logistic_principle.Model.JobOrder.JobOrderCreationResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteCreationResponse;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonData;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonResponse;
import huang.android.logistic_principle.Model.Truck.TruckTypeResponse;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import com.google.android.gms.vision.text.Line;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RequestAService extends AppCompatActivity {

    RelativeLayout loading;
    ListView stopLocationList;
    LinearLayout addStopButton, chooseVendor, vendorInfo, changeVendor;
    TextView originWarning, destinationWarning, refIDWarning, vendorName, vendorAddress, additionalStopLocation;
    EditText  refID, principleName, principleCP, loadDateEditText, unloadDateEditText, cargoInfoEditText, notesEditText, volumeTextEdit;

    EditText originEditText, destinationEditText;
    LinearLayout originDetails, destinationDetails;
    RelativeLayout originIndicator, destinationIndicator;
    Button chooseVendorButton, changeVendorButton, requestButton;
    Spinner principleDropdown, trucktypeDropdown, metricDropdown;
    CheckBox strictCheckbox;
    ImageView vendorImageView, originIcon, destinationIcon, pickUpOrigin, dropOrigin, pickUpDestination, dropDestination;

    List<PrincipleContactPersonData> cps = null;

    List<String> principleCPs = new ArrayList<>();
    List<String> metrics = new ArrayList<>();
    List<String> truckTypes = new ArrayList<>();

    Calendar calendar = Calendar.getInstance();

    int originIndex = 0, destinationIndex = 0;

    final int LOAD_DATE_PICKER_DIALOG = 0;
    final int UNLOAD_DATE_PICKER_DIALOG = 1;
    int datePickerState = -1;
    boolean insertedCP = false, isRequestButtonClick = false;

    Date load_date, unload_date;
    Boolean isLoading=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        principleCPs.add(getString(R.string.create_a_new));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_aservice);

        TextView routeInfoText = (TextView)findViewById(R.id.route_info_text);
        Utility.utility.setFont(routeInfoText,Hind.BOLD,getApplicationContext());
        TextView addStopButtonText = (TextView)findViewById(R.id.add_stop_button_text);
        Utility.utility.setFont(addStopButtonText,Hind.LIGHT,getApplicationContext());
        TextView vendorText = (TextView)findViewById(R.id.vendor_info_text);
        Utility.utility.setFont(vendorText,Hind.BOLD,getApplicationContext());
        TextView refText = (TextView)findViewById(R.id.ref_text);
        Utility.utility.setFont(refText,Hind.LIGHT,getApplicationContext());
        TextView cpText = (TextView)findViewById(R.id.contact_info_text);
        Utility.utility.setFont(cpText,Hind.BOLD,getApplicationContext());
        TextView nameText = (TextView)findViewById(R.id.name_text);
        Utility.utility.setFont(nameText,Hind.LIGHT,getApplicationContext());
        TextView phoneText = (TextView)findViewById(R.id.phone_text);
        Utility.utility.setFont(phoneText,Hind.LIGHT,getApplicationContext());
        TextView cargoText = (TextView)findViewById(R.id.cargo_info_text);
        Utility.utility.setFont(cargoText,Hind.BOLD,getApplicationContext());
        TextView pickText = (TextView)findViewById(R.id.pick_date_text);
        Utility.utility.setFont(pickText,Hind.LIGHT,getApplicationContext());
        TextView delivText = (TextView)findViewById(R.id.delivered_date_text);
        Utility.utility.setFont(delivText,Hind.LIGHT,getApplicationContext());
        TextView notesText = (TextView)findViewById(R.id.notes_text);
        Utility.utility.setFont(notesText,Hind.LIGHT,getApplicationContext());
        TextView volText = (TextView)findViewById(R.id.vol_text);
        Utility.utility.setFont(volText,Hind.LIGHT,getApplicationContext());
        TextView expectedTruckText = (TextView)findViewById(R.id.expected_truck_type_text);
        Utility.utility.setFont(expectedTruckText,Hind.LIGHT,getApplicationContext());

        TextView warning1 = (TextView)findViewById(R.id.warning1);
        Utility.utility.setFont(warning1,Hind.LIGHT,getApplicationContext());
        TextView warning2 = (TextView)findViewById(R.id.warning2);
        Utility.utility.setFont(warning2,Hind.LIGHT,getApplicationContext());
        TextView warning3 = (TextView)findViewById(R.id.warning3);
        Utility.utility.setFont(warning3,Hind.LIGHT,getApplicationContext());
        TextView warning4 = (TextView)findViewById(R.id.warning4);
        Utility.utility.setFont(warning4,Hind.LIGHT,getApplicationContext());

        vendorImageView = (ImageView)findViewById(R.id.ras_vendor_profil_picture);


        originIndicator = (RelativeLayout)findViewById(R.id.origin_indicator);
        destinationIndicator = (RelativeLayout)findViewById(R.id.destination_indicator);
        additionalStopLocation = (TextView)findViewById(R.id.additional_stop_location);
        Utility.utility.setFont(additionalStopLocation, Hind.LIGHT,getApplicationContext());
        if (Home.routes.size() > 0) {
            additionalStopLocation.setVisibility(View.VISIBLE);
        } else {
            additionalStopLocation.setVisibility(View.GONE);
        }
        originIcon = (ImageView)findViewById(R.id.origin_icon);
        destinationIcon = (ImageView)findViewById(R.id.destination_icon);

        originIndicator = (RelativeLayout)findViewById(R.id.origin_indicator);
        destinationIndicator = (RelativeLayout)findViewById(R.id.destination_indicator);

        pickUpOrigin = (ImageView)findViewById(R.id.pickup_origin_icon);
        dropOrigin = (ImageView)findViewById(R.id.drop_origin_icon);

        pickUpDestination = (ImageView)findViewById(R.id.pickup_destination_icon);
        dropDestination = (ImageView)findViewById(R.id.drop_destination_icon);


        loading=(RelativeLayout)findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        addStopButton = (LinearLayout) findViewById(R.id.add_stop_button);
        stopLocationList = (ListView)findViewById(R.id.stop_location_list);

        addStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStopButtonClicked();
            }
        });

        initStopLocation();

        chooseVendor = (LinearLayout)findViewById(R.id.ras_choose_vendor);
        vendorInfo = (LinearLayout)findViewById(R.id.ras_vendor_information);
        changeVendor = (LinearLayout)findViewById(R.id.ras_change_vendor);

        originEditText = (EditText) findViewById(R.id.home_origin);
        Utility.utility.setFont(originEditText,Hind.MEDIUM,getApplicationContext());
        destinationEditText = (EditText) findViewById(R.id.home_destination);
        Utility.utility.setFont(destinationEditText,Hind.MEDIUM,getApplicationContext());
        originDetails = (LinearLayout) findViewById(R.id.home_origin_details) ;
        destinationDetails = (LinearLayout) findViewById(R.id.home_destination_details) ;

        originWarning = (TextView)findViewById(R.id.home_origin_warning);
        Utility.utility.setFont(originWarning,Hind.LIGHT,getApplicationContext());
        destinationWarning = (TextView)findViewById(R.id.home_destination_warning);
        Utility.utility.setFont(destinationWarning,Hind.LIGHT,getApplicationContext());
        refIDWarning = (TextView)findViewById(R.id.ras_ref_id_warning);
        Utility.utility.setFont(refIDWarning,Hind.LIGHT,getApplicationContext());
        refID = (EditText)findViewById(R.id.ras_ref_id);
        Utility.utility.setFont(refID,Hind.MEDIUM,getApplicationContext());
        vendorName = (TextView)findViewById(R.id.ras_vendor_name);
        Utility.utility.setFont(vendorName,Hind.MEDIUM,getApplicationContext());
        vendorAddress = (TextView)findViewById(R.id.ras_vendor_address);
        Utility.utility.setFont(vendorAddress,Hind.LIGHT,getApplicationContext());
        principleName = (EditText) findViewById(R.id.ras_principle_name);
        Utility.utility.setFont(principleName,Hind.MEDIUM,getApplicationContext());
        principleCP = (EditText) findViewById(R.id.ras_principle_cp);
        Utility.utility.setFont(principleCP,Hind.MEDIUM,getApplicationContext());
        loadDateEditText = (EditText)findViewById(R.id.ras_cargo_load_date);
        Utility.utility.setFont(loadDateEditText,Hind.MEDIUM,getApplicationContext());
        unloadDateEditText = (EditText)findViewById(R.id.ras_cargo_unload_date);
        Utility.utility.setFont(unloadDateEditText,Hind.MEDIUM,getApplicationContext());
        cargoInfoEditText = (EditText)findViewById(R.id.ras_cargo_info);
        notesEditText = (EditText)findViewById(R.id.ras_cargo_notes);
        Utility.utility.setFont(notesEditText,Hind.MEDIUM,getApplicationContext());
        volumeTextEdit = (EditText)findViewById(R.id.ras_volume);
        Utility.utility.setFont(volumeTextEdit,Hind.MEDIUM,getApplicationContext());
        strictCheckbox = (CheckBox)findViewById(R.id.ras_strict);
        Utility.utility.setFont(strictCheckbox,Hind.MEDIUM,getApplicationContext());


        chooseVendorButton = (Button)findViewById(R.id.ras_choose_vendor_button);
        Utility.utility.setFont(chooseVendorButton,Hind.BOLD,getApplicationContext());
        changeVendorButton = (Button)findViewById(R.id.ras_change_vendor_button);
        Utility.utility.setFont(changeVendorButton,Hind.BOLD,getApplicationContext());
        requestButton = (Button)findViewById(R.id.ras_request_button);
        Utility.utility.setFont(requestButton,Hind.BOLD,getApplicationContext());

        principleDropdown = (Spinner)findViewById(R.id.ras_principle_dropdown);
        SpinnerStringAdapter principleDropdownAdapter = new SpinnerStringAdapter(getApplicationContext(),principleCPs);
        principleDropdown.setAdapter(principleDropdownAdapter);

        trucktypeDropdown = (Spinner)findViewById(R.id.ras_truck_type_dropdown);

        metricDropdown = (Spinner)findViewById(R.id.ras_volume_metric);
        metrics.add("m3"); metrics.add("ton");
        SpinnerStringAdapter volumeMetricsDropdownAdapter = new SpinnerStringAdapter(this, metrics);
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
                if (originEditText.getText().toString().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), AddStopLocation.class);
                    intent.putExtra("mode", "origin");
                    startActivityForResult(intent, 100);
                } else {
                    Intent intent = new Intent(getApplicationContext(), EditStopLocation.class);
                    intent.putExtra("source", "origin");
                    startActivityForResult(intent, 100);
                }
            }
        });

        destinationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (destinationEditText.getText().toString().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), AddStopLocation.class);
                    intent.putExtra("mode", "destination");
                    startActivityForResult(intent, 200);
                } else {
                    Intent intent = new Intent(getApplicationContext(), EditStopLocation.class);
                    intent.putExtra("source", "destination");
                    startActivityForResult(intent, 200);
                }
            }
        });
        Utility.utility.setEditText(originEditText,Utility.utility.longFormatLocation(Home.origin.loc));
        Utility.utility.setEditText(destinationEditText, Utility.utility.longFormatLocation(Home.destination.loc));

        updateOriginIndicator();
        updateDestinationIndicator();

        Utility.utility.setFont(requestButton,Hind.BOLD,getApplicationContext());
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
        updateStateUI();

        setTitle(R.string.request_a_service);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStopLocationButton();
        fetchCPs();
        fetchTruckTypes();
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
                if (!isLoading)
                    this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name"),
                        address = data.getStringExtra("address"),
                        profileImage = data.getStringExtra("profile_image");
                vendorName.setText(name);
                vendorAddress.setText(address);

                if (profileImage != null) {
                    String imageUrl = profileImage;
                    MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getApplicationContext());
                    API api = Utility.utility.getAPIWithCookie(cookieJar);
                    Call<ResponseBody> callImage = api.getImage(imageUrl);
                    callImage.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                ResponseBody responseBody = response.body();
                                if (responseBody != null) {
                                    Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                                    vendorImageView.setImageBitmap(bm);
                                    vendorImageView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.White));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
                updateStateUI();
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(originEditText,Utility.utility.longFormatLocation(Home.origin.loc));
            updateStopLocationButton();
            updateOriginIndicator();
        }
        updateStopLocation();
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Utility.utility.setEditText(destinationEditText, Utility.utility.longFormatLocation(Home.destination.loc));
            updateStopLocationButton();
            updateDestinationIndicator();
        }
    }

    //Actions

    void updateOriginIndicator() {
        originIndicator.setVisibility(View.GONE);
        originIcon.setVisibility(View.VISIBLE);
        if (Home.origin.type.equals("Pick Up")) {
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
    void updateDestinationIndicator() {
        destinationIndicator.setVisibility(View.GONE);
        destinationIcon.setVisibility(View.VISIBLE);
        if (Home.destination.type.equals("Pick Up")) {
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
    void addStopButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), AddStopLocation.class);
        intent.putExtra("mode","stop");
        startActivityForResult(intent,250);
    }

    void initStopLocation() {
        StopLocationAdapter stopLocationAdapter = new StopLocationAdapter(getApplicationContext(),Home.routes);
        stopLocationList.setAdapter(stopLocationAdapter);
        int height = getIntent().getIntExtra("listHeight",0);
        Utility.utility.setListViewHeigth(stopLocationList,height);
    }

    void updateStopLocation() {
        if (Home.routes.size() > 0) {
            additionalStopLocation.setVisibility(View.VISIBLE);
        } else {
            additionalStopLocation.setVisibility(View.GONE);
        }
        StopLocationAdapter stopLocationAdapter = new StopLocationAdapter(getApplicationContext(),Home.routes);
        stopLocationAdapter.setOnItemDelete(new Runnable() {
            @Override
            public void run() {
                Home.listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
            }
        });
        stopLocationList.setAdapter(stopLocationAdapter);
        Home.listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
    }

    void updateStopLocationButton() {
        if (originEditText.getText().toString().equals("")) {
            addStopButton.setVisibility(View.GONE);
        } else {
            addStopButton.setVisibility(View.VISIBLE);
        }
    }
    void chooseVendorButtonClicked() {
        Intent chooseVendorIntent = new Intent(this, ChooseVendor.class);
        startActivityForResult(chooseVendorIntent, 1);
    }
    void requestButtonClicked() {
        if (!isLoading) {
            isRequestButtonClick = true;
            updateStateUI();
        }
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
                if (loadDate.equals("") || unloadDate.equals("")) {
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
                    } else if (principleDropdown.equals(getString(R.string.create_a_new)) && !insertedCP) {
                        insertedCP = false;
                        insertNewCP(nameCP, phoneCP);
                    } else if (isRequestButtonClick) {
                        sendRequestAService(ref, name, cp, vendor, Utility.utility.dateToFormatDatabase(load_date), Utility.utility.dateToFormatDatabase(unload_date), origin, cargoInfo, notes, dest,volume, truckTypeDropdown, strict);
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
            Utility.utility.setEditText(loadDateEditText,sdf.format(calendar.getTime()));
        } else if (datePickerState == UNLOAD_DATE_PICKER_DIALOG) {
            unload_date = calendar.getTime();
            Utility.utility.setEditText(unloadDateEditText,sdf.format(calendar.getTime()));
        }
    }

    void updateCPDropdown() {
        if (principleDropdown.getSelectedItem().toString().equals(getString(R.string.create_a_new))) {
            principleName.setEnabled(true);
            principleCP.setEnabled(true);
        } else {
            principleName.setEnabled(false);
            principleCP.setEnabled(false);
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


        callCP.enqueue(new Callback<PrincipleContactPersonResponse>() {
            @Override
            public void onResponse(Call<PrincipleContactPersonResponse> call, Response<PrincipleContactPersonResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    PrincipleContactPersonResponse cpResponse = response.body();
                    if (cpResponse.cps == null) {
                        return;
                    }
                    cps = cpResponse.cps;
                    principleCPs.clear();
                    for (int i = 0; i < cps.size(); i++) {
                        principleCPs.add(cps.get(i).name + " (" + cps.get(i).phone + ")");
                    }
                    principleCPs.add(getString(R.string.create_a_new));
                    principleDropdown = (Spinner)findViewById(R.id.ras_principle_dropdown);
                    SpinnerStringAdapter principleDropdownAdapter = new SpinnerStringAdapter(getApplicationContext(),principleCPs);
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
                            updateCPDropdown();
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
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    truckTypes = new ArrayList<String>();
                    for (int i=0;i<response.body().truckTypes.size();i++)
                        truckTypes.add(response.body().truckTypes.get(i).name);
                    SpinnerStringAdapter truckTypeAdapter = new SpinnerStringAdapter(thisActivity, truckTypes);
                    trucktypeDropdown.setAdapter(truckTypeAdapter);
                }
            }

            @Override
            public void onFailure(Call<TruckTypeResponse> call, Throwable t) {

            }
        });
    }


    void insertNewCP(String name, String phone) {
        PrincipleContactPersonData cpData = new PrincipleContactPersonData();
        cpData.name = name;
        cpData.phone = phone;
        cpData.principle = Utility.utility.getLoggedName(this);
        final String json = new Gson().toJson(cpData);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JSONObject> insertCPResponseCall = api.insertPrincipleCP(cpData);
        insertCPResponseCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response,json)) {
                    insertedCP = true;
                    updateStateUI();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }

    boolean jobOrderRequestSuccess = false;

    void sendRequestAService(String ref_id, String principleCPName, String principleCP, final String vendor, String load_date, String unload_date, String origin, String cargoInfo, String notes, String destination, String volume, String truckType, Boolean strict) {
        if (isLoading) return;
        loading.setVisibility(View.VISIBLE);
        isLoading = true;
        String name = Utility.utility.getLoggedName(this);

        JobOrderData jobOrderData = new JobOrderData();
        jobOrderData.docstatus = 1;
        jobOrderData.ref = ref_id;
        jobOrderData.status = JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION;
        jobOrderData.principle = name;

        jobOrderData.principle_cp = principleCPName + " (" + name + ")";
        jobOrderData.principle_cp_name = principleCPName;
        jobOrderData.principle_cp_phone = principleCP;
        jobOrderData.vendor = vendor;
        jobOrderData.etd = load_date;
        jobOrderData.eta = unload_date;

//            jobOrderData.origin = Home.origin.loc.name;
//            jobOrderData.origin_address = Home.origin.address;
//            jobOrderData.origin_city = Home.origin.city;
//            jobOrderData.origin_code = Home.origin.loc.code;
//            jobOrderData.origin_warehouse = Home.origin.loc.warehouse;
//
//
//            jobOrderData.destination = Home.destination.loc.name;
//            jobOrderData.destination_address = Home.destination.address;
//            jobOrderData.destination_city = Home.destination.city;
//            jobOrderData.destination_code = Home.destination.loc.code;
//            jobOrderData.destination_warehouse = Home.destination.loc.warehouse;

        jobOrderData.routes = new ArrayList<>();
        jobOrderData.routes.add(Home.origin);
        jobOrderData.routes.add(Home.destination);
        for (int i=0;i<Home.routes.size();i++) {
            jobOrderData.routes.add(Home.routes.get(i));
        }

        jobOrderData.cargoInfo = cargoInfo;
        jobOrderData.notes = notes;
        jobOrderData.estimate_volume = volume;
        jobOrderData.suggest_truck_type = truckType;
        jobOrderData.strict = strict ? 1 : 0;

        final String json = new Gson().toJson(jobOrderData);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderCreationResponse> jobOrderResponseCall = api.submitJobOrder(jobOrderData);
        jobOrderResponseCall.enqueue(new Callback<JobOrderCreationResponse>() {
            @Override
            public void onResponse(Call<JobOrderCreationResponse> call, Response<JobOrderCreationResponse> response) {
                isLoading = false;
                if (Utility.utility.catchResponse(getApplicationContext(), response, json)) {
                    jobOrderRequestSuccess = true;
                    Home.routes.clear();
                    Toast.makeText(getApplicationContext(),"Your request has been sent and wait to be approved by Vendor",Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JobOrderCreationResponse> call, Throwable t) {
                loading.setVisibility(View.GONE);
                isLoading = false;
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }


}
