package com.alumui.android.logistic_marketplace_principle.Model.JobOrderUpdate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 9/10/17.
 */

public class JobOrderUpdateResponse {
    @SerializedName("data")
    public List<JobOrderUpdateData> jobOrderUpdates;
}
