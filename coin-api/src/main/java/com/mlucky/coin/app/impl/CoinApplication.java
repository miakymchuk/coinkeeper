package com.mlucky.coin.app.impl;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by m.iakymchuk on 05.11.2014.
 */

@DatabaseTable(tableName = "CoinApplicationTable")
public class CoinApplication extends BaseDaoEnabled {
    private static CoinApplication coinApplication = null;

    @DatabaseField
    private static final String localCurrency = "UAH";

    @DatabaseField
    private final Date installDate;

    @DatabaseField
    private Date currentDate;

   // @ForeignCollectionField(eager = true)
    List<InCome> inComeSources;

    //@ForeignCollectionField(eager = true)
    List<Account> accounts;

   // @ForeignCollectionField(eager = true)
    List<Spend> spends;

   // @ForeignCollectionField(eager = true)
    List<Goal> goals;

    //@DatabaseField
    private Money currentBalance;

   // @DatabaseField
    private Money currentSpend;

   // @DatabaseField
    private Money plannedSpend;

    //@DatabaseField
    private Money spendBudget;

    //@DatabaseField
    private Money planedInCome;

    private CoinApplication() {
        super();
        this.installDate = new Date();
        this.currentDate = new Date();
        this.inComeSources =  new ArrayList<InCome>();
        this.accounts =  new ArrayList<Account>();
        this.spends = new ArrayList<Spend>();
        this.goals = new ArrayList<Goal>();
        CurrencyUnit local = CurrencyUnit.getInstance(localCurrency);
        this.currentBalance = Money.zero(local);
        this.currentSpend = Money.zero(local);
        this.plannedSpend = Money.zero(local);
        this.spendBudget = Money.zero(local);
        this.planedInCome = Money.zero(local);
        coinApplication = this;
    }

    public static synchronized CoinApplication getCoinApplication() {
        return (coinApplication == null) ? new CoinApplication() : coinApplication;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public InCome addIncome(String title, Dao<InCome, Integer> incomeDao) throws SQLException {
       InCome income = new InCome(title, localCurrency);

       income.setDao(incomeDao);
       income.create();
       this.inComeSources.add(income);
       //this.update();
       return income;
    }

    public Account addAccount(String title) {
        Account account = new Account(title, localCurrency);
        this.accounts.add(account);
        return account;
    }

    public Spend addSpend(String title) {
        Spend spend = new Spend(title, localCurrency);
        this.spends.add(spend);
        return spend;
    }

    public Goal addGoal(String title) {
        Goal goal = new Goal(title, localCurrency);
        this.goals.add(goal);
        return goal;
    }

    public void addInComeAccountTransaction(InCome from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, true);
    }

    public void addAccountAccountTransaction(Account from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void addAccountGoalTransaction(Account from, Goal to,  String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void addAccountSpendTransaction(Account from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void addGoalSpendTransaction(Goal from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void addGoalAccountTransaction(Goal from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void addGoalGoalTransaction(Goal from, Goal to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    public void removeInCome(InCome inCome) {

    }

    public void removeAccount(Account account) {


    }

    public void removeSpend(Spend spend) {

    }

    public void removeGoal(Goal goal) {

    }

    public void removeTransaction(Transaction transaction) {

    }

    public List<InCome> getInComeSources() {
        return inComeSources;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Spend> getSpends() {
        return spends;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<?> getMoneyFlowList(String type) {
        if ("InCome".equals(type))
            return inComeSources;
        else if ("Account".equals(type))
            return accounts;
        else if ("Spend".equals(type))
            return spends;
        else if ("Goal".equals(type))
            return goals;
        else return null;
    }
}
