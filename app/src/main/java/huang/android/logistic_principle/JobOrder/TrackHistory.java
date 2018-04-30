package huang.android.logistic_principle.JobOrder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import huang.android.logistic_principle.JobOrder.Base.DetailOrder;
import huang.android.logistic_principle.JobOrder.OnProgress.DetailOnProgressOrder;
import huang.android.logistic_principle.Maps.DirectionFinder;
import huang.android.logistic_principle.Maps.DirectionFinderListener;
import huang.android.logistic_principle.Maps.LiveMaps;
import huang.android.logistic_principle.Maps.Route;
import huang.android.logistic_principle.Model.Driver.DriverBackgroundUpdateData;
import huang.android.logistic_principle.Model.Driver.DriverBackgroundUpdateResponse;
import huang.android.logistic_principle.Model.Driver.RouteData;
import huang.android.logistic_principle.Model.Driver.RouteResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackHistory extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Marker> markers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    ListView lv;
    TrackHistoryAdapter trackHistoryAdapter;
    ProgressBar loading;
    LinearLayout layout;
    TextView acceptDateTV;
    List<RouteData> lastUpdateDriverRoute = null;

    ImageView currentLocation, pickUpOrigin, dropOrigin, pickUpDestination, dropDestination;

    public static TrackHistory instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        TrackHistory.instance = this;
        setTitle(R.string.tracking_history);
        setContentView(R.layout.activity_track_history);
        getDriverPosition();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int locationProvide = 0;

        if (DetailOrder.jobOrderUpdates != null) {
            for (int i = 0; i < DetailOrder.jobOrderUpdates.size(); i++) {
                if (DetailOrder.jobOrderUpdates.get(i).longitude != null && DetailOrder.jobOrderUpdates.get(i).latitude != null)
                    if (!DetailOrder.jobOrderUpdates.get(i).longitude.equals("0.0") && !DetailOrder.jobOrderUpdates.get(i).equals("0.0"))
                        locationProvide++;

            }
        } else {
            DetailOrder.jobOrderUpdates = new ArrayList<>();
        }



//        mapFragment.getView().setVisibility(View.GONE);

        currentLocation = (ImageView)findViewById(R.id.current_location);
        currentLocation.setVisibility(View.GONE);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusOnDriver();
            }
        });

        if (locationProvide > 0) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            currentLocation.setVisibility(View.VISIBLE);
        }
        else {
            mapFragment.getView().setVisibility(View.GONE);
            currentLocation.setVisibility(View.GONE);
        }

        if (DetailOrder.jobOrder.status.equals(JobOrderStatus.DONE)) {
            currentLocation.setVisibility(View.GONE);
        }

        String from,to,ref,vendor,to_type,from_type;
        Intent intent = getIntent();
        from = intent.getStringExtra("origin");
        from_type = intent.getStringExtra("origin_type");
        to = intent.getStringExtra("destination");
        to_type = intent.getStringExtra("destination_type");
        vendor = intent.getStringExtra("vendor");
        ref = intent.getStringExtra("ref");

        TextView _vendor = (TextView)findViewById(R.id.vendor);
        _vendor.setText(vendor);
        TextView refid = (TextView)findViewById(R.id.ref_id);
        refid.setText(ref);
        TextView joid = (TextView)findViewById(R.id.joid);
        Utility.utility.setTextView(joid,DetailOrder.jobOrder.joid);
        TextView tanggal = (TextView)findViewById(R.id.tanggal1);
        Utility.utility.setTextView(tanggal,DetailOrder.jobOrder.etd);
        TextView locationfrom = (TextView)findViewById(R.id.origin);
        Utility.utility.setTextView(locationfrom, from);
        TextView locationto = (TextView)findViewById(R.id.destination);
        Utility.utility.setTextView(locationto,to);
        acceptDateTV = (TextView)findViewById(R.id.accept_date);
        acceptDateTV.setVisibility(View.GONE);

        if (DetailOnProgressOrder.jobOrder.acceptDate != "") {
            acceptDateTV.setVisibility(View.VISIBLE);
            acceptDateTV.setText(getString(R.string.confirm_vendor) + " " + Utility.formatDateFromstring(Utility.dateDBShortFormat,Utility.LONG_DATE_FORMAT, DetailOrder.jobOrder.acceptDate));
        }

        pickUpOrigin = (ImageView)findViewById(R.id.pickup_origin_icon);
        dropOrigin = (ImageView)findViewById(R.id.drop_origin_icon);
        pickUpDestination = (ImageView)findViewById(R.id.pickup_destination_icon);
        dropDestination = (ImageView)findViewById(R.id.drop_destination_icon);
        if (from_type.equals("Pick Up")) {
            pickUpOrigin.setVisibility(View.VISIBLE);
        } else {
            dropOrigin.setVisibility(View.VISIBLE);
        }
        if (to_type.equals("Pick Up")) {
            pickUpDestination.setVisibility(View.VISIBLE);
        } else {
            dropDestination.setVisibility(View.VISIBLE);
        }

        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);
        loading.setVisibility(View.GONE);

        TextView noUpdateLocaiton = (TextView)findViewById(R.id.no_update_location);
        if (DetailOrder.jobOrderUpdates.size() == 0) {
            noUpdateLocaiton.setVisibility(View.VISIBLE);
        } else {
            noUpdateLocaiton.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra("from").equals("actived")) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.activity_track_history, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                Intent intent = new Intent(this, LiveMaps.class);
                intent.putExtra("driver",getIntent().getStringExtra("driver"));
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                TrackHistory.instance = null;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawJOUpdateMarker();

        trackHistoryAdapter = new TrackHistoryAdapter(getApplicationContext(),R.layout.activity_track_history_list, DetailOrder.jobOrderUpdates, mMap, markers);
        lv.setAdapter(trackHistoryAdapter);
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    void drawJOUpdateMarker() {
        boolean isPinned = false;
        double minLat = -1, maxLat = -1, minLong = -1, maxLong = -1;
        LatLng lastLocation = null;
        if (DetailOrder.jobOrderUpdates != null) {
            for (int i = 0; i < DetailOrder.jobOrderUpdates.size(); i++) {
                if (DetailOrder.jobOrderUpdates.get(i).longitude == null || DetailOrder.jobOrderUpdates.get(i).latitude == null) {
                    markers.add(null);
                } else {
                    if (DetailOrder.jobOrderUpdates.get(i).latitude.equals("0.0") || DetailOrder.jobOrderUpdates.get(i).equals("0.0")) {
                        markers.add(null);
                    } else {
                        Double lat = Double.valueOf(DetailOrder.jobOrderUpdates.get(i).latitude), longi = Double.valueOf(DetailOrder.jobOrderUpdates.get(i).longitude);
                        LatLng currentLocation = new LatLng(lat, longi);

                        if (lastLocation == null) lastLocation = currentLocation;

                        int icon = R.drawable.loc;
                        String statusIndex = DetailOrder.jobOrderUpdates.get(i).status.substring(0, 1);
                        switch (statusIndex) {
                            case "1":
                                icon = R.drawable.loc_1;
                                break;
                            case "2":
                                icon = R.drawable.loc_2;
                                break;
                            case "3":
                                icon = R.drawable.loc_3;
                                break;
                            case "4":
                                icon = R.drawable.loc_4;
                                break;
                            case "5":
                                icon = R.drawable.loc_5;
                                break;
                            case "6":
                                icon = R.drawable.loc_6;
                                break;
                        }

                        MarkerOptions marker = new MarkerOptions()
                                .position(currentLocation)
                                .title(DetailOrder.jobOrderUpdates.get(i).status)
                                .snippet(getString(R.string.last_update_on) + " " + Utility.formatDateFromstring(Utility.dateDBLongFormat, Utility.LONG_DATE_TIME_FORMAT, DetailOrder.jobOrderUpdates.get(i).time))
                                .icon(BitmapDescriptorFactory.fromResource(icon));
                        markers.add(mMap.addMarker(marker));
                        isPinned = true;
                        if (i == 0) {
                            minLat = lat;
                            maxLat = lat;
                            minLong = longi;
                            maxLong = longi;
                        } else {
                            if (lat < minLat) minLat = lat;
                            else if (lat > maxLat) maxLat = lat;
                            if (longi < minLong) minLong = longi;
                            else if (longi > maxLong) maxLong = longi;
                        }

//                        if (i >= 1) {
//
////                            Double originLat = Double.valueOf(DetailOrder.jobOrderUpdates.get(i - 1).latitude), originLong = Double.valueOf(DetailOrder.jobOrderUpdates.get(i - 1).longitude);
////                            drawDirection(originLat, originLong, lat, longi);
//                        }
                    }

                }
            }
            if (DetailOrder.jobOrderUpdates.size() > 0) {
                int lastIndex = DetailOrder.jobOrderUpdates.size() - 1;
                drawDirection(DetailOrder.jobOrderUpdates.get(lastIndex).name, DetailOrder.jobOrderUpdates.get(0).name, DetailOrder.jobOrder.driver);
            }
        }
        if (!isPinned) {
            RelativeLayout mapHolder = (RelativeLayout)findViewById(R.id.mapholder);
            mapHolder.setVisibility(View.GONE);
        }

        //update driver mark road
        if (lastUpdateDriverRoute != null) {
            int lastIndex = lastUpdateDriverRoute.size() - 1;
            Double lat = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lat), longi = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lo);
            lastLocation = new LatLng(lat,longi);
            MarkerOptions marker = new MarkerOptions()
                    .position(lastLocation)
                    .title(getString(R.string.last_position))
                    .snippet(getString(R.string.last_update_on) + " " + Utility.formatDateFromstring(Utility.dateDBLongFormat,Utility.LONG_DATE_TIME_FORMAT,lastUpdateDriverRoute.get(lastIndex).creation))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_truck));
            markers.add(mMap.addMarker(marker));
//
//            if (lat < minLat) minLat = lat;
//            else if (lat > maxLat) maxLat = lat;
//            if (longi < minLong) minLong = longi;
//            else if (longi > maxLong) maxLong = longi;
//

            //DRAW LAST JOU TO DRIVER POSITION
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(88,114,47)).
                    width(10);
            for (int i=0;i<lastUpdateDriverRoute.size();i++) {
                longi = Double.valueOf(lastUpdateDriverRoute.get(i).lo);
                lat = Double.valueOf(lastUpdateDriverRoute.get(i).lat);

                polylineOptions.add(new LatLng(lat,longi));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        } else {
            if (DetailOrder.jobOrderUpdates.size() > 0 && lastLocation != null) {
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        lastLocation, mMap.getCameraPosition().zoom);
                mMap.animateCamera(location);
                int lastIndex = markers.size() - 1;
                Marker marker = markers.get(lastIndex);
                if (marker != null)
                    marker.showInfoWindow();
            }
        }


//        LatLng minLoc = new LatLng(minLat,minLong), maxLoc = new LatLng(maxLat, maxLong);
//        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(minLoc, maxLoc));
        mMap.setMinZoomPreference(5f);

        focusOnDriver();



    }

    void drawDirection(String startjou, String endjou, String driver) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<RouteResponse> callRoute = api.getRoute(startjou,endjou,driver);
        callRoute.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(),response,"")) {
                    RouteResponse routeResponse = response.body();
                    if (routeResponse != null) {
                        List<RouteData> routeList = routeResponse.data;
                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.rgb(88,114,47)).
                                width(10);
                        if (routeList != null) {
                            for (int i = 0; i < routeList.size(); i++) {
                                Double lo = Double.valueOf(routeList.get(i).lo);
                                Double lat = Double.valueOf(routeList.get(i).lat);

                                polylineOptions.add(new LatLng(lat, lo));
                            }

                            polylinePaths.add(mMap.addPolyline(polylineOptions));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {

            }
        });
    }


    void drawDirection(Double originLat, Double originLong, Double destinationLat, Double destinationLong) {
        DirectionFinder directionFinder = new DirectionFinder(new DirectionFinderListener() {
            @Override
            public void onDirectionFinderStart() {

            }

            @Override
            public void onDirectionFinderSuccess(List<Route> routes) {
                for (Route route : routes) {

                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.rgb(88,114,47)).
                            width(10);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    polylinePaths.add(mMap.addPolyline(polylineOptions));
                }
            }
        },originLat + "," + originLong,destinationLat + "," + destinationLong);
        try {
            directionFinder.execute();
        } catch (UnsupportedEncodingException err) {

        }
    }

    void focusOnDriver() {
        if (lastUpdateDriverRoute != null) {
            int lastIndex = lastUpdateDriverRoute.size() - 1;
            Double lat = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lat), longi = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lo);
            LatLng lastLocation = new LatLng(lat,longi);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    lastLocation, mMap.getCameraPosition().zoom);
            mMap.animateCamera(location);
            lastIndex = markers.size()-1;
            markers.get(lastIndex).showInfoWindow();
        }
    }

    public void refreshDriverPosition(String driver) {
        if (driver.equals(DetailOrder.jobOrder.driver)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDriverPosition();
                }
            }, 2000);
        }
    }

    //API
    void getDriverPosition() {
        if (DetailOrder.jobOrder.status.equals(JobOrderStatus.DONE) || DetailOrder.jobOrder.status.equals(JobOrderStatus.VENDOR_REJECT)) return;
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);

        if (DetailOrder.jobOrderUpdates.size() > 0) {
            Call<RouteResponse> callLastRoute = api.getLastRoute(DetailOrder.jobOrderUpdates.get(0).name, DetailOrder.jobOrder.driver);
            callLastRoute.enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                    if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                        RouteResponse routeResponse = response.body();
                        if (routeResponse != null) {
                            if (routeResponse.data.size() > 0) {
                                lastUpdateDriverRoute = routeResponse.data;
                                drawJOUpdateMarker();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {

                }
            });
        }
    }

}
