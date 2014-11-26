package com.mlucky.coin.app.gui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.mlucky.coin.app.gui.ApplicationActivity;
import com.mlucky.coin.app.gui.R;

/**
 * Created by m.iakymchuk on 26.11.2014.
 */
public class RemoveItemDialogFragment extends DialogFragment {
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ApplicationActivity mActivity = (ApplicationActivity)getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_remove_item, null);

        Button btnRemoveItem = (Button)dialogView.findViewById(R.id.btn_remove_item);
        Button btnRemoveItemAndTransaction = (Button)dialogView.findViewById(R.id.btn_remove_item_and_transaction);
        Button btnCancel = (Button)dialogView.findViewById(R.id.btn_remove_cancel);

        builder.setTitle(R.string.dialog_title_remove_item);
        builder.setView(dialogView);

        Bundle bundle = getArguments();
        final int layoutId = bundle.getInt(LAYOUT_ID_BUNDLE_KEY);
        final int itemPosition = bundle.getInt(ITEM_POSITION_BUNDLE_KEY);

        btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.removeItem(layoutId, itemPosition, false);
                getDialog().dismiss();

            }
        });

        btnRemoveItemAndTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.removeItem(layoutId, itemPosition, true);
                getDialog().dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        return builder.create();
    }
}
