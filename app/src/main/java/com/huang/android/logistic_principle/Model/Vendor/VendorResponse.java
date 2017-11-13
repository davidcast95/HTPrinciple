package com.huang.android.logistic_principle.Model.Vendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 8/23/17.
 */

public class VendorResponse {
    @SerializedName("data")
    public List<VendorData> vendors;
}
