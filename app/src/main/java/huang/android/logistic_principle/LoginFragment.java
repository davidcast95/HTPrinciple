package huang.android.logistic_principle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import huang.android.logistic_principle.Lihat_Profil.MyProfile;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginPrinciple;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.ServiceAPI.API;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by davidwibisono on 8/23/17.
 */

public class LoginFragment extends Fragment {

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    MyCookieJar cookieJar;
    SharedPreferences mPrefs;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        v = inflater.inflate(huang.android.logistic_principle.R.layout.fragment_login, container, false);

        usernameEditText = (EditText)v.findViewById(huang.android.logistic_principle.R.id.login_username);
        passwordEditText = (EditText)v.findViewById(huang.android.logistic_principle.R.id.login_password);
        loginButton = (Button)v.findViewById(huang.android.logistic_principle.R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButtonClicked();
            }
        });

        return v;


    }

    //Actions
    void loginButtonClicked() {
        String username = usernameEditText.getText().toString(), password = passwordEditText.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(),"Username of password cannot be empty",Toast.LENGTH_SHORT).show();
        } else {
            doLogin(username, password);
        }

    }

    //API Connectivity
    void doLogin(String usr, String pw) {
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
        Call<LoginPrinciple> login = api.loginUser(usr,pw,"mobile");

        final Activity thisActivity = getActivity();
        final FragmentManager fragmentManager = this.getFragmentManager();

        login.enqueue(new Callback<LoginPrinciple>() {
            @Override
            public void onResponse(Call<LoginPrinciple> call, Response<LoginPrinciple> response) {

                if(Utility.utility.catchResponse(getActivity().getApplicationContext(), response)){
                    Utility.utility.saveCookieJarToPreference(cookieJar, getActivity());

                    //change fragment to profile
                    MyProfile profileFragment = new MyProfile();
                    if(profileFragment != null){
                        fragmentManager.beginTransaction().replace(huang.android.logistic_principle.R.id.contentLayout,
                                profileFragment,profileFragment.getTag()).commit();
                    }
                }

            }

            @Override
            public void onFailure(Call<LoginPrinciple> call, Throwable throwable) {
                Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), throwable);
            }
        });
    }
}
