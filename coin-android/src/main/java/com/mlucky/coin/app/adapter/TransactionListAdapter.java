package com.mlucky.coin.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.impl.MoneyFlow;
import com.mlucky.coin.app.impl.Transaction;

import java.util.Date;
import java.util.List;

/**
 * Created by m.iakymchuk on 24.11.2014.
 */
public class TransactionListAdapter extends BaseAdapter {
    Context transactionListActivityContext;
    private List<Transaction> transactions;
    LayoutInflater inflater;

    public TransactionListAdapter(Context transactionListActivityContext, List<Transaction> transactions) {
        this.transactionListActivityContext = transactionListActivityContext;
        this.transactions = transactions;
        this.inflater = LayoutInflater.from(transactionListActivityContext);
    }

    @Override
    public int getCount() {
        return this.transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TransactionHolder mHolder;
        if (view == null) {
            ListView transactionList = (ListView)((Activity)transactionListActivityContext).findViewById(R.id.transaction_list);
            view = inflater.inflate(R.layout.transaction_item, transactionList, false);
            mHolder = new TransactionHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (TransactionHolder)view.getTag();
        }

        // TODO fix sorry should be "from or to"
        Date transactionDate = transactions.get(i).getTransactionDate();
        String transactionAmount = transactions.get(i).getMoneyCount().toString();
        mHolder.build(transactions.get(i).getTransactionDate(), "sorry", transactionAmount);
        return view;
    }
}
