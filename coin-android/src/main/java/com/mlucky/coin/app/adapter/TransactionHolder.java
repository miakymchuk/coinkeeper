package com.mlucky.coin.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.mlucky.coin.app.gui.R;

import java.util.Date;

/**
 * Created by m.iakymchuk on 24.11.2014.
 */
public class TransactionHolder {
    private TextView date;
    private TextView title;
    private TextView amount;

    public TransactionHolder(View view) {
        this.date = (TextView)view.findViewById(R.id.transaction_date);
        this.title = (TextView)view.findViewById(R.id.transaction_title);
        this.amount = (TextView)view.findViewById(R.id.transaction_amount);
    }

    public void build(String date, String title, String amount) {
        this.date.setText(date);
        this.title.setText(title);
        this.amount.setText(amount);
    }

}
