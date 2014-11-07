package com.mlucky.coin.app.impl;

import org.joda.money.Money;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by m.iakymchuk on 03.11.14.
 */
public abstract class MoneyFlow {

    private String currency;
    private String title;
    private Money total;
    private List<Transaction> transactions = new ArrayList<Transaction>();

    public void addTransaction(MoneyFlow to, String money) {
        Money sumOfTransaction =  Money.parse(getCurrency() +" "+ money);
        Transaction newTransaction = new Transaction(this, to, sumOfTransaction);
        this.transactions.add(newTransaction);
        to.transactions.add(newTransaction);
        if (money.isEmpty()) return;
        Money inComeMoney = Money.parse(getCurrency() + " " + money);
        this.increaseTotal(inComeMoney);
        to.increaseTotal(inComeMoney);
    }

    public MoneyFlow(String title, String currency) {
        this.currency = currency;
        this.title = title;
        this.total = Money.parse(currency + " " + "0");
    }

    public void removeTransaction(Transaction transaction) {
        handleRemoveOrEditTransaction(transaction, false, "");
    }

    public void editTransaction(Transaction transaction, String strEditMoney) {
        handleRemoveOrEditTransaction(transaction, true, strEditMoney);
    }

    private void handleRemoveOrEditTransaction(Transaction transaction, boolean isEditing, String strEditMoney) {
        MoneyFlow from = transaction.getFrom();
        MoneyFlow to = transaction.getTo();
        int indexFrom = from.transactions.indexOf(transaction);
        int indexTo = to.transactions.indexOf(transaction);
        if(indexFrom == -1 || indexTo == -1)
            throw new NullPointerException("MoneyFlow: Transaction find failed");
        Money money = from.transactions.get(indexFrom).getMoneyCount();
        //        Money moneyTo = to.transactions.get(indexTo).getMoneyCount();
        if (strEditMoney.isEmpty() && isEditing) return;
        from.total = from.total.minus(money);
        to.total = to.total.minus(money);
        if (isEditing) {
            Money editMoney =  Money.parse(getCurrency() +" "+ strEditMoney);
            transaction.setMoneyCount(editMoney);
            from.total = from.total.plus(editMoney);
            to.total = to.total.plus(editMoney);
        } else {
            from.transactions.remove(indexFrom);
            to.transactions.remove(indexTo);
        }
    }

    public void increaseTotal(Money amount) {
        this.total = total.plus(amount);
    }

    public void decreaseTotal(Money amount) {
        this.total = total.minus(amount);
    }

    public Money getTotal() { return total; }

    public String getTitle() {
        return title;
    }

    public String getCurrency() {
        return currency;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
