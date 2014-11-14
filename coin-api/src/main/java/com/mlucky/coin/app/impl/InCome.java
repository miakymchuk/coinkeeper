package com.mlucky.coin.app.impl;

import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
public class InCome extends MoneyFlow {

//    @Override
//    public void addTransaction(MoneyFlow to, String money) {
//        super.addTransaction(to, money);
//        Money inComeMoney = Money.parse(getCurrency() + " " + money);
//        this.increaseTotal(inComeMoney);
//        to.increaseTotal(inComeMoney);
//    }
    private Budget budget;

    public InCome() {
        super();
    }

    public InCome(String title, String currency) {
        super(title, currency);
    }
}
