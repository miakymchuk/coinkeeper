package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

import java.util.Date;

/**
 * Created by m.iakymchuk on 03.11.14.
 */
@DatabaseTable(tableName = "TransactionTable")
public class Transaction extends BaseDaoEnabled {

   /* @DatabaseField(canBeNull = true, foreign = true)
    private InCome moneyFlow;*/

    //private static long idCounter = 0;
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private MoneyFlow from;

    @DatabaseField(canBeNull = false, foreign = true)
    private MoneyFlow to;

    @DatabaseField
    private Date transactionDate;

   // @DatabaseField
    private Money moneyCount;

    @DatabaseField
    private boolean isIncreasing;

    public Transaction(MoneyFlow from, MoneyFlow to, Money sumOfTransaction, boolean isIncreasing) {
        //this.id = createdId();
        this.from = from;
        this.to = to;
        this.moneyCount = sumOfTransaction;
        this.transactionDate = new Date();
        this.isIncreasing = isIncreasing;
    }

  /*  public static synchronized String createdId() {
        return String.valueOf(idCounter++);
    }*/

    public Money getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(Money moneyCount) {
        this.moneyCount = moneyCount;
    }

//    public String getId() {
//        return id;
//    }

    public MoneyFlow getFrom() {
        return from;
    }

    public MoneyFlow getTo() {
        return to;
    }

    public boolean isIncreasing() {
        return isIncreasing;
    }

    public Transaction() {
    }
}
