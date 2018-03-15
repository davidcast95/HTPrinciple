package huang.android.logistic_principle.JobOrder.OnProgress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paging.listview.PagingListView;

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


public class OnProgressOrder extends Fragment implements PagingListView.Pagingable{

    TextView noData;
    View v;
    PagingListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders = new ArrayList<>();
    OnProgressAdapter onProgressAdapter;
    int pager = 0, limit = 20;

    String lastQuery = "";

    public OnProgressOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_job_order, container, false);

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
        onProgressAdapter = new OnProgressAdapter(v.getContext(), R.layout.fragment_job_order_list, jobOrders);
        lv.setOnItemClickListener(onListClick);
        lv.setAdapter(onProgressAdapter);
        lv.setHasMoreItems(false);
        lv.setPagingableListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getOnProgressOrder("");
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
        pager = 0;
        onProgressAdapter.clear();
        getOnProgressOrder("");
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getOnProgressOrder(String ref_id) {
        noData.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String principle = Utility.utility.getLoggedName(this.getActivity());
        Call<GetJobOrderResponse> callJO = api.getJobOrder(JobOrderStatus.IN_PROGRESS,  principle,  ref_id + "%", "" + (pager++ * limit));
        callJO.enqueue(new Callback<GetJobOrderResponse>() {
            @Override
            public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response,"")) {
                    GetJobOrderResponse jobOrderResponse = response.body();
                    if (jobOrderResponse.jobOrders != null) {
                        onProgressAdapter.addAll(jobOrderResponse.jobOrders);

                        lv.onFinishLoading(true,jobOrderResponse.jobOrders);
                    } else {
                        lv.onFinishLoading(false,null);
                    }
                    if (jobOrders.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    }
                    else {
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
    }



    public void searchJobOrder(String query) {
        loading.setVisibility(View.VISIBLE);
        lastQuery = query;
        pager = 0;
        onProgressAdapter.clear();
        getOnProgressOrder(query);
    }

    @Override
    public void onLoadMoreItems() {
        getOnProgressOrder(lastQuery);
    }
}
