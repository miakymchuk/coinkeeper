package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.mlucky.coin.app.impl.*;

import java.net.URLClassLoader;

/**
 * Created by m.iakymchuk on 12.11.2014.
 */
public class AddItemDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity coinActivity = getActivity();

        final CoinApplication coinApplication = CoinApplication.getCoinApplication();
        final String INCOME_VIEW_TAG = "income_index";

        LayoutInflater inflater = coinActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        final EditText titleInput = (EditText)dialogView.findViewById(R.id.title_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(coinActivity);
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

                LayoutInflater layoutInflater = coinActivity.getLayoutInflater();
                LinearLayout inComeLayout = (LinearLayout) coinActivity.findViewById(layoutId);
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
                final String itemType = moneyFlowItem.getClass().getSimpleName();
                final int itemIndex = coinApplication.getMoneyFlowList(itemType).indexOf(moneyFlowItem);
                final int currentLayoutId = inComeLayout.getId();
                //final int itemLayoutId = itemLayout.getId();
                if (layoutId != R.id.spend_linear_layout) {


                    itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            ClipData dragData = ClipData.newPlainText((CharSequence) view.getTag(), Integer.toString(itemIndex));
                            ClipData.Item item = new ClipData.Item(itemType);
                            ClipData.Item itemId = new ClipData.Item(Integer.toString(currentLayoutId));
                            View.DragShadowBuilder inComeShadow = new View.DragShadowBuilder(imageView);
                            dragData.addItem(item);
                            dragData.addItem(itemId);

                            Bundle permissionParameter = new Bundle();
                            permissionParameter.putString("itemType", itemType);
                            permissionParameter.putInt("itemIndex", itemIndex);
                            view.startDrag(dragData, inComeShadow, permissionParameter, 0);
                            return false;
                        }
                    });
                }

                if (layoutId != R.id.income_linear_layout) {
                    imageView.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {
                            final int action = dragEvent.getAction();
                            switch (action) {
                                case DragEvent.ACTION_DRAG_STARTED:
                                    String dropFromItemType = ((Bundle)dragEvent.getLocalState()).getString("itemType");
                                    int itemPosition = ((Bundle)dragEvent.getLocalState()).getInt("itemIndex");
                                    if ("InCome".equals(dropFromItemType) && "InCome".equals(itemType)) {
                                        return false;
                                    } else if ("InCome".equals(dropFromItemType) && "Spend".equals(itemType)) {
                                        return false;
                                    } else if ("InCome".equals(dropFromItemType) && "Goal".equals(itemType)) {
                                        return false;
                                    } else if ("Account".equals(dropFromItemType) && "Account".equals(itemType)
                                            && itemPosition == itemIndex){
                                        return false;
                                    } else if ("Goal".equals(dropFromItemType) && "Goal".equals(itemType)
                                            && itemPosition == itemIndex){
                                        return false;
                                    }
                                    return true;
                                case DragEvent.ACTION_DRAG_ENTERED:

                                    return true;
                                case DragEvent.ACTION_DROP:
                                    int fromIndex = Integer.parseInt((String) dragEvent.getClipData().getItemAt(0).getText());
                                    String fromItemType = dragEvent.getClipData().getItemAt(1).getText().toString();
                                    int fromLayoutId = Integer.parseInt((String) dragEvent.getClipData().getItemAt(2).getText());
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("fromIndex", fromIndex);
                                    bundle.putInt("toIndex", itemIndex);
                                    bundle.putInt("fromLayoutId", fromLayoutId);
                                    bundle.putInt("toLayoutId", currentLayoutId);
                                    bundle.putString("fromItemType", fromItemType);
                                    bundle.putString("toItemType", itemType);

                                    TransactionDialogFragment transactionDialog = new TransactionDialogFragment();
                                    transactionDialog.setArguments(bundle);

                                    FragmentManager fr = coinActivity.getFragmentManager();
                                    transactionDialog.show(fr , "dialog_transaction");


                                    return true;
                            }
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
