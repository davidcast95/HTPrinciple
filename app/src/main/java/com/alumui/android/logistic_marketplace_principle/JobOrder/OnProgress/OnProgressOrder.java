package com.alumui.android.logistic_marketplace_principle.JobOrder.OnProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderStatus;
import com.alumui.android.logistic_marketplace_principle.Model.MyCookieJar;
import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.ServiceAPI.API;
import com.alumui.android.logistic_marketplace_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OnProgressOrder extends Fragment {

    TextView noData;
    View v;
    OnProgressOrderAdapter onProgressOrderAdapter;
    ListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders;

    public OnProgressOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_job_order, container, false);

        lv=(ListView)v.findViewById(R.id.layout);
        noData=(TextView)v.findViewById(R.id.text_no_data);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        getOnProgressOrder();

        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return v;
    }


    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
        Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailOnProgressOrder.class);
        goDetail.putExtra("index", position);
        startActivity(goDetail);
        }
    };

    void refreshItems() {
        getOnProgressOrder();
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getOnProgressOrder() {
        noData.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this.getActivity());
        Call<JobOrderResponse> callJO = api.getJobOrder("[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.IN_PROGRESS+"\"],[\"Job Order\",\"principle\",\"=\",\"" + principle + "\"]]");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    JobOrderResponse jobOrderResponse = response.body();
                    jobOrders = jobOrderResponse.jobOrders;
                    if (jobOrders.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        OnProgressOrderAdapter onProgressOrderAdapter = new OnProgressOrderAdapter(v.getContext(), R.layout.fragment_job_order_list, jobOrders);
                        lv.setOnItemClickListener(onListClick);
                        lv.setAdapter(onProgressOrderAdapter);
                        lv.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                    onItemsLoadComplete();
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), t);
            }
        });
    }

}