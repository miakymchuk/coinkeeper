package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.adapter.MoneyFlowBaseAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationActivity extends Activity {
    //CoinApplication coinApplication = CoinApplication.getCoinApplication();
    private CoinApplication coinApplication = null;
    private final String LOG_TAG = getClass().getSimpleName();
    private DatabaseHelper databaseHelper = null;
    private MoneyFlowBaseAdapter mIncomeAdaper;
    private MoneyFlowBaseAdapter mAccountAdaper;
    private MoneyFlowBaseAdapter mSpendAdaper;
    private MoneyFlowBaseAdapter mGoalAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);

        try {
            Dao<CoinApplication, Integer> coinDao = getHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<InCome, Integer> incomeDao =  getHelper().getInComeDao();
            Dao<Account, Integer> accountDao =  getHelper().getAccountDao();
            Dao<Spend, Integer> spendDao =  getHelper().getSpendDao();
            Dao<Goal, Integer> goalDao =  getHelper().getGoalDao();
            coinApplication.loadEntityFromDatabase(incomeDao, accountDao, spendDao, goalDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mIncomeAdaper = setMoneyFlowBaseAapter(coinApplication.getInComeSources(), R.id.income_linear_layout);
        mAccountAdaper = setMoneyFlowBaseAapter(coinApplication.getAccounts(), R.id.account_linear_layout);
        mSpendAdaper = setMoneyFlowBaseAapter(coinApplication.getSpends(), R.id.spend_linear_layout);
        mGoalAdaper = setMoneyFlowBaseAapter(coinApplication.getGoals(), R.id.goal_linear_layout);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showAddButtonDialog(int layoutId) {
        DialogFragment addDialog = new AddItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        addDialog.setArguments(bundle);
        addDialog.show(getFragmentManager(), "dialog_add_item");
    }

    private MoneyFlowBaseAdapter setMoneyFlowBaseAapter(List<? extends MoneyFlow> mMoneyFlowList, final int layoutId) {
        TwoWayGridView mInComeGridView = (TwoWayGridView) findViewById(layoutId);
        MoneyFlowBaseAdapter mMoneyFlowAdaper = new MoneyFlowBaseAdapter(this, this.coinApplication,
                mMoneyFlowList, layoutId);
        mInComeGridView.setAdapter(mMoneyFlowAdaper);
        mInComeGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                showAddButtonDialog(layoutId);
            }
        });
        return mMoneyFlowAdaper;
    }

    public void  OnAddButtonClick(View view) {
        DialogFragment addDialog = new AddItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", view.getId());
        addDialog.setArguments(bundle);
        addDialog.show(getFragmentManager(), "dialog_add_item");
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(getApplicationContext());
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public MoneyFlowBaseAdapter getmIncomeAdaper() {
        return mIncomeAdaper;
    }

    public MoneyFlowBaseAdapter getmAccountAdaper() {
        return mAccountAdaper;
    }

    public MoneyFlowBaseAdapter getmSpendAdaper() {
        return mSpendAdaper;
    }

    public MoneyFlowBaseAdapter getmGoalAdaper() {
        return mGoalAdaper;
    }
}
