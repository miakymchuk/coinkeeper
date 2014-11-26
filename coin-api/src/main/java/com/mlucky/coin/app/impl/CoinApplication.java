package com.mlucky.coin.app.impl;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.QueryBuilder;
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

   // public enum ItemType { INCOME, ACCOUNT, SPEND, GOAL}
    private final int INCOME = 1;
    private final int ACCOUNT = 2;
    private final int SPEND = 3;
    private final int GOAL = 4;

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


    public void removeInCome(int itemPosition, Dao<InCome , Integer> inComeDao, boolean isRemoveTransaction,
                             Dao<Transaction, Integer> transactionDao) throws  SQLException{
        if (isRemoveTransaction) {
            removeTransaction(transactionDao, INCOME, itemPosition);
        }
        InCome inCome = this.inComeSources.get(itemPosition);
        inComeDao.deleteById(inCome.getId());
        this.inComeSources.remove(itemPosition);

    }

    public void removeAccount(int itemPosition, Dao<Account , Integer> accountDao, boolean isRemoveTransaction,
                              Dao<Transaction, Integer> transactionDao) throws  SQLException {
        if (isRemoveTransaction) {
            removeTransaction(transactionDao, ACCOUNT, itemPosition);
        }
        Account account = this.accounts.get(itemPosition);
        accountDao.deleteById(account.getId());
        this.accounts.remove(itemPosition);
    }

    public void removeSpend(int itemPosition, Dao<Spend , Integer> spendDao, boolean isRemoveTransaction,
                            Dao<Transaction, Integer> transactionDao) throws  SQLException {
        if (isRemoveTransaction) {
            removeTransaction(transactionDao, SPEND, itemPosition);
        }
        Spend spend = this.spends.get(itemPosition);
        spendDao.deleteById(spend.getId());
        this.spends.remove(itemPosition);
    }

    public void removeGoal( int itemPosition, Dao<Goal , Integer> goalDao, boolean isRemoveTransaction,
                            Dao<Transaction, Integer> transactionDao) throws  SQLException {
        if (isRemoveTransaction) {
            removeTransaction(transactionDao, GOAL, itemPosition);
        }
        Goal item = this.goals.get(itemPosition);
        goalDao.deleteById(item.getId());
        this.goals.remove(itemPosition);
    }

    //TODO need implement right removing by changing totalAmount of each item to which this transactions was related
    private void removeTransaction(Dao<Transaction, Integer> transactionDao, final int itemType, int itemPosition) throws SQLException {
            List<Transaction>  transactions = loadTransaction(transactionDao, itemType, itemPosition );
            switch (itemType) {
                case INCOME:
                    for (Transaction transaction: transactions) {
                        MoneyFlow to;
                        MoneyFlow from;
                        if (transaction.getFromInCome() == null) {
                            to = transaction.getToInCome();

                            from = transaction.getFromInCome();
                            if (from == null)
                                from = transaction.getFromAccount();
                            else if (from == null)
                                from = transaction.getFromSpend();
                            else if (from == null)
                                from = transaction.getFromGoal();
                        } else {
                            from = transaction.getFromInCome();

                            to = transaction.getToInCome();
                            if (to == null)
                                to = transaction.getToAccount();
                            else if (to == null)
                                to = transaction.getToSpend();
                            else if (to == null)
                                to = transaction.getToGoal();
                        }

                        //TODO here we have only ID of From and To item
                        Money money = transaction.getMoneyCount();

                        Money previousAmount = from.getTotal().minus(money);
                        from.setTotal(previousAmount);
                        previousAmount = to.getTotal().minus(money);
                        to.setTotal(previousAmount);

                    }
                    break;
            }
            for (Transaction transaction: transactions) {
                transactionDao.deleteById(transaction.getId());
            }
    }

    private void addInComeAccountTransaction(InCome from, Account to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                             Dao<InCome, Integer> inComeDao,  Dao<Account, Integer> accountDao) {
        from.addTransaction(to, sMoney, true, transactionDao, inComeDao, accountDao);
    }

    private void addAccountAccountTransaction(Account from, Account to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                              Dao<Account, Integer> fromAccountDao,  Dao<Account, Integer> toAccountDao) {
        from.addTransaction(to, sMoney, false, transactionDao, fromAccountDao, toAccountDao);
    }

    private void addAccountGoalTransaction(Account from, Goal to,  String sMoney, Dao<Transaction, Integer> transactionDao,
                                           Dao<Account, Integer> accountDao,  Dao<Goal, Integer> goalDao) {
        from.addTransaction(to, sMoney, false, transactionDao, accountDao, goalDao);
    }

    private void addAccountSpendTransaction(Account from, Spend to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                            Dao<Account, Integer> accountDao,  Dao<Spend, Integer> spendDao) {
        from.addTransaction(to, sMoney, false, transactionDao, accountDao, spendDao);
    }

    private void addGoalSpendTransaction(Goal from, Spend to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                         Dao<Goal, Integer> goalDao,  Dao<Spend, Integer> spendDao) {
        from.addTransaction(to, sMoney, false, transactionDao, goalDao, spendDao);
    }

    private void addGoalAccountTransaction(Goal from, Account to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                           Dao<Goal, Integer> goalDao,  Dao<Account, Integer> accountDao) {
        from.addTransaction(to, sMoney, false, transactionDao, goalDao, accountDao);
    }

    private void addGoalGoalTransaction(Goal from, Goal to, String sMoney, Dao<Transaction, Integer> transactionDao,
                                        Dao<Goal, Integer> fromGoalDao,  Dao<Goal, Integer> toGoalDao) {
        from.addTransaction(to, sMoney, false, transactionDao, fromGoalDao, toGoalDao);
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


    public List<Transaction> loadTransaction(Dao<Transaction, Integer> transactionDao,
                                             Integer layoutId, Integer currentItemPosition) throws SQLException{
        QueryBuilder<Transaction, Integer> tQb = transactionDao.queryBuilder();
        Integer itemId;
        switch (layoutId) {
            case INCOME:
                itemId = this.inComeSources.get(currentItemPosition).getId();
                tQb.where().eq(Transaction.INCOME_FROM_FIELD_NAME, itemId)
                           .or()
                           .eq(Transaction.INCOME_TO_FIELD_NAME, itemId);
            break;
            case ACCOUNT:
                itemId = this.accounts.get(currentItemPosition).getId();
                tQb.where().eq(Transaction.ACCOUNT_FROM_FIELD_NAME, itemId)
                           .or()
                           .eq(Transaction.ACCOUNT_TO_FIELD_NAME, itemId);
            break;
            case SPEND:
                itemId = this.spends.get(currentItemPosition).getId();
                tQb.where().eq(Transaction.SPEND_FROM_FIELD_NAME, itemId)
                           .or()
                           .eq(Transaction.SPEND_TO_FIELD_NAME, itemId);
            break;
            case GOAL:
                itemId = this.goals.get(currentItemPosition).getId();
                tQb.where().eq(Transaction.GOAL_FROM_FIELD_NAME, itemId)
                           .or()
                           .eq(Transaction.GOAL_TO_FIELD_NAME, itemId);
            break;
        }
        tQb.orderBy(Transaction.DATE_FIELD_NAME, false);//false mean that the last transaction on top of the list

        List<Transaction> transactions = transactionDao.query(tQb.prepare());
        return transactions;
    }

    public static void  startTransaction(MoneyFlow from, MoneyFlow to, String fromItemType, String toItemType, String title,
                                         Dao<Transaction, Integer> transactionDao,
                                         Dao<InCome, Integer> inComeDao,  Dao<Account, Integer> accountDao,
                                         Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao) {
        if (fromItemType.equals("InCome") && toItemType.equals("Account")) {
            coinApplication.addInComeAccountTransaction((InCome) from, (Account) to, title, transactionDao, inComeDao, accountDao);
        } else if (fromItemType.equals("Account") && toItemType.equals("Spend")) {
            coinApplication.addAccountSpendTransaction((Account) from, (Spend) to, title, transactionDao, accountDao, spendDao);
        } else if (fromItemType.equals("Account") && toItemType.equals("Goal")) {
            coinApplication.addAccountGoalTransaction((Account) from, (Goal) to,title, transactionDao, accountDao, goalDao);
        } else if (fromItemType.equals("Goal") && toItemType.equals("Spend")) {
            coinApplication.addGoalSpendTransaction((Goal) from, (Spend) to, title, transactionDao, goalDao, spendDao);
        } else if (fromItemType.equals("Goal") && toItemType.equals("Goal")) {
            coinApplication.addGoalGoalTransaction((Goal) from, (Goal) to, title, transactionDao, goalDao, goalDao);
        } else if (fromItemType.equals("Account") && toItemType.equals("Account")) {
            coinApplication.addAccountAccountTransaction((Account) from, (Account) to, title, transactionDao, accountDao, accountDao);
        }else if (fromItemType.equals("Goal") && toItemType.equals("Account")) {
            coinApplication.addGoalAccountTransaction((Goal) from, (Account) to, title, transactionDao,goalDao, accountDao);
        }
    }
}
