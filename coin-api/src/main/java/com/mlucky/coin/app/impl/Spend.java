package com.mlucky.coin.app.impl;

import org.joda.money.Money;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
public class Spend extends MoneyFlow {

    private Budget budget;

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Spend(String title, String currency) {
        super(title, currency);
    }

    public Spend() {
        super();
    }
}
