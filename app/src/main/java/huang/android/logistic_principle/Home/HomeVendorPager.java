package huang.android.logistic_principle.Home;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import huang.android.logistic_principle.R;

/**
 * Created by davidwibisono on 8/22/17.
 */

public class HomeVendorPager extends Fragment {

    public HomeVendorPager() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.pager_vendor, container, false);

        return rootView;
    }
}
