package com.alumui.android.logistic_marketplace_principle.Bantuan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alumui.android.logistic_marketplace_principle.R;
import com.alumui.android.logistic_marketplace_principle.Utility;


public class HelpAndSupport extends Fragment {

    public HelpAndSupport() {
        // Required empty public constructor
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_help_and_support, container, false);

        v.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone("+6285707365938");
            }
        });

        return v;
    }
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
