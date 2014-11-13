package com.mlucky.coin.app.impl;


import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import sun.net.www.content.text.Generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by m.iakymchuk on 05.11.2014.
 */
public class CoinApplication {
    private static CoinApplication coinApplication = null;
    private static final String localCurrency = "UAH";
    private final Date installDate;
    private Date currentDate;

    List<InCome> inComeSources;
    List<Account> accounts;
    List<Spend> spends;
    List<Goal> goals;

    private Money currentBalance;
    private Money currentSpend;
    private Money plannedSpend;
    private Money spendBudget;
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

    public InCome addIncome(String title) {
       InCome income = new InCome(title, localCurrency);
       this.inComeSources.add(income);
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
        from.addTransaction(to, sMoney);
    }

    public void addAccountGoalTransaction(Account from, Goal to,  String sMoney) {
        from.addTransaction(to, sMoney);
    }

    public void addAccountSpendTransaction(Account from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney);
    }

    public void addGoalSpendTransaction(Goal from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney);
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
