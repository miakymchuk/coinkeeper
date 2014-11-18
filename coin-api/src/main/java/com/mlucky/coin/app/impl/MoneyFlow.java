package com.mlucky.coin.app.impl;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
@DatabaseTable(tableName = "MonewFlowTable")
public abstract class MoneyFlow extends BaseDaoEnabled {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    private String currency;

    @DatabaseField
    private String title;

   // @DatabaseField
    private Money total;

    //@ForeignCollectionField(eager = true)
    private Collection<Transaction> transactions = new ArrayList<Transaction>();

    public void addTransaction(MoneyFlow to, String money, boolean isIncreasing) {
        if (money.isEmpty()) return;
        Money sumOfTransaction =  Money.parse(getCurrency() +" "+ money);
        Transaction newTransaction = new Transaction(this, to, sumOfTransaction, isIncreasing);
        this.transactions.add(newTransaction);
        to.transactions.add(newTransaction);
        Money inComeMoney = Money.parse(getCurrency() + " " + money);
        if (isIncreasing) {
            this.increaseTotal(inComeMoney);
        } else {
            this.decreaseTotal(inComeMoney);
        }
        to.increaseTotal(inComeMoney);
    }

    protected MoneyFlow() {

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
        int indexFrom = ((ArrayList)from.transactions).indexOf(transaction);
        int indexTo = ((ArrayList)to.transactions).indexOf(transaction);
        if(indexFrom == -1 || indexTo == -1)
            throw new NullPointerException("MoneyFlow: Transaction find failed");
        Money money = ((Transaction)((ArrayList)from.transactions).get(indexFrom)).getMoneyCount();
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

    public Collection<Transaction> getTransactions() {
        return transactions;
    }
}
