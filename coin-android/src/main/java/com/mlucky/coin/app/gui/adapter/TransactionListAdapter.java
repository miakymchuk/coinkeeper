package com.mlucky.coin.app.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.impl.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by m.iakymchuk on 24.11.2014.
 */
public class TransactionListAdapter extends BaseAdapter {
    Context mContext;
    private List<Transaction> transactions;
    LayoutInflater inflater;
    Integer currentLayoutId;
    Integer currentItemPosition;

    public TransactionListAdapter(Context mContext, List<Transaction> transactions,
                                  Integer currentLayoutId, Integer currentItemPosition) {
        this.mContext = mContext;
        this.transactions = transactions;
        this.inflater = LayoutInflater.from(mContext);
        this.currentLayoutId = currentLayoutId;
        this.currentItemPosition = currentItemPosition;
    }

    @Override
    public int getCount() {

        return this.transactions == null ? 0 : this.transactions.size();
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
            //ListView transactionList = (ListView)((Activity) mContext).findViewById(R.id.transaction_list);
            view = inflater.inflate(R.layout.transaction_item, null);
            mHolder = new TransactionHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (TransactionHolder)view.getTag();
        }

        Transaction transaction = transactions.get(i);
        String date = getDateString(i);
        String transactionAmount;

        // TODO when money moves from Account to Account should be in To increasing
        // TODO but in From decreasing probably need add one more field to db
        String transactionTitle;
        if (transaction.isIncreasing()) {
            String shorcut = mContext.getResources().getString(R.string.transaction_from);
            transactionTitle = shorcut + " " + transaction.getTitleFrom();
            transactionAmount = "+ ";
        } else {
            String shorcut = mContext.getResources().getString(R.string.transaction_to);
            transactionTitle = shorcut + " " + transaction.getTitleTo();
            transactionAmount = "- ";
        }
        transactionAmount += transaction.getMoneyCount().toString();
        mHolder.build(date, transactionTitle, transactionAmount);
        return view;
    }

    private String getDateString(int position) {
        Date transactionDate = transactions.get(position).getTransactionDate();
        String format = "dd.MM";
        SimpleDateFormat sdf = new SimpleDateFormat(format, mContext.getResources().getConfiguration().locale);
        return sdf.format(transactionDate);
    }
}
