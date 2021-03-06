package huang.android.logistic_principle.RequestAService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.Vendor.VendorData;
import huang.android.logistic_principle.Model.Vendor.VendorResponse;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseVendor extends AppCompatActivity {

    ListView listView;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.utility.getLanguage(this);
        setContentView(R.layout.activity_choose_vendor);

        fetchVendorList();

        listView = (ListView)findViewById(R.id.choose_vendor_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vendorListClicked((VendorData)listView.getAdapter().getItem(i));
            }
        });

        setTitle(R.string.choose_vendor);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Actions
    void vendorListClicked(VendorData vendor) {
        Intent intent = new Intent();
        intent.putExtra("name", vendor.name);
        intent.putExtra("address", vendor.address);
        intent.putExtra("profile_image",vendor.profile_image);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    //API Connectivity
    void fetchVendorList() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<VendorResponse> callVendor = api.getVendorList();
        callVendor.enqueue(new Callback<VendorResponse>() {
            @Override
            public void onResponse(Call<VendorResponse> call, Response<VendorResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    VendorResponse vendorResponse = response.body();
                    List<VendorData> vendors = vendorResponse.vendors;
                    ChooseVendorAdapter adapter = new ChooseVendorAdapter(ChooseVendor.this, vendors);
                    listView.setAdapter(adapter);

                }

            }

            @Override
            public void onFailure(Call<VendorResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }
}
