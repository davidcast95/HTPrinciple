package huang.android.logistic_principle.Base;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import huang.android.logistic_principle.R;
import huang.android.logistic_principle.Utility;

/**
 * Created by davidwibisono on 1/23/18.
 */

public class ListViewDialog {
    Activity activity;
    ListView lv;
    int dialogLayout;
    public int listHeight = 0;
    ListViewDialogListener listViewDialogListener;

    public ListViewDialog(Activity activity, int dialogLayout, final ListView lv) {
        this.activity = activity;
        this.lv = lv;
        this.dialogLayout = dialogLayout;
        ViewTreeObserver observer = lv.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(lv);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog(i);
            }
        });
    }

    public void setListViewDialogListener(ListViewDialogListener listener) {
        this.listViewDialogListener = listener;
    }

    void dialog(int position) {
        new AlertDialog.Builder(activity);
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(dialogLayout, null);

        listViewDialogListener.onSetContentDialog(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


}
