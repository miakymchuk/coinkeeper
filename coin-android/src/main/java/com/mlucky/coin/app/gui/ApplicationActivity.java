package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.adapter.MoneyFlowBaseAdapter;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;
import java.util.List;

public class ApplicationActivity extends Activity {

    private final String DIALOG_ADD_KEY = "dialog_add_item";
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";
    private final String ITEM_TYPE_BUNDLE_KEY = "itemType";
    private final String ITEM_INDEX_BUNDLE_KEY = "itemIndex";

    private final String LOG_TAG = getClass().getSimpleName();

    private CoinApplication coinApplication = null;

    private DatabaseHelper databaseHelper = null;
    private MoneyFlowBaseAdapter mIncomeAdapter;
    private MoneyFlowBaseAdapter mAccountAdapter;
    private MoneyFlowBaseAdapter mSpendAdapter;
    private MoneyFlowBaseAdapter mGoalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);

        try {
            Dao<CoinApplication, Integer> coinDao = getHelper().getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
            Dao<InCome, Integer> incomeDao =  getHelper().getInComeDao();
            Dao<Account, Integer> accountDao =  getHelper().getAccountDao();
            Dao<Spend, Integer> spendDao =  getHelper().getSpendDao();
            Dao<Goal, Integer> goalDao =  getHelper().getGoalDao();
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
        if (id == R.id.action_settings) {
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
                //Set action on the add buttons
                Integer countOfLayoutItems = choosingCountOfItems(layoutId);
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

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(getApplicationContext());
        }
        return databaseHelper;
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
}
