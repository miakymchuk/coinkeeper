package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.Transaction;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by m.iakymchuk on 28.11.2014.
 */
public class TransactionActivity extends Activity{
    private final String TRANSACTION_ID_BUNDLE_KEY = "transaction_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_item_edit_mode);
        Integer transactionId = getIntent().getExtras().getInt(TRANSACTION_ID_BUNDLE_KEY);
        Transaction transaction = null;
        try {
            Dao<Transaction, Integer> transactionDao = ApplicationActivity.getDatabaseHelper().getTransactionDao();
             transaction = transactionDao.queryForId(transactionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TextView fromTitle = (TextView) findViewById(R.id.tv_transaction_from_title);
        TextView toTitle = (TextView) findViewById(R.id.tv_transaction_to_title);
        fromTitle.setText(transaction.getTitleFrom());
        toTitle.setText(transaction.getTitleTo());

        EditText amount = (EditText)findViewById(R.id.et_transaction_amount);
        EditText date = (EditText)findViewById(R.id.et_transaction_date);
        EditText time = (EditText)findViewById(R.id.et_transaction_time);
        amount.setText(transaction.getMoneyCount().toString());

        Date transactionDate = transaction.getTransactionDate();
        String format = "dd.MM HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, this.getResources().getConfiguration().locale);
        String[] str = sdf.format(transactionDate).split("\\s+");

        date.setText(str[0]);
        time.setText(str[1]);
    }
}
