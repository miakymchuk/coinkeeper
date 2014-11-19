package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.iakymchuk on 03.11.14.
 */

@DatabaseTable(tableName = "InCome")
public class InCome extends MoneyFlow {

    @DatabaseField(foreign = true)
    private Budget budget;

    public InCome() {
        super();
    }

    public InCome(String title, String currency) {
        super(title, currency);
        budget = new Budget(getCurrency());
    }
}
