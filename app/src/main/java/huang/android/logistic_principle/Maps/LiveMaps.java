package huang.android.logistic_principle.Maps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import huang.android.logistic_principle.Model.Driver.DriverBackgroundUpdateData;
import huang.android.logistic_principle.Model.Driver.DriverBackgroundUpdateResponse;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveMaps extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String from2;
    String to2;
    String driver = "";
    boolean isLoading = true;
    RelativeLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle(R.string.title_live_maps);
        Intent intent = getIntent();
        driver = intent.getStringExtra("driver");

        loading = (RelativeLayout) findViewById(R.id.loading);
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        }


    }

    private void sendRequest() {
        String origin = from2;
        String destination = to2;

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        getBackgroundUpdate(mMap);
                    }
                }, 0, 30, TimeUnit.SECONDS);
    }

    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.order_location_a))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.order_location_b))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(230,120,23)).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateMarker(GoogleMap googleMap, Double lat, Double longi) {
        mMap = googleMap;
        LatLng lokasi = new LatLng(lat,longi);
//        LatLng lokasi = new LatLng(0,0);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(lokasi).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi,18));
    }

    //API
    void getBackgroundUpdate(final GoogleMap googleMap) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String filters = "[[\"Driver Background Update\",\"driver\",\"=\",\"" + driver + "\"]]";
        Call<DriverBackgroundUpdateResponse> callbg = api.getBackgroundUpdate(filters);
        callbg.enqueue(new Callback<DriverBackgroundUpdateResponse>() {
            @Override
            public void onResponse(Call<DriverBackgroundUpdateResponse> call, Response<DriverBackgroundUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    isLoading = false;
                    if (!isLoading) {
                        loading.setVisibility(View.GONE);
                    }
                    List<DriverBackgroundUpdateData> bgData = response.body().data;
                    if (bgData.size() > 0) {
                        updateMarker(googleMap, Double.valueOf(bgData.get(0).lat),Double.valueOf(bgData.get(0).lo));
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.warning_no_location_available), Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<DriverBackgroundUpdateResponse> call, Throwable t) {

            }
        });
    }

}



/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera. In this case,
 * we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to install
 * it inside the SupportMapFragment. This method will only be triggered once the user has
 * installed Google Play services and returned to the app.
 */
