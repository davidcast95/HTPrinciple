package com.huang.android.logistic_principle.Model.Truck;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 18/10/17.
 */

public class TruckTypeResponse {
    @SerializedName("data")
    public List<TruckTypeData> truckTypes;
}
