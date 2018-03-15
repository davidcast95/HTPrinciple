package huang.android.logistic_principle.RequestAService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.Profil.Profil;
import huang.android.logistic_principle.Model.Profil.ProfilResponse;
import huang.android.logistic_principle.Model.Vendor.VendorData;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View listVendor = null;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listVendor = inflater.inflate(R.layout.list_vendor, viewGroup, false);
        profilPicture = (ImageView) listVendor.findViewById(R.id.list_vendor_profil_picture);
        nameTextView = (TextView) listVendor.findViewById(R.id.list_vendor_name);
        Utility.utility.setFont(nameTextView, Hind.BOLD,mContext);
        addressTextView = (TextView) listVendor.findViewById(R.id.list_vendor_address);
        Utility.utility.setFont(nameTextView, Hind.REGULAR,mContext);

        nameTextView.setText(vendors.get(i).name);
        addressTextView.setText(vendors.get(i).address);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(mContext);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<ProfilResponse> profilResponseCall = api.getVendor(vendors.get(i).name);
        profilResponseCall.enqueue(new Callback<ProfilResponse>() {
            @Override
            public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                if (Utility.utility.catchResponse(mContext, response,"")) {
                    ProfilResponse profilResponse = response.body();
                    if (profilResponse != null) {
                        List<Profil> profils = profilResponse.data;
                        if (profils.size() > 0) {
                            Profil profil = profils.get(0);

                            if (profil.profile_image.size() > 0) {
                                VendorData vendorData = vendors.get(i);
                                vendorData.profile_image = profil.profile_image.get(0);
                                vendors.set(i, vendorData);
                                String imageUrl = profil.profile_image.get(0);
                                MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(mContext);
                                API api = Utility.utility.getAPIWithCookie(cookieJar);
                                Call<ResponseBody> callImage = api.getImage(imageUrl);
                                callImage.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            ResponseBody responseBody = response.body();
                                            if (responseBody != null) {
                                                Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                                                profilPicture.setImageBitmap(bm);
                                                profilPicture.setBackgroundColor(mContext.getResources().getColor(R.color.White));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfilResponse> call, Throwable t) {
                Log.e("asd",t.getLocalizedMessage());
            }
        });


        return listVendor;
    }
}
