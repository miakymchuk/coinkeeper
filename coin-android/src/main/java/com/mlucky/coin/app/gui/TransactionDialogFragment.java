package com.mlucky.coin.app.gui;


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
    private DatabaseHelper databaseHelper = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout dialogTransactionLayout = (LinearLayout)inflater.inflate(R.layout.dialog_transaction, null);
        final EditText transactionText = (EditText)dialogTransactionLayout.findViewById(R.id.transaction_amount);
        builder.setTitle(R.string.dialog_title_transaction).setView(dialogTransactionLayout);

        builder.setPositiveButton(R.string.set_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dao<CoinApplication, Integer> coinDao;
                CoinApplication coinApplication = null;
                try {
                    coinDao = getHelper().getCoinApplicationDao();
                    coinApplication = CoinApplication.getCoinApplication(coinDao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int fromIndex = getArguments().getInt("fromIndex");
                int toIndex = getArguments().getInt("toIndex");
                String fromItemType = getArguments().getString("fromItemType");
                String toItemType = getArguments().getString("toItemType");

                MoneyFlow from = coinApplication.getMoneyFlowList(fromItemType).get(fromIndex);
                MoneyFlow to = coinApplication.getMoneyFlowList(toItemType).get(toIndex);
                String itemTitle = transactionText.getText().toString();
                try {
                    Dao<InCome, Integer> inComeDao  = getHelper().getInComeDao();
                    Dao<Account, Integer> accountDao  = getHelper().getAccountDao();
                    Dao<Spend, Integer> spendDao  = getHelper().getSpendDao();
                    Dao<Goal, Integer> goalDao  = getHelper().getGoalDao();
                    Dao<Transaction, Integer> transactionDao  = getHelper().getTransactionDao();
                    CoinApplication.startTransaction(from, to , fromItemType, toItemType, itemTitle,
                            transactionDao, inComeDao, accountDao, spendDao, goalDao);
                } catch (SQLException e) {
                    e.printStackTrace();
                }



                int fromLayoutId = getArguments().getInt("fromLayoutId");
                int toLayoutId = getArguments().getInt("toLayoutId");
                setTotal((MoneyFlow) from, fromLayoutId, fromIndex);
                setTotal((MoneyFlow) to, toLayoutId, toIndex);
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
