package com.alumui.android.logistic_marketplace_principle.ServiceAPI;

import com.alumui.android.logistic_marketplace_principle.Model.Dashboard.DashboardResponse;
import com.alumui.android.logistic_marketplace_principle.Model.History.HistoryResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderCreationResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderData;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrder.JobOrderResponse;
import com.alumui.android.logistic_marketplace_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.alumui.android.logistic_marketplace_principle.Model.LoginPrinciple.LoginPrinciple;
import com.alumui.android.logistic_marketplace_principle.Model.Principle.PrincipleContactPersonData;
import com.alumui.android.logistic_marketplace_principle.Model.Principle.PrincipleContactPersonResponse;
import com.alumui.android.logistic_marketplace_principle.Model.Profil.ProfilResponse;
import com.alumui.android.logistic_marketplace_principle.Model.Vendor.VendorResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface API {
    public final String BASE_URL = "http://172.104.166.12";

    //JOB ORDER UPDATE
    @GET("/api/resource/Job Order Update?fields=[\"name\",\"waktu\",\"lo\",\"lat\",\"note\",\"job_order\",\"docstatus\",\"status\",\"vendor\",\"principle\"]")
    Call<JobOrderUpdateResponse> getJOUpdate(@Query("filters") String filters);

    //JOB ORDER
    @GET("/api/resource/Job Order?fields=[\"status\",\"name\", \"principle\",\"vendor\",\"pick_location\",\"delivery_location\",\"nama_principle_cp\",\"telp_principle_cp\",\"nama_vendor_cp\",\"telp_vendor_cp\",\"pick_date\",\"expected_delivery\",\"goods_information\",\"notes\",\"accept_date\"]")
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


    @POST("/api/method/login")
    Call<LoginPrinciple> loginUser(@Query("usr") String usr, @Query("pwd") String pwd, @Query("device") String device);

    //old

    @GET("/api/method/logistic.customer.dashboard")
    Call<DashboardResponse> getDashboard(@Query("customer") String cus);

    @GET("/api/method/logistic.job_order.history")
    Call<HistoryResponse> getHistory(@Query("jaid") String jaid);

    @GET("/api/method/logistic.job_order.list")
    Call<JobOrderResponse> getJobOrderList(@Query("type") String type, @Query("typeid") String typeid);
}
