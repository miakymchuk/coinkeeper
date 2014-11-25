package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.adapter.MoneyFlowBaseAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;

/**
 * Created by m.iakymchuk on 12.11.2014.
 */
public class AddItemDialogFragment extends DialogFragment {
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ApplicationActivity coinActivity = (ApplicationActivity)getActivity();

        LayoutInflater inflater = coinActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        final EditText titleInput = (EditText)dialogView.findViewById(R.id.title_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(coinActivity);
        builder.setTitle(R.string.dialog_title_add_item);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.set_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            String titleItem = titleInput.getText().toString();

            try {
                MoneyFlowBaseAdapter mCommonAdaper = null;
                Dao<CoinApplication, Integer> coinDao = coinActivity.getHelper().getCoinApplicationDao();
                CoinApplication coinApplication = CoinApplication.getCoinApplication(coinDao);
                TwoWayGridView currentGridView = null;
                switch (getArguments().getInt(LAYOUT_ID_BUNDLE_KEY)) {
                    case R.id.income_linear_layout:
                        Dao<InCome, Integer> incomeDao =  coinActivity.getHelper().getInComeDao();
                        coinApplication.addIncome(titleItem, incomeDao);
                        mCommonAdaper = coinActivity.getmIncomeAdapter();
                        currentGridView = (TwoWayGridView)coinActivity.findViewById(R.id.income_linear_layout);
                        break;
                    case R.id.account_linear_layout:
                        Dao<Account, Integer> accountDao =  coinActivity.getHelper().getAccountDao();
                        coinApplication.addAccount(titleItem, accountDao);
                        mCommonAdaper = coinActivity.getmAccountAdapter();
                        currentGridView = (TwoWayGridView)coinActivity.findViewById(R.id.account_linear_layout);
                        break;
                    case R.id.spend_linear_layout:
                        Dao<Spend, Integer> spendDao =  coinActivity.getHelper().getSpendDao();
                        coinApplication.addSpend(titleItem, spendDao);
                        mCommonAdaper = coinActivity.getmSpendAdapter();
                        currentGridView = (TwoWayGridView)coinActivity.findViewById(R.id.spend_linear_layout);
                        break;
                    case R.id.goal_linear_layout:
                        Dao<Goal, Integer> goalDao =  coinActivity.getHelper().getGoalDao();
                        coinApplication.addGoal(titleItem, goalDao);
                        mCommonAdaper = coinActivity.getmGoalAdapter();
                        currentGridView = (TwoWayGridView)coinActivity.findViewById(R.id.goal_linear_layout);
                        break;
                }
                mCommonAdaper.notifyDataSetChanged();
                currentGridView.setAdapter(mCommonAdaper);
            } catch (SQLException e) {
                e.printStackTrace();
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
