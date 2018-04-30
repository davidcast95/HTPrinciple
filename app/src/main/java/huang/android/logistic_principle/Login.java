package huang.android.logistic_principle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import huang.android.logistic_principle.Model.FirebaseID.FirebaseIDData;
import huang.android.logistic_principle.Model.FirebaseID.FirebaseIDResponse;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginPrinciple;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginUserPermission;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginUserPermissionResponse;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.ServiceAPI.API;
import com.google.gson.Gson;

import java.util.List;

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
    RelativeLayout loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(huang.android.logistic_principle.R.layout.activity_login);
        cookieJar = new MyCookieJar();
        loadingView = (RelativeLayout)findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);
    }
    public void clicklogin(View v) {
        EditText username= (EditText)findViewById(huang.android.logistic_principle.R.id.my_profile_username);
        user= username.getText().toString();
        EditText password= (EditText)findViewById(huang.android.logistic_principle.R.id.password);
        pw= password.getText().toString();

        if(user.isEmpty()||pw.isEmpty()){
            Context c = getApplicationContext();
            Toast.makeText(c,"Username of password is invalid", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingView.setVisibility(View.VISIBLE);
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
                if (response.code() == 200) {
                    checkPermission(cookieJar);
                } else {
                    loadingView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginPrinciple> call, Throwable throwable) {
                loadingView.setVisibility(View.GONE);
                Utility.utility.showConnectivityWithError(getApplicationContext(), throwable);
            }
        });
    }

    void checkPermission(final MyCookieJar cookieJar) {
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<FirebaseIDResponse> loginUserPermissionResponseCall = api.getFirebaseID("Principle");
        final Activity activity = this;
        loginUserPermissionResponseCall.enqueue(new Callback<FirebaseIDResponse>() {
            @Override
            public void onResponse(Call<FirebaseIDResponse> call, Response<FirebaseIDResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    FirebaseIDResponse firebaseIDResponse = response.body();
                    if (firebaseIDResponse != null) {
                        if (firebaseIDResponse.message.role.equals("valid")) {
                            List<FirebaseIDData> data = firebaseIDResponse.message.for_value;
                            if (data.size() > 0) {
                                Utility.utility.saveLoggedName(data.get(0).user, activity);
                                Utility.utility.saveUsername(user,activity);
                                Utility.utility.saveCookieJarToPreference(cookieJar, activity);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                loadingView.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingView.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<FirebaseIDResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
                loadingView.setVisibility(View.GONE);
            }
        });
    }

}
