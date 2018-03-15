package huang.android.logistic_principle.Lihat_Profil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import huang.android.logistic_principle.Fonts.Hind;
import huang.android.logistic_principle.Model.MyCookieJar;
import huang.android.logistic_principle.Model.Profil.Profil;
import huang.android.logistic_principle.Model.Profil.ProfilResponse;
import huang.android.logistic_principle.ServiceAPI.API;
import huang.android.logistic_principle.Utility;
import huang.android.logistic_principle.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyProfile extends Fragment {
    TextView nameTextEdit, phoneTextEdit, emailTextEdit, addresTextEdit;

    public MyProfile() {
        // Required empty public constructor
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_view_profile, container, false);

        TextView nameText = (TextView)v.findViewById(R.id.name_text),
                emailText = (TextView)v.findViewById(R.id.email_text),
                phoneText = (TextView)v.findViewById(R.id.phone_text),
                addressText = (TextView)v.findViewById(R.id.address_text);

        Utility.utility.setFont(nameText,Hind.LIGHT,getContext());
        Utility.utility.setFont(emailText,Hind.LIGHT,getContext());
        Utility.utility.setFont(phoneText,Hind.LIGHT,getContext());
        Utility.utility.setFont(addressText,Hind.LIGHT,getContext());

        nameTextEdit = (TextView)v.findViewById(R.id.name);
        phoneTextEdit = (TextView)v.findViewById(R.id.telephone);
        emailTextEdit = (TextView)v.findViewById(R.id.email);
        addresTextEdit = (TextView)v.findViewById(R.id.address);

        Utility.utility.setFont(nameTextEdit, Hind.MEDIUM,getContext());
        Utility.utility.setFont(phoneTextEdit, Hind.MEDIUM,getContext());
        Utility.utility.setFont(emailTextEdit, Hind.MEDIUM,getContext());
        Utility.utility.setFont(addresTextEdit, Hind.MEDIUM,getContext());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getProfile();
    }

    //API Connectivity
    void getProfile() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String name = Utility.utility.getLoggedName(this.getActivity());
        Call<ProfilResponse> profilResponseCall = api.getProfile("[[\"Principle\",\"name\",\"=\",\""+name+"\"]]");
        profilResponseCall.enqueue(new Callback<ProfilResponse>() {
            @Override
            public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response,"")) {
                    ProfilResponse profilResponse = response.body();
                    List<Profil> profils = profilResponse.data;
                    if (profils.size() > 0) {
                        Profil profil = profils.get(0);
                        nameTextEdit.setText(profil.name);
                        phoneTextEdit.setText(profil.phone);
                        emailTextEdit.setText(profil.email);
                        addresTextEdit.setText(profil.address);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfilResponse> call, Throwable t) {

            }
        });
    }

}
