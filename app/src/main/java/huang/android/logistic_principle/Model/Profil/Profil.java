package huang.android.logistic_principle.Model.Profil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Profil {
    @SerializedName("nama")
    public String name;
    @SerializedName("telp")
    public String phone;
    @SerializedName("alamat")
    public String address;
    @SerializedName("email")
    public String email;
    @SerializedName("profile_image")
    public List<String> profile_image = new ArrayList<>();
}
