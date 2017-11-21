package com.huang.android.logistic_principle.ServiceAPI;

import com.huang.android.logistic_principle.Model.Dashboard.DashboardResponse;
import com.huang.android.logistic_principle.Model.History.HistoryResponse;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderCreationResponse;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import com.huang.android.logistic_principle.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic_principle.Model.Location.LocationCreation;
import com.huang.android.logistic_principle.Model.Location.LocationResponse;
import com.huang.android.logistic_principle.Model.LoginPrinciple.LoginPrinciple;
import com.huang.android.logistic_principle.Model.LoginPrinciple.LoginUserPermissionResponse;
import com.huang.android.logistic_principle.Model.Principle.PrincipleContactPersonData;
import com.huang.android.logistic_principle.Model.Principle.PrincipleContactPersonResponse;
import com.huang.android.logistic_principle.Model.Profil.ProfilResponse;
import com.huang.android.logistic_principle.Model.Truck.TruckTypeResponse;
import com.huang.android.logistic_principle.Model.Vendor.VendorResponse;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface API {
//    public final String BASE_URL = "http://172.104.166.12";
    public final String BASE_URL = "http://172.104.163.118";

    //TRUCK
    @GET("/api/resource/Truck Type")
    Call<TruckTypeResponse> getTruckType();

    //JOB ORDER UPDATE
    @GET("/api/resource/Job Order Update?fields=[\"name\",\"waktu\",\"lo\",\"lat\",\"note\",\"job_order\",\"docstatus\",\"status\",\"vendor\",\"principle\"]")
    Call<JobOrderUpdateResponse> getJOUpdate(@Query("filters") String filters, @Query("limit_page_length") String limit);

    //JOB ORDER
    @GET("/api/resource/Job Order?fields=[\"reference\",\"status\",\"name\",\"driver_nama\",\"driver_phone\", \"principle\",\"vendor\",\"pick_location\",\"delivery_location\",\"nama_principle_cp\",\"telp_principle_cp\",\"nama_vendor_cp\",\"telp_vendor_cp\",\"pick_date\",\"expected_delivery\",\"goods_information\",\"notes\",\"accept_date\",\"suggest_truck_type\",\"strict\",\"estimate_volume\",\"truck\",\"truck_lambung\",\"truck_type\",\"truck_volume\",\"kota_pengambilan\",\"alamat_pengambilan\",\"kode_distributor_pengambilan\",\"nama_gudang_pengambilan\",\"kota_pengiriman\",\"alamat_pengiriman\",\"kode_distributor_pengiriman\",\"nama_gudang_pengiriman\"]")
    Call<JobOrderResponse> getJobOrder(@Query("filters") String filters);

    @POST("/api/resource/Job Order")
    Call<JobOrderCreationResponse> submitJobOrder(@Body JobOrderData data);

    //PRINCIPLE
    @POST("/api/resource/Principle Contact Person")
    Call<JSONObject> insertPrincipleCP(@Body PrincipleContactPersonData data);
    @GET("/api/resource/Principle Contact Person?fields=[\"principle\",\"nama\",\"telp\"]")
    Call<PrincipleContactPersonResponse> getPrincipleCP(@Query("filters") String filters);
    @GET("/api/resource/Principle?fields=[\"nama\",\"telp\",\"alamat\",\"email\"]")
    Call<ProfilResponse> getProfile(@Query("filters") String filter);


    //VENDOR
    @GET("/api/resource/Vendor?fields=[\"nama\",\"telp\",\"alamat\"]")
    Call<VendorResponse> getVendorList();

    //LOCATION
    @POST("/api/resource/Location")
    Call<LocationCreation> createLocation(@Body HashMap<String ,String> location);
    @GET("/api/resource/Location?fields=[\"name\",\"kota\",\"alamat\",\"nama_gudang\",\"kode_distributor\",\"phone\",\"nama_pic\"]")
    Call<LocationResponse> getLocation(@Query("filters") String filters);

    @POST("/api/method/login")
    Call<LoginPrinciple> loginUser(@Query("usr") String usr, @Query("pwd") String pwd, @Query("device") String device);
    @GET("/api/resource/User Permission/?fields=[\"for_value\",\"allow\"]")
    Call<LoginUserPermissionResponse> loginPermission(@Query("filters") String filters);


    //old

    @GET("/api/method/logistic.customer.dashboard")
    Call<DashboardResponse> getDashboard(@Query("customer") String cus);

    @GET("/api/method/logistic.job_order.history")
    Call<HistoryResponse> getHistory(@Query("jaid") String jaid);

    @GET("/api/method/logistic.job_order.list")
    Call<JobOrderResponse> getJobOrderList(@Query("type") String type, @Query("typeid") String typeid);
}
