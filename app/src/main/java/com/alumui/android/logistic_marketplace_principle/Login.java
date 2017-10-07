package com.alumui.android.logistic_marketplace_principle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alumui.android.logistic_marketplace_principle.Model.LoginPrinciple.LoginPrinciple;
import com.alumui.android.logistic_marketplace_principle.Model.MyCookieJar;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    String user, pw;
    MyCookieJar cookieJar;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cookieJar = new MyCookieJar();

    }
    public void clicklogin(View v) {

        EditText username= (EditText)findViewById(R.id.my_profile_username);
        user= username.getText().toString();
        EditText password= (EditText)findViewById(R.id.password);
        pw= password.getText().toString();

        if(user.isEmpty()||pw.isEmpty()){
            Context c = getApplicationContext();
            Toast.makeText(c,"Username of password is invalid", Toast.LENGTH_SHORT).show();
        }
        else {
            doLogin();
        }
    }

    public void doLogin() {

        //create client to get cookies from OkHttp
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        cookieJar = new MyCookieJar();
        okHttpClient.cookieJar(cookieJar);
        OkHttpClient client = okHttpClient.build();

        //add cookie jar intercept to retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        API api = retrofit.create(API.class);
        final Activity thisActivity = this;
        Call<LoginPrinciple> login = api.loginUser(user,pw,"mobile");
        login.enqueue(new Callback<LoginPrinciple>() {
            @Override
            public void onResponse(Call<LoginPrinciple> call, Response<LoginPrinciple> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    LoginPrinciple userData = response.body();
                    Utility.utility.saveLoggedName(userData.name, thisActivity);
                    Gson gson = new Gson();
                    String json = gson.toJson(cookieJar);
                    Utility.utility.saveCookieJarToPreference(cookieJar, thisActivity);

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginPrinciple> call, Throwable throwable) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), throwable);
            }
        });
    }
}
