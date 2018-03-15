package huang.android.logistic_principle.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paging.listview.PagingListView;

import huang.android.logistic_principle.Model.Location.Location;
import huang.android.logistic_principle.Model.Location.LocationResponse;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.SearchActivity;
import huang.android.logistic_principle.SearchAndAddActivity;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class SearchLocation extends SearchAndAddActivity implements PagingListView.Pagingable {
    ProgressBar loading;
    PagingListView lv;
    TextView nodata;

    String mode = "origin";
    LocationAdapter locationAdapter;
    List<Location> locations = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;
    String lastKeyword = "";

    int pager = 0, limit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        setTitle("Search Location");

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        lv = (PagingListView) findViewById(R.id.layout);
        nodata = (TextView)findViewById(R.id.nodata);

        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mode = getIntent().getStringExtra("mode");

        locationAdapter = new LocationAdapter(getApplicationContext(),R.layout.location_list, locations);
        lv.setAdapter(locationAdapter);
        lv.setHasMoreItems(false);
        lv.setPagingableListener(this);

        getLocation(lastKeyword);
    }

    @Override
    protected void addAction() {
        Intent intent = new Intent(SearchLocation.this, AddLocation.class);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getLocation(lastKeyword);
        }
    }

    void refreshItems() {
        locationAdapter.clear();
        pager=0;
        getLocation(lastKeyword);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getLocation(String keyword) {
        if (pager == 0) {
            loading.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
        }
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this);
        Call<LocationResponse> locationResponseCall = api.getLocation("[[\"Location\",\"principle\",\"=\",\""+principle+"\"],[\"Location\",\"nama_gudang\",\"like\",\"%" + keyword + "%\"]]",
                                                                        "" + (pager++ * limit));
        locationResponseCall.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                loading.setVisibility(View.GONE);
                if (Utility.utility.catchResponse(getApplicationContext(), response,"")) {
                    if (response.body().locations.size() == 0) {
                        lv.onFinishLoading(false,null);
                    } else {
                        locationAdapter.addAll(response.body().locations);
                        lv.onFinishLoading(true,response.body().locations);
                        lv.setVisibility(View.VISIBLE);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (mode.equals("stop")) {
                                    AddStopLocation.location = locationAdapter.getItem(i);
                                    setResult(RESULT_OK);
                                    finish();
                                } else if (mode.equals("edit_stop")) {
                                    EditStopLocation.jobOrderRouteData.loc = locationAdapter.getItem(i);
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }
                        });
                    }
                    if (locationAdapter.isEmpty()) {
                        nodata.setVisibility(View.VISIBLE);
                    } else {
                        nodata.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }



    @Override
    protected void searchQuery(String keyword) {
        pager=0;
        locationAdapter.clear();
        getLocation(keyword);
        lastKeyword = keyword;
    }

    @Override
    public void onLoadMoreItems() {
        getLocation(lastKeyword);
    }
}
