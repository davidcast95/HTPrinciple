package com.huang.android.logistic_principle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic_principle.Model.Location.Location;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.ServiceAPI.API;
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

    public static String dateDBLongFormat = "yyyy-MM-dd HH:mm:ss";

    public static String LONG_DATE_TIME_FORMAT = "dd MMMM yyyy HH:mm:ss";
    public static String LONG_DATE_FORMAT = "dd MMMM yyyy";

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
        if (response.code() == 200) {
            Log.e("DATA UPLOADED","OK");
            return true;
        }
        else if (response.code() == 401) {
            if (context == null) return false;
            Toast.makeText(context,"Invalid username or password",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (response.code() == 500 || response.code() == 417) {
            if (context == null) return false;
            Toast.makeText(context,"Server is unreachable",Toast.LENGTH_SHORT).show();
            return false;
        } else if (response.message().equals("Forbidden")) {

            if (context == null) return false;
            Toast.makeText(context,"Your session is expired. Please renew it by re-login",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.e("FALSE","");
            return false;
        }
    }
    public void showConnectivityUnstable(Context context) {
        if (context == null) return;
        Toast.makeText(context,"Connectivity unstable",Toast.LENGTH_SHORT).show();
    }

    public void showConnectivityWithError(Context context, Throwable t) {
        if (context == null) return;
        Toast.makeText(context,"Connectivity unstable",Toast.LENGTH_SHORT).show();
    }

    public void setDialContactPhone(View trigger, final String phoneNumber,final Activity activity) {
        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            }
        });
    }

    public String formatLocation(Location location) {
        return location.warehouse + " ("+ location.code +") - " + location.address + ", " + location.city;
    }

    public void setTextView(TextView view, String text) {
        if (view != null && text != null) {
            view.setText(text);
        }
    }
    public void setEditText(EditText view, String text) {
        if (view != null && text != null) {
            view.setText(text);
        }
    }
}
