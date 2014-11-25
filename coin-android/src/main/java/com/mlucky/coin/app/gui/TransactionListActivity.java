package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.adapter.TransactionListAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by m.iakymchuk on 24.11.2014.
 */
public class TransactionListActivity extends Activity {
    private DatabaseHelper databaseHelper = null;
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer clickedCurrentLayoutId = null;
        Integer clickedCurrentItemPosition = null;
        Integer layoutIndex = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            clickedCurrentLayoutId = extras.getInt(LAYOUT_ID_BUNDLE_KEY);
            clickedCurrentItemPosition = extras.getInt(ITEM_POSITION_BUNDLE_KEY);
            layoutIndex = getlayoutIndex(clickedCurrentLayoutId);
        }

        CoinApplication coinApplication;
        List transactions = null;
        try {
            Dao<CoinApplication, Integer> coinDao = getHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<Transaction, Integer> transactionDao = getHelper().getTransactionDao();
            transactions = coinApplication.loadTransaction(transactionDao, layoutIndex, clickedCurrentItemPosition);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.transaction_list);

        ListView transactionList = (ListView)findViewById(R.id.transaction_list);
        TransactionListAdapter transactionAdapter = new TransactionListAdapter(this, transactions,
                clickedCurrentLayoutId, clickedCurrentItemPosition);
        transactionList.setAdapter(transactionAdapter);
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

    private int getlayoutIndex(int clickedCurrentLayoutId) {
        switch (clickedCurrentLayoutId) {
            case R.id.income_linear_layout:
                return 1;
            case R.id.account_linear_layout:
                return 2;
            case R.id.spend_linear_layout:
                return 3;
            case R.id.goal_linear_layout:
                return 4;
            default: return 0;
        }
    }
}
