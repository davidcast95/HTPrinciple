package com.huang.android.logistic_principle.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.huang.android.logistic_principle.Model.Location.LocationCreation;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.ServiceAPI.API;
import com.huang.android.logistic_principle.Utility;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class AddLocation extends AppCompatActivity {

    EditText code, city, address, warehouse,phone,pic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        setTitle("Add Location");

        code = (EditText)findViewById(R.id.add_loc_code);
        city = (EditText)findViewById(R.id.add_loc_city);
        address = (EditText)findViewById(R.id.add_loc_address);
        warehouse = (EditText)findViewById(R.id.add_loc_warehouse);
        phone = (EditText)findViewById(R.id.add_loc_phone);
        pic = (EditText)findViewById(R.id.add_loc_pic);
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
                    addLocation();
                }
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    
    boolean validate() {
        boolean validate = true;
        if (warehouse.getText().toString().equals("")) {
            validate=false;
            Toast.makeText(getApplicationContext(),"Warehouse cannot be empty",Toast.LENGTH_SHORT).show();
        } else if (code.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(getApplicationContext(), "Distribution Code cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (address.getText().toString().equals("")) {
            validate=false;
            Toast.makeText(getApplicationContext(),"Address cannot be empty",Toast.LENGTH_SHORT).show();
        } else if (city.getText().toString().equals("")) {
            validate=false;
            Toast.makeText(getApplicationContext(),"City cannot be empty",Toast.LENGTH_SHORT).show();
        } else if (phone.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(getApplicationContext(), "Phone cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (pic.getText().toString().equals("")) {
            validate = false;
            Toast.makeText(getApplicationContext(), "PIC cannot be empty", Toast.LENGTH_SHORT).show();
        }
        return validate;
    }

    void addLocation() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> locationJSON = new HashMap<>();
        locationJSON.put("kota",city.getText().toString());
        locationJSON.put("alamat",address.getText().toString());
        locationJSON.put("nama_gudang",warehouse.getText().toString());
        locationJSON.put("phone",phone.getText().toString());
        locationJSON.put("kode_distributor",code.getText().toString());
        locationJSON.put("principle",Utility.utility.getLoggedName(this));
        locationJSON.put("nama_pic",pic.getText().toString());
        Call<LocationCreation> locationResponseCall = api.createLocation(locationJSON);
        locationResponseCall.enqueue(new Callback<LocationCreation>() {
            @Override
            public void onResponse(Call<LocationCreation> call, Response<LocationCreation> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LocationCreation> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }
}
