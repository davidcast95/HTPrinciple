package com.alumui.android.logistic_marketplace_principle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.alumui.android.logistic_marketplace_principle.Model.MyCookieJar;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.google.android.gms.common.api.Result;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;


public class Utility {
    public static Utility utility = new Utility();
    public static String dateDBShortFormat = "yyyy-MM-dd";
    public static String dateLongFormat = "EEE, d MMM yyyy";
    public void getLanguage(Activity activity){
        SharedPreferences prefs = activity.getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");

        if(language.contentEquals("English")){
            setLocal(activity, "en");
        }
        else {
            setLocal(activity, "in");
        }
    }
    private void setLocal(Activity activity, String language) {
        Locale myLocale;
        myLocale = new Locale(language);
        Resources res = activity.getResources();
        DisplayMetrics dm= res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf,dm);
    }
    public static String formatDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        }
        catch (ParseException e) {
            Log.e("DATE", "ParseException - dateFormat");
        }

        return outputDate;
    }

    public String dateToFormatDatabase(Date date) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(date);
    }


    public String getLoggedName(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        String name = preferences.getString("name","");
        return name;
    }

    public void saveLoggedName(String name, Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("name", name);
        ed.commit();
    }
    public MyCookieJar getCookieFromPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        String cookieJson = preferences.getString("cookieJar","");
        Gson gson = new Gson();
        MyCookieJar cookieJar = gson.fromJson(cookieJson, MyCookieJar.class);
        if (cookieJar == null) { cookieJar = new MyCookieJar(); }
        return cookieJar;
    }
    public void saveCookieJarToPreference(MyCookieJar cookieJar, Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(cookieJar);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("cookieJar", json);
        ed.commit();
    }


    public API getAPIWithCookie(MyCookieJar cookieJar) {
        //create client to get cookies from OkHttp
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.cookieJar(cookieJar);
        OkHttpClient client = okHttpClient
                .build();

        //add cookie jar intercept to retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(API.class);
    }

    public <T> boolean catchResponse(Context context, Response<T> response) {
        if (response.message().equals("OK")) {
            return true;
        }
        else if (response.message().equals("UNAUTHORIZED")) {
            Toast.makeText(context,"Invalid username or password",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (response.message().equals("INTERNAL SERVER ERROR")) {
            Toast.makeText(context,"Server is unreachable",Toast.LENGTH_SHORT).show();
            return false;
        } else if (response.message().equals("FORBIDDEN")) {
            Toast.makeText(context,"Your session is expired. Please renew it by re-login",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return false;
        }
    }
    public void showConnectivityUnstable(Context context) {
        Toast.makeText(context,"Connectivity unstable",Toast.LENGTH_SHORT).show();
    }

    public void showConnectivityWithError(Context context, Throwable t) {
        Toast.makeText(context,"Connectivity unstable",Toast.LENGTH_SHORT).show();
    }
}