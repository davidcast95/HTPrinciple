package com.huang.android.logistic_principle.JobOrder.Pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huang.android.logistic_principle.JobOrder.OnProgress.OnProgressOrderAdapter;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.ServiceAPI.API;
import com.huang.android.logistic_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingOrder extends Fragment {
    TextView noData;
    View v;
    OnProgressOrderAdapter onProgressOrderAdapter;
    ListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders;

    public PendingOrder() {
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

        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPendingOrder("");
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailPendingOrder.class);
            goDetail.putExtra("index", position);
            startActivity(goDetail);
        }
    };

    void refreshItems() {
        getPendingOrder("");
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getPendingOrder(String ref_id) {
        noData.setVisibility(View.GONE);
        lv.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this.getActivity());
        Call<JobOrderResponse> callJO = api.getJobOrder("[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION+"\"],[\"Job Order\",\"principle\",\"=\",\"" + principle + "\"],[\"Job Order\",\"reference\",\"like\",\"" + ref_id + "%\"]]");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                Log.e("asd",response.message());
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    JobOrderResponse jobOrderResponse = response.body();
                    jobOrders = jobOrderResponse.jobOrders;
                    if (jobOrders.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    }
                    else {
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

    public void searchJobOrder(String query) {
        loading.setVisibility(View.VISIBLE);
        getPendingOrder(query);
    }
}
