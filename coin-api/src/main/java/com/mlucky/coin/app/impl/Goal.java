package com.mlucky.coin.app.impl;

import org.joda.money.Money;

/**
 * Created by m.iakymchuk on 04.11.2014.
 */
public class Goal extends MoneyFlow {

    private boolean isClosed;
    private Budget budget;

    @Override
    public void addTransaction(MoneyFlow to, String money, boolean isIncreasing) {
        super.addTransaction(to, money, isIncreasing);
//        if(this.budget.isOverBudget(this)) {
//            this.budget.setBudget(this.getTotal());
//        }
    }

    public Goal() {
        super();
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;

    }

    public boolean isClosed() { return isClosed; }

    public Goal(String title, String currency) {
        super(title, currency);
    }

}
