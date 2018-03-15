package huang.android.logistic_principle.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidwibisono on 2/3/18.
 */

public class JobOrderMetaDataMessage {
    @SerializedName("Menunggu Persetujuan Vendor")
    public JobOrderMetaData pending;
    @SerializedName("Di Tolak")
    public JobOrderMetaData rejected;
    @SerializedName("Dalam Proses")
    public JobOrderMetaData onprogress;
    @SerializedName("Selesai")
    public JobOrderMetaData done;
}
