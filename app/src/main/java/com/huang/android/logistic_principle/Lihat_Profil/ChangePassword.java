package com.huang.android.logistic_principle.Lihat_Profil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.huang.android.logistic_principle.R;

import java.util.Locale;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.changepassword_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_yes:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
    public void getLanguage(){
        SharedPreferences prefs = this.getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");

        if(language.contentEquals("English")){
            setLocal("en");
        }
        else {
            setLocal("in");
        }
    }
    private void setLocal(String language) {
        Locale myLocale;
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm= res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf,dm);
    }
}
