package huang.android.logistic_principle.JobOrder.Done;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import huang.android.logistic_principle.JobOrder.Base.JobOrderAdapter;
import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Model.JobOrder.JobOrderData;

import java.util.List;


public class DoneOrderAdapter extends JobOrderAdapter {


    public DoneOrderAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout, list);
    }
}
