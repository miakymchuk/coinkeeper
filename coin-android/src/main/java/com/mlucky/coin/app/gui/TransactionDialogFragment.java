package com.mlucky.coin.app.gui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayAbsListView;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by m.iakymchuk on 13.11.2014.
 */
public class TransactionDialogFragment extends DialogFragment {
    private final String FROM_ITEM_INDEX_BUNDLE_KEY = "fromIndex";
    private final String TO_ITEM_INDEX_BUNDLE_KEY = "toIndex";
    private final String FROM_LAYOUT_ID_BUNDLE_KEY = "fromLayoutId";
    private final String TO_LAYOUT_ID_BUNDLE_KEY = "toLayoutId";
    private final String FROM_ITEM_TYPE_BUNDLE_KEY = "fromItemType";
    private final String TO_ITEM_TYPE_BUNDLE_KEY = "toItemType";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ApplicationActivity mActivity = (ApplicationActivity)getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();

        LinearLayout dialogTransactionLayout = (LinearLayout)inflater.inflate(R.layout.dialog_transaction, null);
        final EditText transactionText = (EditText)dialogTransactionLayout.findViewById(R.id.transaction_amount);
        builder.setTitle(R.string.dialog_title_transaction).setView(dialogTransactionLayout);

        builder.setPositiveButton(R.string.set_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dao<CoinApplication, Integer> coinDao;
                CoinApplication coinApplication = null;
                try {
                    coinDao = mActivity.getHelper().getCoinApplicationDao();
                    coinApplication = CoinApplication.getCoinApplication(coinDao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int fromIndex = getArguments().getInt(FROM_ITEM_INDEX_BUNDLE_KEY);
                int toIndex = getArguments().getInt(TO_ITEM_INDEX_BUNDLE_KEY);
                String fromItemType = getArguments().getString(FROM_ITEM_TYPE_BUNDLE_KEY);
                String toItemType = getArguments().getString(TO_ITEM_TYPE_BUNDLE_KEY);

                MoneyFlow from = coinApplication.getMoneyFlowList(fromItemType).get(fromIndex);
                MoneyFlow to = coinApplication.getMoneyFlowList(toItemType).get(toIndex);
                String itemTitle = transactionText.getText().toString();
                try {
                    Dao<InCome, Integer> inComeDao  = mActivity.getHelper().getInComeDao();
                    Dao<Account, Integer> accountDao  = mActivity.getHelper().getAccountDao();
                    Dao<Spend, Integer> spendDao  = mActivity.getHelper().getSpendDao();
                    Dao<Goal, Integer> goalDao  = mActivity.getHelper().getGoalDao();
                    Dao<Transaction, Integer> transactionDao  = mActivity.getHelper().getTransactionDao();
                    CoinApplication.startTransaction(from, to , fromItemType, toItemType, itemTitle,
                            transactionDao, inComeDao, accountDao, spendDao, goalDao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int fromLayoutId = getArguments().getInt(FROM_LAYOUT_ID_BUNDLE_KEY);
                int toLayoutId = getArguments().getInt(TO_LAYOUT_ID_BUNDLE_KEY);
                setTotal(from, fromLayoutId, fromIndex);
                setTotal(to, toLayoutId, toIndex);
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

    private void setTotal(MoneyFlow moneyFlow,int fromLayoutId, int fromIndex ) {
        TwoWayAbsListView fromLinearLayout = (TwoWayAbsListView)getActivity().findViewById(fromLayoutId);
        LinearLayout  fromLinearLayoutItem = (LinearLayout)fromLinearLayout.getChildAt(fromIndex);

        TextView fromTextView = (TextView)fromLinearLayoutItem.findViewById(R.id.item_total);
        fromTextView.setText(moneyFlow.getTotal().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
