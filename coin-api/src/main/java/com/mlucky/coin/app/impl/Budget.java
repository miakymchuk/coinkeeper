package com.mlucky.coin.app.impl;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.Money;

/**
 * Created by m.iakymchuk on 05.11.2014.
 */
@DatabaseTable(tableName = "Budget")
public class Budget {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Money budget ;

    public Budget() {
    }
    public Budget(String currency) {
        this.budget = Money.parse(currency +" 0");
    }

    public void setBudget(Money budget) {
        this.budget = budget;
    }

    public Money getBudget() {
        return budget;
    }

    public boolean isOverBudget(MoneyFlow moneyFlow) {
        return this.budget.isGreaterThan(moneyFlow.getTotal());
    }


}
