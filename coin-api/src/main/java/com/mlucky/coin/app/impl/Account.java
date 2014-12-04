package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
@DatabaseTable(tableName = "Account")
public class Account extends MoneyFlow {

     public Account() {
        super();
    }

    public Account(String title, String currency, CoinApplication.ItemType itemType) {
        super(title, currency, itemType);
    }
}
