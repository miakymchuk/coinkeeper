package com.mlucky.coin.app.impl;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
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

    public enum ItemType { InCome, Account, Spend, Goal}


    @DatabaseField(id = true)
    private int id = 1;
    private static CoinApplication coinApplication = null;

    @DatabaseField
    private static final String localCurrency = "UAH";

    @DatabaseField(dataType = DataType.DATE)
    private Date installDate;

    @DatabaseField(dataType = DataType.DATE)
    private Date currentDate;

    List<InCome> inComeSources =  new ArrayList<InCome>();

    List<Account> accounts = new ArrayList<Account>();

    List<Spend> spends = new ArrayList<Spend>();

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

    public void addItem(ItemType itemType, String title, Dao<InCome, Integer> incomeDao,
                   Dao<Account, Integer> accountDao, Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao) throws SQLException {
        switch(itemType) {
            case InCome:
                InCome income = new InCome(title, localCurrency);
                income.setDao(incomeDao);
                income.create();
                this.inComeSources.add(income);
                break;
            case Account:
                Account account = new Account(title, localCurrency);
                account.setDao(accountDao);
                account.create();
                this.accounts.add(account);
                break;
            case Spend:
                Spend spend = new Spend(title, localCurrency);
                spend.setDao(spendDao);
                spend.create();
                this.spends.add(spend);
                break;
            case Goal:
                Goal goal = new Goal(title, localCurrency);
                goal.setDao(goalDao);
                goal.create();
                this.goals.add(goal);
                break;
        }
    }

    public void removeItem(ItemType itemType, int itemPosition,
                               Dao<InCome, Integer> inComeDao, Dao<Account, Integer> accountDao,
                               Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao,
                               boolean isRemoveTransaction, Dao<Transaction, Integer> transactionDao) throws  SQLException{
        if (isRemoveTransaction) {
            removeTransaction(inComeDao, accountDao, spendDao, goalDao, transactionDao, itemType, itemPosition);
        }
        switch(itemType) {
            case InCome:
                InCome inCome = this.inComeSources.get(itemPosition);
                inComeDao.deleteById(inCome.getId());
                this.inComeSources.remove(itemPosition);
                break;
            case Account:
                Account account = this.accounts.get(itemPosition);
                accountDao.deleteById(account.getId());
                this.accounts.remove(itemPosition);
                break;
            case Spend:
                Spend spend = this.spends.get(itemPosition);
                spendDao.deleteById(spend.getId());
                this.spends.remove(itemPosition);
                break;
            case Goal:
                Goal item = this.goals.get(itemPosition);
                goalDao.deleteById(item.getId());
                this.goals.remove(itemPosition);
                break;
        }
    }

    private void removeTransaction(Dao<InCome, Integer> inComeDao, Dao<Account, Integer> accountDao,
                                   Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao,
                                   Dao<Transaction, Integer> transactionDao, ItemType itemType,
                                   int itemPosition) throws SQLException {
            List<Transaction>  transactions = loadTransaction(transactionDao, itemType, itemPosition );

            for (Transaction transaction: transactions) {

                Integer itemFromId = transaction.getFromId();
                Integer itemToId = transaction.getToId();

                ItemType itemFromType = transaction.getFromType();
                ItemType itemToType = transaction.getToType();
                boolean isRollbackMoneyOperation;
                if (itemFromType.equals(ItemType.InCome) && itemToType.equals(ItemType.Account))  isRollbackMoneyOperation = false;
                else isRollbackMoneyOperation = true;
                this.queryItemById(transaction, inComeDao, accountDao, spendDao, goalDao, itemFromType, itemFromId, isRollbackMoneyOperation);

                this.queryItemById(transaction, inComeDao, accountDao, spendDao, goalDao, itemToType, itemToId, false);
                transactionDao.deleteById(transaction.getId());

            }
    }

    private void queryItemById(Transaction transaction , Dao<InCome, Integer> inComeDao, Dao<Account, Integer> accountDao,
                                    Dao<Spend, Integer> spendDao, Dao<Goal, Integer> goalDao,
                                    ItemType itemFromType, int id, boolean isRollbackMoneyOperation) throws SQLException{
        MoneyFlow item = null;
        Money money = transaction.getMoneyCount();
        int itemIndex;
        switch (itemFromType) {
            case InCome:
                item = inComeDao.queryForId(id);

                itemIndex = this.inComeSources.indexOf(item);
                item.decreaseTotal(money);
                this.inComeSources.get(itemIndex).decreaseTotal(money);
                break;
            case Account:
                item = accountDao.queryForId(id);
                itemIndex = this.accounts.indexOf(item);
                if (isRollbackMoneyOperation) {
                    item.increaseTotal(money);
                    this.accounts.get(itemIndex).increaseTotal(money);
                } else {
                    item.decreaseTotal(money);
                    this.accounts.get(itemIndex).decreaseTotal(money);
                }
                break;
            case Spend:
                item = spendDao.queryForId(id);
                itemIndex = this.spends.indexOf(item);
                item.decreaseTotal(money);
                this.spends.get(itemIndex).decreaseTotal(money);
                break;
            case Goal:
                item = goalDao.queryForId(id);
                itemIndex = this.goals.indexOf(item);
                if (isRollbackMoneyOperation) {
                    item.increaseTotal(money);
                    this.goals.get(itemIndex).increaseTotal(money);
                } else {
                    item.decreaseTotal(money);
                    this.goals.get(itemIndex).decreaseTotal(money);
                }
                break;
        }

        if (item.update() != 1) {
            throw new SQLException("Failure update entity: " + item.getClass().getSimpleName() +
                    " id: " +  item.getId());
        }
        item.refresh();
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

    public List<? extends MoneyFlow> getMoneyFlowList(ItemType itemType) {
        switch (itemType) {
            case InCome:
                return inComeSources;
            case Account:
                return accounts;
            case Spend:
                return spends;
            case Goal:
                return goals;
            default: return null;
        }
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
                                             ItemType itemType, Integer currentItemPosition) throws SQLException{

        Integer itemId = null;
        switch (itemType) {
            case InCome:
                itemId = this.inComeSources.get(currentItemPosition).getId();
            break;
            case Account:
                itemId = this.accounts.get(currentItemPosition).getId();
            break;
            case Spend:
                itemId = this.spends.get(currentItemPosition).getId();
            break;
            case Goal:
                itemId = this.goals.get(currentItemPosition).getId();
            break;
        }

        QueryBuilder<Transaction, Integer> tQb = transactionDao.queryBuilder();
        Where<Transaction, Integer> where = tQb.where();
        where.or(where.and( where.eq(Transaction.FROM_TYPE_FIELD_NAME, itemType),
                        where.eq(Transaction.FROM_ID_FIELD_NAME, itemId)),
                where.and( where.eq(Transaction.TO_TYPE_FIELD_NAME, itemType),
                        where.eq(Transaction.TO_ID_FIELD_NAME, itemId)));
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
