package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.gui.adapter.TransactionEditModeBaseAdapter;
import com.mlucky.coin.app.impl.*;
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
    private final String TRANSACTION_INDEX_BUNDLE_KEY = "transaction_index";
    private final String DONE_CODE_BUNDLE_KEY = "transaction_done_code";

    private CoinApplication coinApplication = null;
    private Transaction transaction = null;
    private List<? extends MoneyFlow> itemsFrom;
    private List<? extends MoneyFlow> itemsTo;
    private Dao<Transaction, Integer> transactionDao = null;
    private Dao<InCome, Integer> inComeDao = null;
    private Dao<Account, Integer> accountDao = null;
    private Dao<Spend, Integer> spendDao = null;
    private Dao<Goal, Integer> goalDao = null;
    private Integer transactionIndex = null;
    private MoneyFlow beforeFromItem;
    private MoneyFlow beforeToItem;
    private Money beforeTransactionAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_item_edit_mode);
        Integer transactionId = getIntent().getExtras().getInt(TRANSACTION_ID_BUNDLE_KEY);
        transactionIndex = getIntent().getExtras().getInt(TRANSACTION_INDEX_BUNDLE_KEY);

        try {
            transactionDao = ApplicationActivity.getDatabaseHelper().getTransactionDao();
            inComeDao = ApplicationActivity.getDatabaseHelper().getInComeDao();
            accountDao = ApplicationActivity.getDatabaseHelper().getAccountDao();
            spendDao = ApplicationActivity.getDatabaseHelper().getSpendDao();
            goalDao = ApplicationActivity.getDatabaseHelper().getGoalDao();

            transaction = transactionDao.queryForId(transactionId);
            Dao<CoinApplication, Integer> coinDao =  ApplicationActivity.getDatabaseHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CoinApplication.ItemType itemFromType = transaction.getFromType();
        CoinApplication.ItemType itemToType = transaction.getToType();
        itemsFrom = coinApplication.getMoneyFlowList(itemFromType);
        itemsTo = coinApplication.getMoneyFlowList(itemToType);

        Integer fromId = transaction.getFromId();
        Integer toId = transaction.getToId();
        Integer beforeFromItemIndex = setAdapter(fromId, itemsFrom, R.id.list_view_items_from);
        Integer beforeToItemIndex = setAdapter(toId, itemsTo, R.id.list_view_items_to);

        beforeTransactionAmount = transaction.getMoneyCount();
        beforeFromItem = itemsFrom.get(beforeFromItemIndex);
        beforeToItem = itemsTo.get(beforeToItemIndex);

        EditText amount = (EditText)findViewById(R.id.et_transaction_amount);
        EditText date = (EditText)findViewById(R.id.et_transaction_date);
        EditText time = (EditText)findViewById(R.id.et_transaction_time);
        amount.setText(transaction.getMoneyCount().getAmount().toString());

        Date transactionDate = transaction.getTransactionDate();
        String format = "dd.MM HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, this.getResources().getConfiguration().locale);
        String[] str = sdf.format(transactionDate).split("\\s+");

        date.setText(str[0]);
        time.setText(str[1]);
    }

    private Integer setAdapter(int itemCheckedId, List<? extends MoneyFlow> itemsFrom, int id) {
        TransactionEditModeBaseAdapter listEditModeAdapter = new TransactionEditModeBaseAdapter(this,itemCheckedId, itemsFrom);
        RadioGroup radioGroup = (RadioGroup)findViewById(id);
        Integer index = null;
        for (int i = 0; i < itemsFrom.size(); i++) {
            radioGroup.addView(listEditModeAdapter.getView(i, null, radioGroup));
            if (listEditModeAdapter.isChecked()) {
                int radioButtonId = radioGroup.getChildAt(i).getId();
                radioGroup.check(radioButtonId);
                index = i;
            }
        }
        return index;
    }

    //TODO need implement buttons onClick index of bound
    public void onClickDoneTransactionButton(View view) {
        EditText amount = (EditText)findViewById(R.id.et_transaction_amount);
        String money = amount.getText().toString();
        if (money.isEmpty()) {
            Toast.makeText(this, R.string.toast_error_money, Toast.LENGTH_LONG).show();
            amount.setBackgroundColor(Color.RED);
            return;
        }

        Money newAmount = Money.parse(coinApplication.getLocalCurrency() + " " + money);
        transaction.setMoneyCount(newAmount);

        Integer[] indexesItemFrom = getCheckedItemId(R.id.list_view_items_from, itemsFrom);
        Integer[] indexesItemTo = getCheckedItemId(R.id.list_view_items_to, itemsTo);
        transaction.setFromId(indexesItemFrom[1]);
        transaction.setToId(indexesItemTo[1]);

        String titleItemFrom = itemsFrom.get(indexesItemFrom[0]).getTitle();
        String titleItemTo = itemsTo.get(indexesItemTo[0]).getTitle();
        transaction.setTitleFrom(titleItemFrom);
        transaction.setTitleTo(titleItemTo);

        MoneyFlow from = itemsFrom.get(indexesItemFrom[0]);
        MoneyFlow to = itemsTo.get(indexesItemTo[0]);


        try {
                coinApplication.editItemRelatedToTransaction(from, to, beforeFromItem, beforeToItem,
                        beforeTransactionAmount, money, transaction, inComeDao, accountDao, spendDao, goalDao);

            if (transaction.update() != 1) {
                throw new SQLException("Transaction update failed i : " + transaction.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(TransactionActivity.this, TransactionListActivity.class);
        intent.putExtra(TRANSACTION_INDEX_BUNDLE_KEY, transactionIndex);
        final int DONE = 700;
        intent.putExtra(DONE_CODE_BUNDLE_KEY, DONE);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickRemoveTransactionButton(View view) {
        coinApplication.removeTransaction(transaction, inComeDao, accountDao, spendDao, goalDao, transactionDao);
        Intent intent = new Intent(TransactionActivity.this, TransactionListActivity.class);
        intent.putExtra(TRANSACTION_INDEX_BUNDLE_KEY, transactionIndex);
        final int REMOVE = 600;
        intent.putExtra(DONE_CODE_BUNDLE_KEY, REMOVE);
        setResult(RESULT_OK, intent);
        finish();
    }

    /*
    * @param index[0] is item index in a list
    * @param index[1] is item id in a data base
    * */
    private Integer[] getCheckedItemId(int viewId, List<? extends MoneyFlow> items) {
        RadioGroup radioGroup = (RadioGroup)findViewById(viewId);
        Integer checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(checkedRadioButtonId);
        Integer[] indexes= new Integer[2];
        indexes[0] = radioGroup.indexOfChild(radioButton);
        indexes[1] = items.get(indexes[0]).getId();
        return indexes;
    }

}
