package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.gui.adapter.TransactionListAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by m.iakymchuk on 24.11.2014.
 */
public class TransactionListActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";
    private final String TRANSACTION_ID_BUNDLE_KEY = "transaction_id";
    private final String TRANSACTION_INDEX_BUNDLE_KEY = "transaction_index";
    private final String DONE_CODE_BUNDLE_KEY = "transaction_done_code";
    final int DONE = 700;
    final int REMOVE = 600;

    private List<Transaction> transactions;
    TransactionListAdapter transactionAdapter;
    ListView transactionList;
    private final int TRANSACTION_ACTIVITY_REQUEST_CODE = 888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getDataBaseHelper(getApplicationContext());
        Integer clickedCurrentLayoutId = null;
        Integer clickedCurrentItemPosition = null;
        CoinApplication.ItemType layoutIndex = null;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            clickedCurrentLayoutId = extras.getInt(LAYOUT_ID_BUNDLE_KEY);
            clickedCurrentItemPosition = extras.getInt(ITEM_POSITION_BUNDLE_KEY);
            layoutIndex = getLayoutIndex(clickedCurrentLayoutId);
        }

        CoinApplication coinApplication;

        try {
            Dao<CoinApplication, Integer> coinDao = databaseHelper.getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<Transaction, Integer> transactionDao = databaseHelper.getTransactionDao();
            transactions = coinApplication.loadTransaction(transactionDao, layoutIndex, clickedCurrentItemPosition);

            setContentView(R.layout.transaction_list);

             transactionList = (ListView)findViewById(R.id.transaction_list);

            transactionAdapter = new TransactionListAdapter(this, transactions,
                    clickedCurrentLayoutId, clickedCurrentItemPosition);
            transactionList.setAdapter(transactionAdapter);
            transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    Intent intent = new Intent(TransactionListActivity.this, TransactionActivity.class);
                    Integer transactionDbId = transactions.get(index).getId();
                    intent.putExtra(TRANSACTION_ID_BUNDLE_KEY, transactionDbId);
                    intent.putExtra(TRANSACTION_INDEX_BUNDLE_KEY, index);

                    startActivityForResult(intent, TRANSACTION_ACTIVITY_REQUEST_CODE);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private CoinApplication.ItemType getLayoutIndex(int clickedCurrentLayoutId) {
        switch (clickedCurrentLayoutId) {
            case R.id.income_linear_layout:
                return CoinApplication.ItemType.InCome;
            case R.id.account_linear_layout:
                return CoinApplication.ItemType.Account;
            case R.id.spend_linear_layout:
                return CoinApplication.ItemType.Spend;
            case R.id.goal_linear_layout:
                return CoinApplication.ItemType.Goal;
            default: return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == TRANSACTION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int doneCode = data.getExtras().getInt(DONE_CODE_BUNDLE_KEY);
            if (doneCode == REMOVE) {
                int index = data.getExtras().getInt(TRANSACTION_INDEX_BUNDLE_KEY);
                transactions.remove(index);
            }
            transactionAdapter.notifyDataSetChanged();
            transactionList.setAdapter(transactionAdapter);
        }
    }
}
