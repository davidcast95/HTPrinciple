package huang.android.logistic_principle.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import huang.android.logistic_principle.Base.SpinnerStringAdapter;
import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonData;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonResponse;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 1/20/18.
 */

public class AddStopLocation extends AppCompatActivity {
    EditText principleName, principleCP, stopLocation, itemInfo, remark;
    Spinner principleDropdown, statusDropdown;
    List<PrincipleContactPersonData> cps = null;
    List<String> principleCPs = new ArrayList<>();
    List<String> status = new ArrayList<>();
    SpinnerStringAdapter principleDropdownAdapter;

    String newPrincipleCP = "";


    public static Location location = new Location();
    boolean insertedCP=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        principleCPs.add(getString(R.string.create_a_new));
        setContentView(R.layout.activity_add_stop_location);
        setTitle(getString(R.string.add_stop_location));

        TextView typeText = (TextView)findViewById(R.id.type_text);
        Utility.utility.setFont(typeText,Hind.LIGHT,getApplicationContext());
        TextView locationText = (TextView)findViewById(R.id.location_text);
        Utility.utility.setFont(locationText,Hind.LIGHT,getApplicationContext());
        TextView cpText = (TextView)findViewById(R.id.cp_text);
        Utility.utility.setFont(cpText,Hind.LIGHT,getApplicationContext());
        TextView nameText = (TextView)findViewById(R.id.name_text);
        Utility.utility.setFont(nameText,Hind.LIGHT,getApplicationContext());
        TextView phoneText = (TextView)findViewById(R.id.phone_text);
        Utility.utility.setFont(phoneText,Hind.LIGHT,getApplicationContext());
        TextView itemInfoText = (TextView)findViewById(R.id.item_info_text);
        Utility.utility.setFont(itemInfoText,Hind.LIGHT,getApplicationContext());
        TextView remarkText = (TextView)findViewById(R.id.remark_text);
        Utility.utility.setFont(remarkText,Hind.LIGHT,getApplicationContext());
        TextView warning1 = (TextView)findViewById(R.id.warning1);
        Utility.utility.setFont(warning1,Hind.LIGHT,getApplicationContext());
        TextView warning2 = (TextView)findViewById(R.id.warning2);
        Utility.utility.setFont(warning2,Hind.LIGHT,getApplicationContext());
        TextView warning3 = (TextView)findViewById(R.id.warning3);
        Utility.utility.setFont(warning3,Hind.LIGHT,getApplicationContext());
        TextView warning4 = (TextView)findViewById(R.id.warning4);
        Utility.utility.setFont(warning4,Hind.LIGHT,getApplicationContext());

        statusDropdown = (Spinner)findViewById(R.id.spinner_status);
        status.add("Pick Up"); status.add("Drop");
        SpinnerStopLocationTypeAdapter statusAdapter = new SpinnerStopLocationTypeAdapter(this,status);
        statusDropdown.setAdapter(statusAdapter);

        stopLocation = (EditText)findViewById(R.id.stop_location);
        Utility.utility.setFont(stopLocation, Hind.MEDIUM,getApplicationContext());
        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationTapped();
            }
        });

        principleName = (EditText)findViewById(R.id.ras_principle_name);
        Utility.utility.setFont(principleName, Hind.MEDIUM,getApplicationContext());
        Utility.utility.setTextView(principleName, location.pic);
        principleCP = (EditText)findViewById(R.id.ras_principle_cp);
        Utility.utility.setFont(principleCP, Hind.MEDIUM,getApplicationContext());
        Utility.utility.setTextView(principleCP, location.phone);

//        principleDropdown = (Spinner)findViewById(R.id.ras_principle_dropdown);
//        principleDropdownAdapter = new SpinnerStringAdapter(getApplicationContext(),principleCPs);
//        principleDropdown.setAdapter(principleDropdownAdapter);
//
//        updateCPDropdown();

        itemInfo = (EditText)findViewById(R.id.item);
        Utility.utility.setFont(itemInfo, Hind.MEDIUM,getApplicationContext());
        remark = (EditText)findViewById(R.id.remark);
        Utility.utility.setFont(remark, Hind.MEDIUM,getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
//        fetchCPs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm_titlebar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_yes:
                if (validate()) {
                    addStopLocation();
                } else {
                    return false;
                }
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            stopLocation.setText(Html.fromHtml(Utility.utility.longFormatLocation(location)));

            Utility.utility.setTextView(principleName, location.pic);
            Utility.utility.setTextView(principleCP, location.phone);
        }
    }

    void stopLocationTapped() {
        Intent intent = new Intent(AddStopLocation.this, SearchLocation.class);
        intent.putExtra("mode","stop");
        startActivityForResult(intent,100);
    }

    boolean validate() {
        if (stopLocation.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.warning_location), Toast.LENGTH_SHORT).show();
            return false;
        } else if (principleName.getText().toString().equals("") || principleCP.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),getString(R.string.warning_cp),Toast.LENGTH_SHORT).show();
            return false;
//        } else if (principleDropdown.getSelectedItem().toString().equals(getString(R.string.create_a_new)) && !insertedCP) {
//            insertNewCP(principleName.getText().toString(), principleCP.getText().toString());
//            return false;
        }
//        else if (itemInfo.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(),getString(R.string.warning_item_info),Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    void addStopLocation() {
        JobOrderRouteData jobOrderRouteData = new JobOrderRouteData();
        jobOrderRouteData.type = statusDropdown.getSelectedItem().toString();
        jobOrderRouteData.location = location.name;
        jobOrderRouteData.warehouse_name = location.warehouse;
        jobOrderRouteData.distributor_code = location.code;
        jobOrderRouteData.city = location.city;
        jobOrderRouteData.address = location.address;
//        if (newPrincipleCP.equals("")) {
//            jobOrderRouteData.contact = principleDropdown.getSelectedItem().toString();
//        } else {
//            jobOrderRouteData.contact = newPrincipleCP;
//        }
        jobOrderRouteData.contact = null;
        jobOrderRouteData.nama = principleName.getText().toString();
        jobOrderRouteData.phone = principleCP.getText().toString();
        jobOrderRouteData.item_info = itemInfo.getText().toString();
        jobOrderRouteData.remark = remark.getText().toString();
        jobOrderRouteData.loc = location;

        String mode = getIntent().getStringExtra("mode");
        if (mode.equals("origin"))
            Home.origin = jobOrderRouteData;
        else if (mode.equals("destination"))
            Home.destination = jobOrderRouteData;
        else
            Home.routes.add(jobOrderRouteData);
        setResult(RESULT_OK);
        location = new Location();
        finish();
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
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String name = Utility.utility.getLoggedName(this);
        Call<PrincipleContactPersonResponse> callCP = api.getPrincipleCP("[[\"Principle Contact Person\", \"principle\", \"=\", \"" + name + "\"]]");

        final Activity thisActivity = this;

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
                        principleCPs.add(cps.get(i).id);
                    }
                    principleCPs.add(getString(R.string.create_a_new));
                    principleDropdownAdapter = new SpinnerStringAdapter(getApplicationContext(),principleCPs);
                    principleDropdown.setAdapter(principleDropdownAdapter);
                    principleDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (cps == null) {
                                return;
                            }
                            if (i >= cps.size()) {
                                newPrincipleCP = "";
                                principleName.setText("");
                                principleCP.setText("");
                            } else {
                                newPrincipleCP = "";
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


    void insertNewCP(String name, String phone) {
        final PrincipleContactPersonData cpData = new PrincipleContactPersonData();
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
                if (Utility.utility.catchResponse(getApplicationContext(), response, json)) {
                    insertedCP = true;
                    newPrincipleCP = cpData.name + " (" + cpData.principle + ")";
                    validate();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }
}
