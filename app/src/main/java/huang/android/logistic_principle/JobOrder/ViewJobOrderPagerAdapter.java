package huang.android.logistic_principle.JobOrder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by davidwibisono on 8/23/17.
 */

public class ViewJobOrderPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> listFragments;

    public ViewJobOrderPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.listFragments=listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }
}
