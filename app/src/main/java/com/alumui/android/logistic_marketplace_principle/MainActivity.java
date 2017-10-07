package com.alumui.android.logistic_marketplace_principle;

import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;

import com.alumui.android.logistic_marketplace_principle.Bantuan.HelpAndSupport;
import com.alumui.android.logistic_marketplace_principle.Home.Home;
import com.alumui.android.logistic_marketplace_principle.Lihat_Profil.MyProfile;
import com.alumui.android.logistic_marketplace_principle.Pengaturan.Settings;
import com.alumui.android.logistic_marketplace_principle.JobOrder.ViewJobOrder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNav();

        Fragment fragment = new Home();
        FragmentManager manager2=getSupportFragmentManager();
        manager2.beginTransaction().replace(R.id.contentLayout,
                fragment,fragment.getTag()).commit();
        setTitle(R.string.home);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch (id){
            case R.id.nav_home:
                fragment = new Home();
                setTitle(R.string.home);
                break;
            case R.id.nav_job_orders:
                fragment = new ViewJobOrder();
                setTitle(R.string.job_order);
                break;
            case R.id.nav_profile:
                fragment = new MyProfile();
                setTitle(R.string.my_profile);
                break;
            case R.id.nav_login:
                fragment = new LoginFragment();
                setTitle(R.string.login);
                break;
            case R.id.nav_settings:
                fragment = new Settings();
                setTitle(R.string.settings);
                break;
            case R.id.nav_help:
                fragment = new HelpAndSupport();
                setTitle(R.string.hns);
                break;
            case R.id.nav_logout:
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
            manager2.beginTransaction().replace(R.id.contentLayout,
                    fragment,fragment.getTag()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    public void updateNav() {
        Menu menu = navigationView.getMenu();
        MenuItem nav_profile = menu.findItem(R.id.nav_profile),
                nav_login = menu.findItem(R.id.nav_login);

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
