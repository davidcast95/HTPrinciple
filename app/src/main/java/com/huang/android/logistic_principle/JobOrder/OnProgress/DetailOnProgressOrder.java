package com.huang.android.logistic_principle.JobOrder.OnProgress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic_principle.JobOrder.Base.DetailOrder;
import com.huang.android.logistic_principle.JobOrder.TrackHistory;
import com.huang.android.logistic_principle.Maps.TrackOrderMaps;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import com.huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic_principle.Model.MyCookieJar;
import com.huang.android.logistic_principle.R;
import com.huang.android.logistic_principle.ServiceAPI.API;
import com.huang.android.logistic_principle.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOnProgressOrder extends DetailOrder {


    @Override
    public List<JobOrderData> getJobOrders(List<JobOrderData> jobOrderDatas) {
        return OnProgressOrder.jobOrders;
    }

    @Override
    public String getMenuTitle() {
        return "Active Order Details";
    }
}
