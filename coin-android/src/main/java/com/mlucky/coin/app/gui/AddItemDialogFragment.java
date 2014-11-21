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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.adapter.MoneyFlowBaseAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by m.iakymchuk on 12.11.2014.
 */
public class AddItemDialogFragment extends DialogFragment {
    private DatabaseHelper databaseHelper = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ApplicationActivity coinActivity = (ApplicationActivity)getActivity();

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

                CoinApplication coinApplication = null;
                try {
                    MoneyFlowBaseAdapter mCommonAdaper = null;
                    Dao<CoinApplication, Integer> coinDao = getHelper().getCoinApplicationDao();
                    coinApplication = CoinApplication.getCoinApplication(coinDao);
                    switch (getArguments().getInt("layoutId")) {
                        case R.id.income_linear_layout:
                            Dao<InCome, Integer> incomeDao =  getHelper().getInComeDao();
                            moneyFlowItem = coinApplication.addIncome(titleItem, incomeDao);
                            mCommonAdaper = coinActivity.getmIncomeAdaper();
                            break;
                        case R.id.account_linear_layout:
                            Dao<Account, Integer> accountDao =  getHelper().getAccountDao();
                            moneyFlowItem = coinApplication.addAccount(titleItem, accountDao);
                            mCommonAdaper = coinActivity.getmAccountAdaper();
                            break;
                        case R.id.spend_linear_layout:
                            Dao<Spend, Integer> spendDao =  getHelper().getSpendDao();
                            moneyFlowItem = coinApplication.addSpend(titleItem, spendDao);
                            mCommonAdaper = coinActivity.getmSpendAdaper();
                            break;
                        case R.id.goal_linear_layout:
                            Dao<Goal, Integer> goalDao =  getHelper().getGoalDao();
                            moneyFlowItem = coinApplication.addGoal(titleItem, goalDao);
                            mCommonAdaper = coinActivity.getmGoalAdaper();
                            break;
                    }
                    mCommonAdaper.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

//                LayoutInflater layoutInflater = coinActivity.getLayoutInflater();
//                LinearLayout inComeLayout = (LinearLayout) coinActivity.findViewById(layoutId);
//                LinearLayout itemLayout =
//                        (LinearLayout) layoutInflater.inflate(R.layout.item, inComeLayout, false);
//
//                TextView titleText = (TextView) itemLayout.findViewById(R.id.item_title);
//                titleText.setText(moneyFlowItem.getTitle().toString());
//                TextView totalText = (TextView) itemLayout.findViewById(R.id.item_total);
//                totalText.setText(moneyFlowItem.getTotal().toString());
//
//                itemLayout.setTag(INCOME_VIEW_TAG);
//                final ImageView imageView;
//                imageView = (ImageView) itemLayout.findViewById(R.id.item_image);
//
//                Drawable mIcon = getResources().getDrawable(R.drawable.ic_launcher);
//                imageView.setBackground(mIcon);
//
//                inComeLayout.addView(itemLayout);
//                final String itemType = moneyFlowItem.getClass().getSimpleName();
//                final int itemIndex = coinApplication.getMoneyFlowList(itemType).indexOf(moneyFlowItem);
//                final int currentLayoutId = inComeLayout.getId();
//
//                if (layoutId != R.id.spend_linear_layout) {
//                    itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View view) {
//                            ClipData dragData = ClipData.newPlainText((CharSequence) view.getTag(), Integer.toString(itemIndex));
//                            ClipData.Item item = new ClipData.Item(itemType);
//                            ClipData.Item itemId = new ClipData.Item(Integer.toString(currentLayoutId));
//                            View.DragShadowBuilder inComeShadow = new View.DragShadowBuilder(imageView);
//                            dragData.addItem(item);
//                            dragData.addItem(itemId);
//
//                            Bundle permissionParameter = new Bundle();
//                            permissionParameter.putString("itemType", itemType);
//                            permissionParameter.putInt("itemIndex", itemIndex);
//                            view.startDrag(dragData, inComeShadow, permissionParameter, 0);
//                            return false;
//                        }
//                    });
//                }
//
//                if (layoutId != R.id.income_linear_layout) {
//                    imageView.setOnDragListener(new View.OnDragListener() {
//                        @Override
//                        public boolean onDrag(View view, DragEvent dragEvent) {
//                            final int action = dragEvent.getAction();
//                            switch (action) {
//                                case DragEvent.ACTION_DRAG_STARTED:
//                                    String dropFromItemType = ((Bundle)dragEvent.getLocalState()).getString("itemType");
//                                    int itemPosition = ((Bundle)dragEvent.getLocalState()).getInt("itemIndex");
//
//                                    return  CoinApplication.isDragAllowed(dropFromItemType, itemType, itemPosition, itemIndex);
//                                case DragEvent.ACTION_DRAG_ENTERED:
//
//                                    return true;
//                                case DragEvent.ACTION_DROP:
//                                    int fromIndex = Integer.parseInt((String) dragEvent.getClipData().getItemAt(0).getText());
//                                    String fromItemType = dragEvent.getClipData().getItemAt(1).getText().toString();
//                                    int fromLayoutId = Integer.parseInt((String) dragEvent.getClipData().getItemAt(2).getText());
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt("fromIndex", fromIndex);
//                                    bundle.putInt("toIndex", itemIndex);
//                                    bundle.putInt("fromLayoutId", fromLayoutId);
//                                    bundle.putInt("toLayoutId", currentLayoutId);
//                                    bundle.putString("fromItemType", fromItemType);
//                                    bundle.putString("toItemType", itemType);
//
//                                    TransactionDialogFragment transactionDialog = new TransactionDialogFragment();
//                                    transactionDialog.setArguments(bundle);
//
//                                    FragmentManager fr = coinActivity.getFragmentManager();
//                                    transactionDialog.show(fr , "dialog_transaction");
//
//                                    return true;
//                            }
//                            return false;
//                        }
//                    });
//                }
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

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        }
        return databaseHelper;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
