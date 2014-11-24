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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoinApplication coinApplication = null;
        List transactions = null;
        try {
            Dao<CoinApplication, Integer> coinDao = getHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<Transaction, Integer> transactionDao = getHelper().getTransactionDao();
            transactions = coinApplication.loadTransaction(transactionDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        setContentView(R.layout.transaction_list);
        ListView transactionList = (ListView)findViewById(R.id.transaction_list);
        TransactionListAdapter transactionAdapter = new TransactionListAdapter(this, transactions);
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
}
