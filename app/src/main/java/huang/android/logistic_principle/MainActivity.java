package huang.android.logistic_principle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import huang.android.logistic_principle.Bantuan.HelpAndSupport;
import huang.android.logistic_principle.Home.Home;
import huang.android.logistic_principle.Lihat_Profil.MyProfile;
import huang.android.logistic_principle.Pengaturan.Settings;
import huang.android.logistic_principle.JobOrder.ViewJobOrder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private BroadcastReceiver broadcastReceiver;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(huang.android.logistic_principle.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(huang.android.logistic_principle.R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(huang.android.logistic_principle.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, huang.android.logistic_principle.R.string.navigation_drawer_open, huang.android.logistic_principle.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(huang.android.logistic_principle.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNav();

        Fragment fragment = new Home();
        FragmentManager manager2=getSupportFragmentManager();
        manager2.beginTransaction().replace(huang.android.logistic_principle.R.id.contentLayout,
                fragment,fragment.getTag()).commit();
        setTitle(huang.android.logistic_principle.R.string.home);




        //tokenize Firebase
        final String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.e("Token",token);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("Token",token);
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(token));

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        String principle = huang.android.logistic_principle.Utility.utility.getLoggedName(this).replace(" ","_");
        FirebaseMessaging.getInstance().subscribeToTopic(principle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(huang.android.logistic_principle.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(huang.android.logistic_principle.R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == huang.android.logistic_principle.R.id.action_yes) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch (id){
            case huang.android.logistic_principle.R.id.nav_home:
                fragment = new Home();
                setTitle(huang.android.logistic_principle.R.string.home);
                break;
            case huang.android.logistic_principle.R.id.nav_job_orders:
                fragment = new ViewJobOrder();
                setTitle(huang.android.logistic_principle.R.string.job_order);
                break;
            case huang.android.logistic_principle.R.id.nav_profile:
                fragment = new MyProfile();
                setTitle(huang.android.logistic_principle.R.string.my_profile);
                break;
            case huang.android.logistic_principle.R.id.nav_login:
                fragment = new LoginFragment();
                setTitle(huang.android.logistic_principle.R.string.login);
                break;
            case huang.android.logistic_principle.R.id.nav_settings:
                fragment = new Settings();
                setTitle(huang.android.logistic_principle.R.string.settings);
                break;
            case huang.android.logistic_principle.R.id.nav_help:
                fragment = new HelpAndSupport();
                setTitle(huang.android.logistic_principle.R.string.hns);
                break;
            case huang.android.logistic_principle.R.id.nav_logout:
                SharedPreferences mPrefs;
                mPrefs = getSharedPreferences("myprefs",MODE_PRIVATE);
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putString("cookieJar", "null");
                ed.commit();
                Intent mainIntent = new Intent(MainActivity.this,SplashScreen.class);
                startActivity(mainIntent);
                finish();
                break;
        }
        if(fragment != null){
            FragmentManager manager2=getSupportFragmentManager();
            manager2.beginTransaction().replace(huang.android.logistic_principle.R.id.contentLayout,
                    fragment,fragment.getTag()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(huang.android.logistic_principle.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;

    }

    public void setFragmentOnNav(int index) {
        navigationView.getMenu().getItem(index).setChecked(true);
    }

    public void updateNav() {
        Menu menu = navigationView.getMenu();
        MenuItem nav_profile = menu.findItem(huang.android.logistic_principle.R.id.nav_profile),
                nav_login = menu.findItem(huang.android.logistic_principle.R.id.nav_login);

        SharedPreferences cookie = getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        String cookieJar = cookie.getString("cookieJar", "null");

        if(cookieJar.equals("null")){
            nav_login.setVisible(true);
            nav_profile.setVisible(false);
        }
        else {
            nav_login.setVisible(false);
            nav_profile.setVisible(true);
        }
    }

}
