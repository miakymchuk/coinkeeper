package com.mlucky.coin.app.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.MoneyFlow;

/**
 * Created by m.iakymchuk on 12.11.2014.
 */
public class AddItemDialogFragment extends DialogFragment {
    private CoinApplication coinApplication = CoinApplication.getCoinApplication();
    private static final String INCOME_VIEW_TAG = "income_index";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        final EditText titleInput = (EditText)dialogView.findViewById(R.id.title_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_add_item);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.set_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int layoutId = 0;
                MoneyFlow moneyFlowItem = null;
                String titleItem = titleInput.getText().toString();
                switch (getArguments().getInt("layoutId")) {
                    case R.id.income_add_button:
                        layoutId = R.id.income_linear_layout;
                         moneyFlowItem = coinApplication.addIncome(titleItem);
                        break;
                    case R.id.account_add_button:
                        layoutId = R.id.account_linear_layout;
                        moneyFlowItem = coinApplication.addAccount(titleItem);
                        break;
                    case R.id.spend_add_button:
                        layoutId = R.id.spend_linear_layout;
                        moneyFlowItem = coinApplication.addSpend(titleItem);
                        break;
                    case R.id.goal_add_button:
                        layoutId = R.id.goal_linear_layout;
                        moneyFlowItem = coinApplication.addGoal(titleItem);
                        break;
                }

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                LinearLayout inComeLayout = (LinearLayout) getActivity().findViewById(layoutId);
                LinearLayout itemLayout =
                        (LinearLayout) layoutInflater.inflate(R.layout.item, inComeLayout, false);

                TextView titleText = (TextView) itemLayout.findViewById(R.id.item_title);
                titleText.setText(moneyFlowItem.getTitle().toString());
                TextView totalText = (TextView) itemLayout.findViewById(R.id.item_total);
                totalText.setText(moneyFlowItem.getTotal().toString());

                itemLayout.setTag(INCOME_VIEW_TAG);
                final ImageView imageView;
                imageView = (ImageView) itemLayout.findViewById(R.id.item_image);

                Drawable mIcon = getResources().getDrawable(R.drawable.ic_launcher);
                imageView.setBackground(mIcon);

                inComeLayout.addView(itemLayout);

                if (layoutId != R.id.spend_linear_layout) {
                    final int index = coinApplication.getInComeSources().indexOf(moneyFlowItem);

                    itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ClipData dragData = ClipData.newPlainText((CharSequence) view.getTag(), Integer.toString(index));
                            //ClipData.Item item = new ClipData.Item(Integer.toString(totalText.getId()));
                            View.DragShadowBuilder inComeShadow = new View.DragShadowBuilder(imageView);
                            //dragData.addItem(item);
                            view.startDrag(dragData, inComeShadow, null, 0);
                            return false;
                        }
                    });
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().cancel();
            }
        });

        return builder.create();
    }
}
