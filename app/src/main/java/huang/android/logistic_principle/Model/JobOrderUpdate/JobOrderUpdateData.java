package huang.android.logistic_principle.Model.JobOrderUpdate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import huang.android.logistic_principle.Model.JobOrderUpdateImage.JobOrderUpdateImageData;

/**
 * Created by davidwibisono on 9/10/17.
 */

public class JobOrderUpdateData {
    @SerializedName("name")
    public String name;
    @SerializedName("waktu")
    public String time = "-";
    @SerializedName("job_order")
    public String joid = "-";
    @SerializedName("note")
    public String note = "-";
    @SerializedName("lo")
    public String longitude = "0.0";
    @SerializedName("lat")
    public String latitude = "0.0";
    @SerializedName("vendor")
    public String vendor = "-";
    @SerializedName("principle")
    public String principle = "-";
    @SerializedName("docstatus")
    public int docstatus = 0;
    @SerializedName("status")
    public String status = "-";
    @SerializedName("location")
    public String location;
    @SerializedName("warehouse_name")
    public String warehouse_name;
    @SerializedName("city")
    public String city;
    @SerializedName("image_count")
    public List<JobOrderUpdateImageData> images;
}
