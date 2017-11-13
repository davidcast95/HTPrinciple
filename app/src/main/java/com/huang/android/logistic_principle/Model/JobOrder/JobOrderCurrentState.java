package com.huang.android.logistic_principle.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kristoforus Gumilang on 8/17/2017.
 */

public class JobOrderCurrentState {
    @SerializedName("longitude")
    public String longitude;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("state_name")
    public String state_name;

    @SerializedName("posting_date")
    public String posting_date;

    @SerializedName("name")
    public String name;
}
