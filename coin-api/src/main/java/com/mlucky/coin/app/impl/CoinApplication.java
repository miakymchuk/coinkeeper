package com.mlucky.coin.app.impl;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by m.iakymchuk on 05.11.2014.
 */

@DatabaseTable(tableName = "CoinApplicationTable")
public class CoinApplication extends BaseDaoEnabled {
    @DatabaseField(id = true)
    private int id = 1;
    private static CoinApplication coinApplication = null;

    @DatabaseField
    private static final String localCurrency = "UAH";

    @DatabaseField(dataType = DataType.DATE)
    private final Date installDate;

    @DatabaseField(dataType = DataType.DATE)
    private Date currentDate;

   // @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    List<InCome> inComeSources =  new ArrayList<InCome>();

   // @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    List<Account> accounts = new ArrayList<Account>();

    //@ForeignCollectionField(eager = true, maxEagerLevel = 2)
    List<Spend> spends = new ArrayList<Spend>();

   // @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    List<Goal> goals  = new ArrayList<Goal>();

    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    private Money currentBalance;

    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    private Money currentSpend;

    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    private Money plannedSpend;

    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    private Money spendBudget;

    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    private Money planedInCome;

    private CoinApplication() {
        super();
        this.installDate = new Date();
        this.currentDate = new Date();
        CurrencyUnit local = CurrencyUnit.getInstance(localCurrency);
        this.currentBalance = Money.zero(local);
        this.currentSpend = Money.zero(local);
        this.plannedSpend = Money.zero(local);
        this.spendBudget = Money.zero(local);
        this.planedInCome = Money.zero(local);
        coinApplication = this;
    }

    public static synchronized CoinApplication getCoinApplication(Dao<CoinApplication, Integer> coinDao) throws SQLException {
        if (coinApplication == null) {
            coinApplication = coinDao.queryForId(1);
        }
        if (coinApplication == null) {
            CoinApplication tmp = new CoinApplication();
            tmp.setDao(coinDao);
            tmp.create();
            return tmp;
        } else {
            return coinApplication;
        }
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public InCome addIncome(String title, Dao<InCome, Integer> incomeDao) throws SQLException {
       InCome income = new InCome(title, localCurrency);
       income.setDao(incomeDao);
       income.create();
       this.inComeSources.add(income);
       return income;
    }

    public Account addAccount(String title, Dao<Account, Integer>accountDao) throws SQLException {
        Account account = new Account(title, localCurrency);
        account.setDao(accountDao);
        account.create();
        this.accounts.add(account);
        return account;
    }

    public Spend addSpend(String title, Dao<Spend, Integer> spendDao) throws SQLException {
        Spend spend = new Spend(title, localCurrency);
        spend.setDao(spendDao);
        spend.create();
        this.spends.add(spend);
        return spend;
    }

    public Goal addGoal(String title, Dao<Goal, Integer> goalDao) throws SQLException {
        Goal goal = new Goal(title, localCurrency);
        goal.setDao(goalDao);
        goal.create();
        this.goals.add(goal);
        return goal;
    }

    private void addInComeAccountTransaction(InCome from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, true);
    }

    private void addAccountAccountTransaction(Account from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    private void addAccountGoalTransaction(Account from, Goal to,  String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    private void addAccountSpendTransaction(Account from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    private void addGoalSpendTransaction(Goal from, Spend to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    private void addGoalAccountTransaction(Goal from, Account to, String sMoney) {
        from.addTransaction(to, sMoney, false);
    }

    private void addGoalGoalTransaction(Goal from, Goal to, String sMoney) {
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

    public List<? extends MoneyFlow> getMoneyFlowList(String type) {
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

    public static boolean isDragAllowed(String dragFromItemType, String dropToItemType, int dragFromItemIndex,int  dropToItemIndex) {
        if (("InCome".equals(dragFromItemType) && "InCome".equals(dropToItemType)) ||
            ("InCome".equals(dragFromItemType) && "Spend".equals(dropToItemType)) ||
            ("InCome".equals(dragFromItemType) && "Goal".equals(dropToItemType)) ||
            ("Account".equals(dragFromItemType) && "Account".equals(dropToItemType)
                && dragFromItemIndex == dropToItemIndex) ||
            ("Goal".equals(dragFromItemType) && "Goal".equals(dropToItemType)
                && dragFromItemIndex == dropToItemIndex)) {
            return false;
        }
        return true;
    }

    public void loadEntityFromDatabase(Dao<InCome, Integer> incomeDao, Dao<Account, Integer> accountDao,
                                       Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao) throws SQLException {
        this.inComeSources = incomeDao.queryForAll();
        this.accounts = accountDao.queryForAll();
        this.spends = spendDao.queryForAll();
        this.goals = goalDao.queryForAll();

    }

    public static void  startTransaction(MoneyFlow from, MoneyFlow to, String fromItemType, String toItemType, String title ) {
        if (fromItemType.equals("InCome") && toItemType.equals("Account")) {
            coinApplication.addInComeAccountTransaction((InCome) from, (Account) to, title);
        } else if (fromItemType.equals("Account") && toItemType.equals("Spend")) {
            coinApplication.addAccountSpendTransaction((Account) from, (Spend) to, title);
        } else if (fromItemType.equals("Account") && toItemType.equals("Goal")) {
            coinApplication.addAccountGoalTransaction((Account) from, (Goal) to,title);
        } else if (fromItemType.equals("Goal") && toItemType.equals("Spend")) {
            coinApplication.addGoalSpendTransaction((Goal) from, (Spend) to, title);
        } else if (fromItemType.equals("Goal") && toItemType.equals("Goal")) {
            coinApplication.addGoalGoalTransaction((Goal) from, (Goal) to, title);
        } else if (fromItemType.equals("Account") && toItemType.equals("Account")) {
            coinApplication.addAccountAccountTransaction((Account) from, (Account) to, title);
        }else if (fromItemType.equals("Goal") && toItemType.equals("Account")) {
            coinApplication.addGoalAccountTransaction((Goal) from, (Account) to, title);
        }
    }
}
