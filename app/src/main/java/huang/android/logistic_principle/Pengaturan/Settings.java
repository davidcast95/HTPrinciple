package huang.android.logistic_principle.Pengaturan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import huang.android.logistic_principle.Base.SpinnerStringAdapter;
import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.SplashScreen;
import huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Settings extends Fragment {
    String bahasa;
    public Settings() {
        // Required empty public constructor
    }
    private Spinner spinner;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getLanguage();
        setHasOptionsMenu(true);

        v = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView languageText = (TextView)v.findViewById(R.id.language_text);
        Utility.utility.setFont(languageText,Hind.LIGHT,getContext());

        List<String> categories = new ArrayList<String>();
        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","Bahasa Indonesia");
        if(language.contentEquals("English")){
            categories.add("Bahasa Indonesia");
        }
        else {
            categories.add("Bahasa Indonesia");
            categories.add("English");
        }

        spinner = (Spinner)v.findViewById(R.id.spinner);
        SpinnerStringAdapter dataAdapter = new SpinnerStringAdapter(this.getActivity(), categories);
        spinner.setAdapter(dataAdapter);

        Button save = (Button)v.findViewById(R.id.save);
        Utility.utility.setFont(save, Hind.BOLD,getContext());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bahasa = spinner.getSelectedItem().toString();
                savelanguage();
            }
        });

        return v;
    }

//    @Override
//    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.settings_titlebar, menu);
//        super.onCreateOptionsMenu(menu,inflater);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.id.action_settings:
//                bahasa = spinner.getSelectedItem().toString();
//                savelanguage();
//                return true;
//        }
//
//        return false;
//    }

    public void savelanguage(){
        SharedPreferences prefs1 = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs1.edit();
        editor.putString("language", bahasa);
        editor.commit();
//        Toast.makeText(getActivity().getApplicationContext(),bahasa, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity().getApplicationContext(),SplashScreen.class));
        getActivity().finish();
    }

    public void getLanguage(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","Bahasa Indonesia");

        if(language.contentEquals("English")){
            setLocal("en");
        }
        else {
            setLocal("id");
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
