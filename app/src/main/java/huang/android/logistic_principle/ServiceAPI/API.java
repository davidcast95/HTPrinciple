package huang.android.logistic_principle.ServiceAPI;

import huang.android.logistic_principle.Model.APILogData;
import huang.android.logistic_principle.Model.Communication.CommunicationCreation;
import huang.android.logistic_principle.Model.Communication.CommunicationData;
import huang.android.logistic_principle.Model.Communication.CommunicationResponse;
import huang.android.logistic_principle.Model.Dashboard.DashboardResponse;
import huang.android.logistic_principle.Model.Driver.DriverBackgroundUpdateResponse;
import huang.android.logistic_principle.Model.History.HistoryResponse;
import huang.android.logistic_principle.Model.JobOrder.GetJobOrderResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderCreationResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.Model.JobOrder.JobOrderMetaDataResponse;
import huang.android.logistic_principle.Model.JobOrder.JobOrderResponse;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteCreationResponse;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteData;
import huang.android.logistic_principle.Model.JobOrderRoute.JobOrderRouteResponse;
import huang.android.logistic_principle.Model.JobOrderUpdate.JobOrderUpdateResponse;
import huang.android.logistic_principle.Model.JobOrderUpdateImage.JobOrderUpdateImageResponse;
import huang.android.logistic_principle.Model.Location.LocationCreation;
import huang.android.logistic_principle.Model.Location.LocationResponse;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginPrinciple;
import huang.android.logistic_principle.Model.LoginPrinciple.LoginUserPermissionResponse;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonData;
import huang.android.logistic_principle.Model.Principle.PrincipleContactPersonResponse;
import huang.android.logistic_principle.Model.Profil.ProfilResponse;
import huang.android.logistic_principle.Model.Truck.TruckTypeResponse;
import huang.android.logistic_principle.Model.Vendor.VendorResponse;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface API {
//    public final String BASE_URL = "http://172.104.49.99";
//    public final String BASE_URL = "http://172.104.163.118";
    public final String BASE_URL = "http://system.digitruk.com";

    //API Log
    @POST("/api/resource/API Log")
    Call<JSONObject> sendAPILog(@Body APILogData apiLogData);

    //TRUCK
    @GET("/api/resource/Truck Type")
    Call<TruckTypeResponse> getTruckType();

    //JOB ORDER UPDATE IMAGE
    @GET
    Call<ResponseBody> getImage(@Url String image_link);
    @GET("/api/method/logistic_marketplace.api.get_image_jo_update")
    Call<JobOrderUpdateImageResponse> getJOUpdateImage(@Query("jod_name") String jod_name);

    //JOB ORDER UPDATE
    @GET("/api/method/logistic_marketplace.api.get_job_order_update")
    Call<JobOrderUpdateResponse> getJOUpdate(@Query("job_order") String job_order);

    //JOB ORDER
//    @GET("/api/resource/Job Order?fields=[\"modified\",\"driver\", \"reference\",\"status\",\"name\",\"driver_nama\",\"driver_phone\", \"principle\",\"vendor\",\"pick_location\",\"delivery_location\",\"nama_principle_cp\",\"telp_principle_cp\",\"nama_vendor_cp\",\"telp_vendor_cp\",\"pick_date\",\"expected_delivery\",\"goods_information\",\"notes\",\"accept_date\",\"suggest_truck_type\",\"strict\",\"estimate_volume\",\"truck\",\"truck_lambung\",\"truck_type\",\"truck_volume\",\"kota_pengambilan\",\"alamat_pengambilan\",\"kode_distributor_pengambilan\",\"nama_gudang_pengambilan\",\"kota_pengiriman\",\"alamat_pengiriman\",\"kode_distributor_pengiriman\",\"nama_gudang_pengiriman\"]")
//    Call<JobOrderResponse> getJobOrder(@Query("filters") String filters, @Query("limit_start") String start);
    @GET("/api/method/logistic_marketplace.api.get_job_order")
    Call<GetJobOrderResponse> getJobOrder(@Query("status") String status, @Query("principle") String principle,@Query("ref") String ref, @Query("start") String start);

    //COMMUNICATION
    @GET("/api/resource/Communication?fields=[\"creation\",\"sender\",\"sender_full_name\",\"content\"]&limit_page_length=1000")
    Call<CommunicationResponse> getComment(@Query("filters") String filters);
    @POST("/api/resource/Communication")
    Call<CommunicationCreation> insertComment(@Body CommunicationData communicationData);


    @GET("/api/method/logistic_marketplace.api.get_job_order_by")
    Call<GetJobOrderResponse> getJobOrderBy(@Query("name") String name);
    @GET("/api/method/logistic_marketplace.api.get_job_order_count?role=principle")
    Call<JobOrderMetaDataResponse> getJobOrderCount(@Query("id") String id);

    @POST("/api/resource/Job Order")
    Call<JobOrderCreationResponse> submitJobOrder(@Body JobOrderData data);


    @PUT("/api/resource/Job Order/{joid}")
    Call<JSONObject> updateVendor(@Path("joid") String joid, @Body HashMap<String , String> change);

    //JOB ORDER ROUTE
    @GET("/api/resource/Job Order Route?fields=[\"location\",\"warehouse_name\",\"distributor_code\",\"city\",\"address\",\"contact\",\"nama\",\"phone\",\"type\",\"item_info\",\"remark\",\"order_index\"]&limit_page_length=1000")
    Call<JobOrderRouteResponse> getJobOrderRoute(@Query("filters") String filters);

    @POST("/api/resource/Job Order Route")
    Call<JobOrderRouteCreationResponse> submitJobOrderRoute(@Body JobOrderRouteData data);

    //PRINCIPLE
    @POST("/api/resource/Principle Contact Person")
    Call<JSONObject> insertPrincipleCP(@Body PrincipleContactPersonData data);
    @GET("/api/resource/Principle Contact Person?fields=[\"name\",\"principle\",\"nama\",\"telp\"]")
    Call<PrincipleContactPersonResponse> getPrincipleCP(@Query("filters") String filters);
    @GET("/api/method/logistic_marketplace.api.get_user")
    Call<ProfilResponse> getProfile(@Query("principle") String principle);


    //VENDOR
    @GET("/api/resource/Vendor?fields=[\"name\",\"telp\",\"alamat\"]")
    Call<VendorResponse> getVendorList();
    @GET("/api/method/logistic_marketplace.api.get_user")
    Call<ProfilResponse> getVendor(@Query("vendor") String vendor);


    //LOCATION
    @POST("/api/resource/Location")
    Call<LocationCreation> createLocation(@Body HashMap<String ,String> location);
    @GET("/api/resource/Location?fields=[\"name\",\"kota\",\"alamat\",\"nama_gudang\",\"kode_distributor\",\"phone\",\"nama_pic\"]")
    Call<LocationResponse> getLocation(@Query("filters") String filters, @Query("limit_start") String start);

    @POST("/api/method/login")
    Call<LoginPrinciple> loginUser(@Query("usr") String usr, @Query("pwd") String pwd, @Query("device") String device);
    @GET("/api/resource/User Permission/?fields=[\"for_value\",\"allow\"]")
    Call<LoginUserPermissionResponse> loginPermission(@Query("filters") String filters);

    //DRIVER
    @GET("/api/resource/Driver Background Update?fields=[\"lo\",\"lat\",\"last_update\"]&limit_page_length=1")
    Call<DriverBackgroundUpdateResponse> getBackgroundUpdate(@Query("filters") String filters);

    //old

    @GET("/api/method/logistic.customer.dashboard")
    Call<DashboardResponse> getDashboard(@Query("customer") String cus);

    @GET("/api/method/logistic.job_order.history")
    Call<HistoryResponse> getHistory(@Query("jaid") String jaid);

    @GET("/api/method/logistic.job_order.list")
    Call<JobOrderResponse> getJobOrderList(@Query("type") String type, @Query("typeid") String typeid);
}
