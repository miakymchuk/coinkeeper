package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DataType;
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
    public final static String INCOME_FROM_FIELD_NAME = "from_income_id";
    public final static String ACCOUNT_FROM_FIELD_NAME = "from_account_id";
    public final static String SPEND_FROM_FIELD_NAME = "from_spend_id";
    public final static String GOAL_FROM_FIELD_NAME = "from_goal_id";

    public final static String INCOME_TO_FIELD_NAME = "to_income_id";
    public final static String ACCOUNT_TO_FIELD_NAME = "to_account_id";
    public final static String SPEND_TO_FIELD_NAME = "to_spend_id";
    public final static String GOAL_TO_FIELD_NAME = "to_goal_id";

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, columnName = INCOME_FROM_FIELD_NAME)
    private InCome fromInCome;
    @DatabaseField(foreign = true, columnName = ACCOUNT_FROM_FIELD_NAME)
    private Account fromAccount;
    @DatabaseField(foreign = true,columnName = SPEND_FROM_FIELD_NAME)
    private Spend fromSpend;
    @DatabaseField(foreign = true, columnName = GOAL_FROM_FIELD_NAME)
    private Goal fromGoal;

    @DatabaseField(foreign = true, columnName = INCOME_TO_FIELD_NAME)
    private InCome toInCome;
    @DatabaseField(foreign = true, columnName = ACCOUNT_TO_FIELD_NAME)
    private Account toAccount;
    @DatabaseField(foreign = true,columnName = SPEND_TO_FIELD_NAME)
    private Spend toSpend;
    @DatabaseField(foreign = true, columnName = GOAL_TO_FIELD_NAME)
    private Goal toGoal;


   // @DatabaseField(canBeNull = false, foreign = true,  foreignAutoRefresh = true)
   // private MoneyFlow from;

   // @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
 //   private MoneyFlow to;

    @DatabaseField
    private Date transactionDate;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Money moneyCount;

    @DatabaseField
    private boolean isIncreasing;

    public Transaction(MoneyFlow from, MoneyFlow to, Money sumOfTransaction, boolean isIncreasing) {
        if (from instanceof InCome) {
            fromInCome = (InCome)from;
        } else if( from instanceof Account) {
            fromAccount = (Account)from;
        } else if( from instanceof Spend) {
            fromSpend = (Spend)from;
        } else if( from instanceof Goal) {
            fromGoal = (Goal)from;
        }

        if (to instanceof InCome) {
            toInCome = (InCome)to;
        } else if (to instanceof Account) {
            toAccount = (Account)to;
        } else if (to instanceof Spend) {
            toSpend = (Spend)to;
        } else if (to instanceof Goal) {
            toGoal = (Goal)to;
        }

//        this.from = from;
//        this.to = to;
        this.moneyCount = sumOfTransaction;
        this.transactionDate = new Date();
        this.isIncreasing = isIncreasing;
    }

    public Money getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(Money moneyCount) {
        this.moneyCount = moneyCount;
    }

//    public MoneyFlow getFrom() {
//        return from;
//    }
//
//    public MoneyFlow getTo() {
//        return to;
//    }

    public boolean isIncreasing() {
        return isIncreasing;
    }

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
}
