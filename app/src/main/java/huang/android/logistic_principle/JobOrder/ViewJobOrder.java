package huang.android.logistic_principle.JobOrder;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import huang.android.logistic_principle.JobOrder.Done.DoneOrder;
import huang.android.logistic_principle.JobOrder.OnProgress.OnProgressOrder;
import huang.android.logistic_principle.JobOrder.Pending.PendingOrder;
import huang.android.logistic_principle.Model.JobOrder.JobOrderResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewJobOrder extends Fragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    ViewPager viewPager;
    TabHost tabHost;
    View v;

    private MenuItem mSearchItem;
    private SearchView sv;
    int pending = 0,onprogress = 0,done = 0;

    public OnProgressOrder progressOrder = new OnProgressOrder();
    public PendingOrder pendingOrder = new PendingOrder();
    public DoneOrder doneOrder = new DoneOrder();

    public ViewJobOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_view_order, container, false);

        initViewPager();
        initTabHost();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getCount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu); // Put your search menu in "menu_search" menu file.
        mSearchItem = menu.findItem(R.id.search);
        sv = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        sv.setIconified(true);

        SearchManager searchManager = (SearchManager)  getActivity().getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.e("search",query);
                int selectedItem = tabHost.getCurrentTab();
                if (selectedItem == 0) {
                    progressOrder.searchJobOrder(query);
                } else if (selectedItem == 1) {
                    pendingOrder.searchJobOrder(query);
                } else {
                    doneOrder.searchJobOrder(query);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.search:
                getActivity().onSearchRequested();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initTabHost() {
        tabHost=(TabHost)v.findViewById(R.id.tabhost);
        tabHost.setup();

        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");
        String[] tabs = new String[3];
        if(language.contentEquals("English")){
            tabs[0]="Pending";
            tabs[1]="On Progress";
            tabs[2]="Done";
        }
        else {
            tabs[0]="Pending";
            tabs[1]="Dalam Proses";
            tabs[2]="Selesai";
        }
        for(int i=0;i<3;i++){
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabs[i]);
            tabSpec.setIndicator(tabs[i]);
            tabSpec.setContent(new FakeContent(getActivity().getApplicationContext()));
            tabHost.addTab(tabSpec);
        }

        tabHost.setOnTabChangedListener(this);
    }

    public class FakeContent implements TabHost.TabContentFactory{
        Context context;
        public FakeContent(Context mcontext){
            context=mcontext;
        }
        @Override
        public View createTabContent(String s) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumHeight(0);
            return fakeView;
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager)v.findViewById(R.id.view_pager);
        List<Fragment> listFragments =  new ArrayList<Fragment>();
        listFragments.add(pendingOrder);
        listFragments.add(progressOrder);
        listFragments.add(doneOrder);

        ViewJobOrderPagerAdapter viewJobOrderAdapter = new ViewJobOrderPagerAdapter(getChildFragmentManager(), listFragments);
        viewPager.setAdapter(viewJobOrderAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selectedItem) {
        tabHost.setCurrentTab(selectedItem);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String s) {
        int selectedItem = tabHost.getCurrentTab();
        Log.e("asd",viewPager.toString());
        viewPager.setCurrentItem(selectedItem);
    }


    void getOnProgressOrder() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this.getActivity());
        Call<JobOrderResponse> callJO = api.getJobOrder("[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.IN_PROGRESS+"\"],[\"Job Order\",\"principle\",\"=\",\"" + principle + "\"]]");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    onprogress = response.body().jobOrders.size();
                    SharedPreferences prefs = getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
                    String language = prefs.getString("language","English");
                    TextView label = (TextView)tabHost.getTabWidget().getChildTabViewAt(1).findViewById(android.R.id.title);
                    if(language.contentEquals("English")) {
                        label.setText("On Progress (" + onprogress + ")");
                    } else {
                        label.setText("Dalam Proses (" + onprogress + ")");
                    }
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), t);
            }
        });
    }
    void getPendingOrder() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this.getActivity());
        Call<JobOrderResponse> callJO = api.getJobOrder("[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION+"\"],[\"Job Order\",\"principle\",\"=\",\"" + principle + "\"]]");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    pending = response.body().jobOrders.size();
                    TextView label = (TextView)tabHost.getTabWidget().getChildTabViewAt(0).findViewById(android.R.id.title);
                    label.setText("Pending (" + pending + ")");
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), t);
            }
        });
    }
    void getDoneOrder() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(getActivity());
        String filters = "[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.DONE+"\"], [\"Job Order\",\"principle\",\"=\",\"" + principle + "\"],[\"Job Order\",\"reference\",\"like\",\"%\"]]";
        Call<JobOrderResponse> callJO = api.getJobOrder(filters);
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    done = response.body().jobOrders.size();
                    SharedPreferences prefs = getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
                    String language = prefs.getString("language","English");
                    TextView label = (TextView)tabHost.getTabWidget().getChildTabViewAt(2).findViewById(android.R.id.title);
                    if(language.contentEquals("English")) {
                        label.setText("Complete (" + done + ")");
                    } else {
                        label.setText("Selesai (" + done + ")");
                    }
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
            }
        });
    }


    void getCount() {
        getOnProgressOrder();
        getPendingOrder();
        getDoneOrder();
    }

}
