package huang.android.logistic_principle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(huang.android.logistic_principle.R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                SharedPreferences cookie = getSharedPreferences("myprefs", Context.MODE_PRIVATE);
                String cookieJar = cookie.getString("cookieJar", "null");

                if(cookieJar.equals("null")){
                    Intent mainIntent = new Intent(SplashScreen.this,Login.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, 1000);
    }
}
