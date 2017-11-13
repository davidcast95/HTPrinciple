package com.huang.android.logistic_principle.Model.Principle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 8/24/17.
 */

public class PrincipleContactPersonResponse {
    @SerializedName("data")
    public List<PrincipleContactPersonData> cps;
}
