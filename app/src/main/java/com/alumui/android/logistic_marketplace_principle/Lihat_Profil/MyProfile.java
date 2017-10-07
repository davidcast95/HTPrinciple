package com.alumui.android.logistic_marketplace_principle.Lihat_Profil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alumui.android.logistic_marketplace_principle.Model.Profil.ProfilData;
import com.alumui.android.logistic_marketplace_principle.Model.Profil.ProfilResponse;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.alumui.android.logistic_marketplace_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyProfile extends Fragment {

    String genders,customer_group,customer_name,names;

    public MyProfile() {
        // Required empty public constructor
    }

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        return v;
    }

    //API
    public void getProfile() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<ProfilResponse> callJO = api.getProfile("PT. UNILEVER INDONESIA");
        callJO.enqueue(new Callback<ProfilResponse>() {
            @Override
            public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                ProfilResponse profilResponse = response.body();
                List<ProfilData> profils = profilResponse.message.data;
                for (int i = 0;i<profils.size();i++) {
                    genders=profils.get(i).gender;
                    customer_group=profils.get(i).customer_group;
                    customer_name=profils.get(i).customer_name;
                    names=profils.get(i).name;
                }
                TextView username = (TextView)v.findViewById(R.id.my_profile_username);
                TextView name = (TextView)v.findViewById(R.id.name);
                username.setText(customer_name);
                name.setText(names);

            }

            @Override
            public void onFailure(Call<ProfilResponse> call, Throwable throwable) {
                String error = throwable.getLocalizedMessage();
                Log.e("err", error);
            }
        });
    }

}
