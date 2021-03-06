package huang.android.logistic_principle.JobOrder.Pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paging.listview.PagingListView;

import huang.android.logistic_principle.JobOrder.OnProgress.OnProgressAdapter;
import huang.android.logistic_principle.Model.JobOrder.GetJobOrderResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrder.JobOrderResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderStatus;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteResponse;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingOrder extends Fragment implements PagingListView.Pagingable{
    LinearLayout statusToggle;
    TextView noData, pendingToggle, rejectedToggle;
    View v;
    PagingListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders = new ArrayList<>();
    PendingOrderAdapter pendingOrderAdapter;
    String lastQuery = "";
    int pager = 0,limit = 20;

    public PendingOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_job_order, container, false);

        statusToggle = (LinearLayout) v.findViewById(R.id.status_toggle);
        pendingToggle = (TextView) v.findViewById(R.id.pending_toggle);
        rejectedToggle = (TextView) v.findViewById(R.id.rejected_toggle);

        lv=(PagingListView) v.findViewById(R.id.layout);
        noData=(TextView)v.findViewById(R.id.text_no_data);
        loading=(ProgressBar)v.findViewById(R.id.loading);

        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        pendingOrderAdapter = new PendingOrderAdapter(v.getContext(), R.layout.fragment_job_order_list, jobOrders);

        lv.setOnItemClickListener(onListClick);
        lv.setAdapter(pendingOrderAdapter);
        lv.setHasMoreItems(false);
        lv.setPagingableListener(this);

        setupStatusToggle();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getRejectedOrder("");
    }

    void setupStatusToggle() {
        updateStatusToggle();

        pendingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean lastToggle = Utility.utility.getPendingToggle(getActivity());
                Utility.utility.savePendingToggle(!lastToggle,getActivity());
                updateStatusToggle();
                refreshItems();
            }
        });
        rejectedToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean lastToggle = Utility.utility.getRejectedToggle(getActivity());
                Utility.utility.saveRejectedToggle(!lastToggle,getActivity());
                updateStatusToggle();
                refreshItems();
            }
        });
    }

    void updateStatusToggle() {
        if (Utility.utility.getPendingToggle(getActivity())) {
            pendingToggle.setAlpha(1.0f);
        } else {
            pendingToggle.setAlpha(0.5f);
        }
        if (Utility.utility.getRejectedToggle(getActivity())) {
            rejectedToggle.setAlpha(1.0f);
        } else {
            rejectedToggle.setAlpha(0.5f);
        }
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
        pager=0;
        pendingOrderAdapter.clear();
        getRejectedOrder(lastQuery);
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getPendingOrder(String ref_id) {
        if (Utility.utility.getPendingToggle(getActivity())) {
            noData.setVisibility(View.GONE);
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
            API api = Utility.utility.getAPIWithCookie(cookieJar);
            String principle = Utility.utility.getLoggedName(this.getActivity());
            Call<GetJobOrderResponse> callJO = api.getJobOrder(JobOrderStatus.VENDOR_APPROVAL_CONFIRMATION, principle, ref_id + "%", "" + (pager++ * limit));
            callJO.enqueue(new Callback<GetJobOrderResponse>() {
                @Override
                public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                    if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response, "")) {
                        GetJobOrderResponse jobOrderResponse = response.body();

                        if (jobOrderResponse.jobOrders != null) {
                            pendingOrderAdapter.addAll(jobOrderResponse.jobOrders);
                            if (jobOrders.size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                            } else {
                                lv.setVisibility(View.VISIBLE);
                            }
                            lv.onFinishLoading(true, jobOrderResponse.jobOrders);
                        } else {
                            lv.onFinishLoading(false, null);
                        }

                        loading.setVisibility(View.GONE);
                        onItemsLoadComplete();
                    }

                }

                @Override
                public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {
                    Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), t);
                }
            });
        } else {
            lv.onFinishLoading(false,null);
        }
    }

    void getRejectedOrder(String ref_id) {
        if (Utility.utility.getRejectedToggle(getActivity())) {
            pager = 0;
            pendingOrderAdapter.clear();
            noData.setVisibility(View.GONE);
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
            API api = Utility.utility.getAPIWithCookie(cookieJar);
            String principle = Utility.utility.getLoggedName(this.getActivity());
            Call<GetJobOrderResponse> callJO = api.getJobOrder(JobOrderStatus.VENDOR_REJECT, principle, ref_id + "%", "0");
            callJO.enqueue(new Callback<GetJobOrderResponse>() {
                @Override
                public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                    if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response, "")) {
                        GetJobOrderResponse jobOrderResponse = response.body();

                        if (jobOrderResponse.jobOrders != null) {
                            pendingOrderAdapter.addAll(jobOrderResponse.jobOrders);
                        } else {
                        }
                        getPendingOrder(lastQuery);
                        if (jobOrders.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            lv.setVisibility(View.VISIBLE);
                        }
                        loading.setVisibility(View.GONE);
                        onItemsLoadComplete();
                    }

                }

                @Override
                public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {
                    Utility.utility.showConnectivityWithError(getActivity().getApplicationContext(), t);
                }
            });
        } else if (Utility.utility.getPendingToggle(getActivity())) {
            pager = 0;
            pendingOrderAdapter.clear();
            noData.setVisibility(View.GONE);
            getPendingOrder(ref_id);
        }
    }

    public void searchJobOrder(String query) {
        loading.setVisibility(View.VISIBLE);
        lastQuery = query;
        pager = 0;
        pendingOrderAdapter.clear();
        getRejectedOrder(query);
    }

    @Override
    public void onLoadMoreItems() {
        getPendingOrder(lastQuery);
    }
}
