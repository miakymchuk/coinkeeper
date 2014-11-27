package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.adapter.MoneyFlowBaseAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.gui.dialog.AddItemDialogFragment;
import com.mlucky.coin.app.gui.dialog.RemoveItemDialogFragment;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationActivity extends Activity {

    private final String DIALOG_ADD_KEY = "dialog_add_item";
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";
    private final String ITEM_TYPE_BUNDLE_KEY = "itemType";
    private final String ITEM_INDEX_BUNDLE_KEY = "itemIndex";

    private final String IS_REMOVE_TRANSACTION_BUNDLE_KEY = "isRemoveTransaction";

    private final String LOG_TAG = getClass().getSimpleName();

    private CoinApplication coinApplication = null;

    private static DatabaseHelper databaseHelper;
    private MoneyFlowBaseAdapter mIncomeAdapter;
    private MoneyFlowBaseAdapter mAccountAdapter;
    private MoneyFlowBaseAdapter mSpendAdapter;
    private MoneyFlowBaseAdapter mGoalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getDataBaseHelper(getApplicationContext());
        setContentView(R.layout.activity_application);

        try {
            Dao<CoinApplication, Integer> coinDao = databaseHelper.getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<InCome, Integer> incomeDao =  databaseHelper.getInComeDao();
            Dao<Account, Integer> accountDao =  databaseHelper.getAccountDao();
            Dao<Spend, Integer> spendDao =  databaseHelper.getSpendDao();
            Dao<Goal, Integer> goalDao =  databaseHelper.getGoalDao();
            coinApplication.loadEntityFromDatabase(incomeDao, accountDao, spendDao, goalDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mIncomeAdapter = setMoneyFlowBaseAdapter(coinApplication.getInComeSources(), R.id.income_linear_layout);
        mAccountAdapter = setMoneyFlowBaseAdapter(coinApplication.getAccounts(), R.id.account_linear_layout);
        mSpendAdapter = setMoneyFlowBaseAdapter(coinApplication.getSpends(), R.id.spend_linear_layout);
        mGoalAdapter = setMoneyFlowBaseAdapter(coinApplication.getGoals(), R.id.goal_linear_layout);
    }

    @Override
    public void onBackPressed() {
        if(!this.mIncomeAdapter.isEditMode()) {
            super.onBackPressed();
        }else {
            setEditMode(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
            return true;
            case R.id.action_edit_mode:
                setEditMode(true);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddButtonDialog(int layoutId) {
        DialogFragment addDialog = new AddItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID_BUNDLE_KEY, layoutId);
        addDialog.setArguments(bundle);
        addDialog.show(getFragmentManager(), DIALOG_ADD_KEY);
    }

    private MoneyFlowBaseAdapter setMoneyFlowBaseAdapter(final List<? extends MoneyFlow> mMoneyFlowList, final int layoutId) {
        TwoWayGridView mInComeGridView = (TwoWayGridView) findViewById(layoutId);
        MoneyFlowBaseAdapter mMoneyFlowAdaper = new MoneyFlowBaseAdapter(this, this.coinApplication,
                mMoneyFlowList, layoutId);
        mInComeGridView.setAdapter(mMoneyFlowAdaper);
        mInComeGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                Integer countOfLayoutItems = choosingCountOfItems(layoutId);

                if(mIncomeAdapter.isEditMode()){
                    if (position == countOfLayoutItems) return;

                    DialogFragment removeDialog = new RemoveItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(LAYOUT_ID_BUNDLE_KEY, layoutId);
                    bundle.putInt(ITEM_POSITION_BUNDLE_KEY, position);
                    removeDialog.setArguments(bundle);
                    removeDialog.show(getFragmentManager(), null);

                    return;
                }

                //Set action on the add buttons
                if (position == countOfLayoutItems) {
                    showAddButtonDialog(layoutId);
                } else {
                    //Set action on a other items
                    Intent transactionIntent = new Intent(ApplicationActivity.this, TransactionListActivity.class);
                    transactionIntent.putExtra(LAYOUT_ID_BUNDLE_KEY, layoutId);
                    transactionIntent.putExtra(ITEM_POSITION_BUNDLE_KEY, position);
                    startActivity(transactionIntent);

                }
            }
        });

        final String ITEM_VIEW_TAG = "income_index";
        mInComeGridView.setTag(ITEM_VIEW_TAG);
        mInComeGridView.setOnItemLongClickListener(new TwoWayAdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                Integer countOfLayoutItems = choosingCountOfItems(layoutId);
                if (position != countOfLayoutItems) {
                    MoneyFlow moneyFlowItem = mMoneyFlowList.get(position);
                    final String itemType = moneyFlowItem.getClass().getSimpleName();

                    if (layoutId != R.id.spend_linear_layout) {

                        ClipData dragData = ClipData.newPlainText((CharSequence) parent.getTag(), Integer.toString(position));
                        ClipData.Item item = new ClipData.Item(itemType);
                        ClipData.Item itemId = new ClipData.Item(Integer.toString(layoutId));
                        View.DragShadowBuilder inComeShadow = new View.DragShadowBuilder(view);
                        dragData.addItem(item);
                        dragData.addItem(itemId);

                        Bundle permissionParameter = new Bundle();
                        permissionParameter.putString(ITEM_TYPE_BUNDLE_KEY, itemType);
                        permissionParameter.putInt(ITEM_INDEX_BUNDLE_KEY, position);
                        view.startDrag(dragData, inComeShadow, permissionParameter, 0);
                    }
                }
                return false;
            }
        });
        return mMoneyFlowAdaper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public MoneyFlowBaseAdapter getmIncomeAdapter() {
        return mIncomeAdapter;
    }

    public MoneyFlowBaseAdapter getmAccountAdapter() {
        return mAccountAdapter;
    }

    public MoneyFlowBaseAdapter getmSpendAdapter() {
        return mSpendAdapter;
    }

    public MoneyFlowBaseAdapter getmGoalAdapter() {
        return mGoalAdapter;
    }

    private Integer choosingCountOfItems(int parentId) {
        Integer countOfLayoutItems = null;
        switch (parentId) {
            case R.id.income_linear_layout:
                countOfLayoutItems = coinApplication.getInComeSources().size();
                break;
            case R.id.account_linear_layout:
                countOfLayoutItems = coinApplication.getAccounts().size();
                break;
            case R.id.spend_linear_layout:
                countOfLayoutItems = coinApplication.getSpends().size();
                break;
            case R.id.goal_linear_layout:
                countOfLayoutItems = coinApplication.getGoals().size();
                break;
        }
        return countOfLayoutItems;
    }

    private void setEditMode(boolean isEditMode) {
        this.mIncomeAdapter.setEditMode(isEditMode);
        this.mAccountAdapter.setEditMode(isEditMode);
        this.mSpendAdapter.setEditMode(isEditMode);
        this.mGoalAdapter.setEditMode(isEditMode);

        this.mIncomeAdapter.notifyDataSetChanged();
        this.mAccountAdapter.notifyDataSetChanged();
        this.mSpendAdapter.notifyDataSetChanged();
        this.mGoalAdapter.notifyDataSetChanged();
    }
    public void addItem(String titleItem, int layoutID) {
        this.addOrRemoveItem(titleItem, layoutID, false, false, 0);
    }

    public void removeItem(int layoutID, int positionItem, boolean isRemoveTransaction) {
        this.addOrRemoveItem(null, layoutID, true, isRemoveTransaction, positionItem);
    }

    private void addOrRemoveItem(String titleItem, int layoutID,  boolean isRemove,
                                        boolean isRemoveTransaction, int positionItem) {
        try {
            MoneyFlowBaseAdapter mCommonAdaper = null;
            Dao<CoinApplication, Integer> coinDao = databaseHelper.getCoinApplicationDao();
            CoinApplication coinApplication = CoinApplication.getCoinApplication(coinDao);


            Dao<InCome, Integer> incomeDao =  databaseHelper.getInComeDao();
            Dao<Account, Integer> accountDao =  databaseHelper.getAccountDao();
            Dao<Spend, Integer> spendDao =  databaseHelper.getSpendDao();
            Dao<Goal, Integer> goalDao = databaseHelper.getGoalDao();
            Dao<Transaction, Integer> transactionDao = databaseHelper.getTransactionDao();

            TwoWayGridView currentGridView = null;
            CoinApplication.ItemType itemType = null;
            switch (layoutID) {
                case R.id.income_linear_layout:
                    itemType = CoinApplication.ItemType.InCome;
                    mCommonAdaper = this.getmIncomeAdapter();
                    currentGridView = (TwoWayGridView)this.findViewById(R.id.income_linear_layout);
                    break;
                case R.id.account_linear_layout:
                    itemType = CoinApplication.ItemType.Account;
                    mCommonAdaper = this.getmAccountAdapter();
                    currentGridView = (TwoWayGridView)this.findViewById(R.id.account_linear_layout);
                    break;
                case R.id.spend_linear_layout:
                    itemType = CoinApplication.ItemType.Spend;
                    mCommonAdaper = this.getmSpendAdapter();
                    currentGridView = (TwoWayGridView)this.findViewById(R.id.spend_linear_layout);
                    break;
                case R.id.goal_linear_layout:
                    itemType = CoinApplication.ItemType.Goal;
                    mCommonAdaper = this.getmGoalAdapter();
                    currentGridView = (TwoWayGridView)this.findViewById(R.id.goal_linear_layout);
                    break;
            }
            if (isRemove) {
                coinApplication.removeItem(itemType, positionItem,  incomeDao, accountDao, spendDao, goalDao,
                        isRemoveTransaction, transactionDao);
            } else {
                coinApplication.addItem(itemType, titleItem, incomeDao, accountDao, spendDao, goalDao);
            }

            mCommonAdaper.notifyDataSetChanged();
            currentGridView.setAdapter(mCommonAdaper);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
