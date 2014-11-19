package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
@DatabaseTable(tableName = "Spend")
public class Spend extends MoneyFlow {


    @DatabaseField(foreign = true)
    private Budget budget;

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Spend(String title, String currency) {
        super(title, currency);
        this.budget = new Budget(getCurrency());
    }

    public Spend() {
        super();
    }
}
