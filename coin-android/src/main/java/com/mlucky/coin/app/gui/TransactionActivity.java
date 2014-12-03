package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.*;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.gui.adapter.TransactionEditModeBaseAdapter;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.MoneyFlow;
import com.mlucky.coin.app.impl.Transaction;
import org.joda.money.Money;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        CoinApplication coinApplication = null;
        try {
            Dao<Transaction, Integer> transactionDao = ApplicationActivity.getDatabaseHelper().getTransactionDao();
            transaction = transactionDao.queryForId(transactionId);
            Dao<CoinApplication, Integer> coinDao =  ApplicationActivity.getDatabaseHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CoinApplication.ItemType itemFromType = transaction.getFromType();
        CoinApplication.ItemType itemToType = transaction.getToType();
        List<? extends MoneyFlow> itemsFrom = coinApplication.getMoneyFlowList(itemFromType);
        List<? extends MoneyFlow> itemsTo = coinApplication.getMoneyFlowList(itemToType);

        Integer fromId = transaction.getFromId();
        Integer toId = transaction.getToId();
        setAdapter(fromId, itemsFrom, R.id.list_view_items_from);
        setAdapter(toId, itemsTo, R.id.list_view_items_to);

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

    private void setAdapter(int itemCheckedId, List<? extends MoneyFlow> itemsFrom, int id) {
        TransactionEditModeBaseAdapter listEditModeAdapter = new TransactionEditModeBaseAdapter(this,itemCheckedId, itemsFrom);
        RadioGroup radioGroup = (RadioGroup)findViewById(id);

        for (int i = 0; i < itemsFrom.size(); i++) {
            radioGroup.addView(listEditModeAdapter.getView(i, radioGroup.getChildAt(i), radioGroup));
            if (listEditModeAdapter.isChecked()) {
                int radioButtonId= radioGroup.getChildAt(i).getId();
                radioGroup.check(radioButtonId);
            }
        }
    }
}
