package com.mlucky.coin.app.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
//@DatabaseTable(tableName = "MoneyFlow")
public abstract class MoneyFlow extends BaseDaoEnabled {
    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    CoinApplication.ItemType itemType;

    @DatabaseField
    private String currency;

    @DatabaseField
    private String title;

    @DatabaseField
    private int viewPosition;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Money total;

    public void addTransaction(MoneyFlow to, String money, boolean isIncreasing,
                               Dao<Transaction, Integer> transactionDao,
                               Dao< ? extends MoneyFlow, Integer> fromItemDao,
                               Dao< ? extends MoneyFlow, Integer> toItemDao) {
        if (money.isEmpty()) return;
        Money sumOfTransaction =  Money.parse(getCurrency() +" "+ money);
        Transaction newTransaction = new Transaction(this, to, sumOfTransaction, isIncreasing);
        newTransaction.setDao(transactionDao);
        try {
            if (newTransaction.create() != 1) {
                throw new SQLException("Failure adding transaction");
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        }

        Money inComeMoney = Money.parse(getCurrency() + " " + money);
        if (isIncreasing) {
            this.increaseTotal(inComeMoney);
        } else {
            this.decreaseTotal(inComeMoney);
        }
        to.increaseTotal(inComeMoney);

        this.setDao(fromItemDao);
        to.setDao(toItemDao);
        try {
            if (this.update() != 1) {
                throw new SQLException("Failure update entity: " + this.getClass().getSimpleName() +
                        " id: " +  this.getId());
            }
            if (to.update() != 1) {
                throw new SQLException("Failure update entity: " +  to.getClass().getSimpleName() +
                        " id: " +  to.getId());
            }
            if (newTransaction.update() != 1) {
                throw new SQLException("Failure refresh transaction id: " + newTransaction.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editTransactionItems(MoneyFlow to, MoneyFlow previousFrom, MoneyFlow previousTo, Money beforeTransactionAmount,
                                     String money, Dao< ? extends MoneyFlow, Integer> fromItemDao,
                               Dao< ? extends MoneyFlow, Integer> toItemDao) {
        if (money.isEmpty()) return;

        Money inComeMoney = Money.parse(getCurrency() + " " + money);
        if (!beforeTransactionAmount.getAmount().equals(inComeMoney.getAmount())) {
            if (!to.equals(previousTo) && !this.equals(previousFrom))  {
                this.increaseTotal(inComeMoney);
                to.increaseTotal(inComeMoney);
                previousFrom.decreaseTotal(beforeTransactionAmount);
                previousTo.decreaseTotal(beforeTransactionAmount);
            } else if (!to.equals(previousTo)) {
                this.decreaseTotal(beforeTransactionAmount);
                this.increaseTotal(inComeMoney);
                to.increaseTotal(inComeMoney);
                previousTo.decreaseTotal(beforeTransactionAmount);
            } else if (!this.equals(previousFrom)) {
                to.decreaseTotal(beforeTransactionAmount);
                to.increaseTotal(inComeMoney);
                this.increaseTotal(inComeMoney);
                previousFrom.decreaseTotal(beforeTransactionAmount);
            } else {
                this.decreaseTotal(beforeTransactionAmount);
                this.increaseTotal(inComeMoney);
                to.decreaseTotal(beforeTransactionAmount);
                to.increaseTotal(inComeMoney);
            }
        } else if (!to.equals(previousTo) && !this.equals(previousFrom)) {
            this.increaseTotal(inComeMoney);
            to.increaseTotal(inComeMoney);
            previousFrom.decreaseTotal(inComeMoney);
            previousTo.decreaseTotal(inComeMoney);
        } else if (!this.equals(previousFrom)) {
            this.increaseTotal(inComeMoney);
            previousFrom.decreaseTotal(beforeTransactionAmount);
        } else if (!to.equals(previousTo)) {
            to.increaseTotal(inComeMoney);
            previousTo.decreaseTotal(beforeTransactionAmount);
        }



        this.setDao(fromItemDao);
        to.setDao(toItemDao);
        try {
            if (this.update() != 1) {
                throw new SQLException("Failure update entity: " + this.getClass().getSimpleName() +
                        " id: " +  this.getId());
            }
            if (to.update() != 1) {
                throw new SQLException("Failure update entity: " +  to.getClass().getSimpleName() +
                        " id: " +  to.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected MoneyFlow() {

    }

    public MoneyFlow(String title, String currency, CoinApplication.ItemType itemType ) {
        this.currency = currency;
        this.title = title;
        this.total = Money.parse(currency + " " + "0");
        this.itemType = itemType;
    }

    public void removeTransaction(Transaction transaction) {
        handleRemoveOrEditTransaction(transaction, false, "");
    }

    public void editTransaction(Transaction transaction, String strEditMoney) {
        handleRemoveOrEditTransaction(transaction, true, strEditMoney);
    }

    private void handleRemoveOrEditTransaction(Transaction transaction, boolean isEditing, String strEditMoney) {
//        MoneyFlow from = transaction.getFrom();
//        MoneyFlow to = transaction.getTo();
//        int indexFrom = ((ArrayList)from.transactions).indexOf(transaction);
//        int indexTo = ((ArrayList)to.transactions).indexOf(transaction);
//        if(indexFrom == -1 || indexTo == -1)
//            throw new NullPointerException("MoneyFlow: Transaction find failed");
//        Money money = ((Transaction)((ArrayList)from.transactions).get(indexFrom)).getMoneyCount();
//        //        Money moneyTo = to.transactions.get(indexTo).getMoneyCount();
//        if (strEditMoney.isEmpty() && isEditing) return;
//        from.total = from.total.minus(money);
//        to.total = to.total.minus(money);
//        if (isEditing) {
//            Money editMoney =  Money.parse(getCurrency() +" "+ strEditMoney);
//            transaction.setMoneyCount(editMoney);
//            from.total = from.total.plus(editMoney);
//            to.total = to.total.plus(editMoney);
//        } else {
//            from.transactions.remove(indexFrom);
//            to.transactions.remove(indexTo);
//        }
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

//    public Collection<Transaction> getTransactions() {
//        return transactions;
//    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash = hash * id;
        hash = hash *17 + title.hashCode();
        hash = hash *17 + total.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MoneyFlow)) {
            return false;
        }
        MoneyFlow mf = (MoneyFlow)o;

        return id.equals(mf.id) &&
                title.equals(mf.title) &&
                total.equals(mf.total);
    }

    public CoinApplication.ItemType getItemType() {
        return itemType;
    }

    public void setItemType(CoinApplication.ItemType itemType) {
        this.itemType = itemType;
    }
}
