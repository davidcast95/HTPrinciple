package huang.android.logistic_principle.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/16/2017.
 */

public class JobOrderMessage {
    @SerializedName("data")
    public List<JobOrderData> data;
}
