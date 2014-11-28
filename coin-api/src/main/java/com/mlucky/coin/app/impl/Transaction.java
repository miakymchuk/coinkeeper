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
    public final static String FROM_ID_FIELD_NAME = "from_id";
    public final static String TO_ID_FIELD_NAME = "to_id";

    public final static String FROM_TYPE_FIELD_NAME = "from_type";
    public final static String TO_TYPE_FIELD_NAME = "to_type";

    public final static String DATE_FIELD_NAME = "date_of_transaction";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(columnName = FROM_ID_FIELD_NAME)
    Integer fromId;

    @DatabaseField(columnName = TO_ID_FIELD_NAME)
    Integer toId;

    @DatabaseField(columnName = FROM_TYPE_FIELD_NAME, dataType = DataType.ENUM_INTEGER)
    CoinApplication.ItemType fromType;

    @DatabaseField( columnName = TO_TYPE_FIELD_NAME, dataType = DataType.ENUM_INTEGER)
    CoinApplication.ItemType toType;

    @DatabaseField
    private String titleFrom;
    @DatabaseField
    private String titleTo;

    @DatabaseField(columnName = DATE_FIELD_NAME)
    private Date transactionDate;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Money moneyCount;

    @DatabaseField
    private boolean isIncreasing;

    public Transaction(MoneyFlow from, MoneyFlow to, Money sumOfTransaction, boolean isIncreasing) {
        if (from instanceof InCome) {
            fromType = CoinApplication.ItemType.InCome;
        } else if( from instanceof Account) {
            fromType = CoinApplication.ItemType.Account;
        } else if( from instanceof Spend) {
            fromType = CoinApplication.ItemType.Spend;
        } else if( from instanceof Goal) {
            fromType = CoinApplication.ItemType.Goal;
        }

        if (to instanceof InCome) {
            toType = CoinApplication.ItemType.InCome;
        } else if (to instanceof Account) {
            toType = CoinApplication.ItemType.Account;
        } else if (to instanceof Spend) {
            toType = CoinApplication.ItemType.Spend;
        } else if (to instanceof Goal) {
            toType = CoinApplication.ItemType.Goal;
        }
        fromId = from.getId();
        toId = to.getId();
        this.titleFrom = from.getTitle();
        this.titleTo = to.getTitle();

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

    public boolean isIncreasing() {
        return isIncreasing;
    }

    public Transaction() {
    }

    public Integer getId() {
        return id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getTitleFrom() {
        return titleFrom;
    }

    public String getTitleTo() {
        return titleTo;
    }

    public Integer getFromId() {
        return fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public CoinApplication.ItemType getFromType() {
        return fromType;
    }

    public CoinApplication.ItemType getToType() {
        return toType;
    }
}
