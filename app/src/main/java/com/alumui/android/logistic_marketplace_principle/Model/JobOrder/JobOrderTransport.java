package com.alumui.android.logistic_marketplace_principle.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kristoforus Gumilang on 8/17/2017.
 */

public class JobOrderTransport {
    @SerializedName("driver")
    public String driver;

    @SerializedName("truck_type")
    public String truck_type;

    @SerializedName("truck_license_plate")
    public String nopol;
}
