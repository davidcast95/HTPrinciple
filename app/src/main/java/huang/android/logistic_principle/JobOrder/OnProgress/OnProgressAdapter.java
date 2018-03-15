package huang.android.logistic_principle.JobOrder.OnProgress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import huang.android.logistic_principle.JobOrder.Base.JobOrderAdapter;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;
import huang.android.logistic_principle.R;

import java.util.List;


public class OnProgressAdapter extends JobOrderAdapter {


    public OnProgressAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout, list);
    }
}
