package com.mlucky.coin.app.db;

/**
 * Created by m.iakymchuk on 17.11.2014.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "coinkeeper.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the InCome table
    private Dao<InCome, Integer> incomeDao = null;
    private Dao<Account, Integer> accountDao = null;
    private Dao<Spend, Integer> spendDao = null;
    private Dao<Goal, Integer> goalDao = null;
    private Dao<CoinApplication, Integer> coinApplicationDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, CoinApplication.class);
            TableUtils.createTable(connectionSource, InCome.class);
            TableUtils.createTable(connectionSource, Account.class);
            TableUtils.createTable(connectionSource, Spend.class);
            TableUtils.createTable(connectionSource, Goal.class);
            TableUtils.createTable(connectionSource, Transaction.class);
          //  TableUtils.createTable(connectionSource, MoneyFlow.class);
            TableUtils.createTable(connectionSource, Budget.class);

           /* // here we try inserting data in the on-create as a test
            Dao<InCome, Integer> dao = getInComeDao();
            long millis = System.currentTimeMillis();
            // create some entries in the onCreate
            InCome simple = new InCome("Salary", "UAH" );
            dao.create(simple);
            simple = new InCome("Taxi", "UAH");
            dao.create(simple);*/
            Log.i(DatabaseHelper.class.getName(), "create database finished" );
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, CoinApplication.class, true);
            TableUtils.dropTable(connectionSource, InCome.class, true);
            TableUtils.dropTable(connectionSource, Account.class, true);
            TableUtils.dropTable(connectionSource, Spend.class, true);
            TableUtils.dropTable(connectionSource, Goal.class, true);
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            TableUtils.dropTable(connectionSource, Budget.class, true);
           // TableUtils.dropTable(connectionSource, MoneyFlow.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our InCome class. It will create it or just give the cached
     * value.
     */
    public Dao<InCome, Integer> getInComeDao() throws SQLException {
        if (incomeDao == null) {
            incomeDao = getDao(InCome.class);
        }
        return incomeDao;
    }

    public Dao<Account, Integer> getAccountDao() throws SQLException {
        if (accountDao == null) {
            accountDao = getDao(Account.class);
        }
        return accountDao;
    }

    public Dao<Spend, Integer> getSpendDao() throws SQLException {
        if (spendDao == null) {
            spendDao = getDao(Spend.class);
        }
        return spendDao;
    }

    public Dao<Goal, Integer> getGoalDao() throws SQLException {
        if (goalDao == null) {
            goalDao = getDao(Goal.class);
        }
        return goalDao;
    }

    public Dao<CoinApplication, Integer> getCoinApplicationDao() throws SQLException {
        if (coinApplicationDao == null) {
            coinApplicationDao = getDao(CoinApplication.class);
        }
        return coinApplicationDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        incomeDao = null;
        accountDao = null;
        spendDao = null;
        goalDao = null;
        coinApplicationDao = null;
    }
}