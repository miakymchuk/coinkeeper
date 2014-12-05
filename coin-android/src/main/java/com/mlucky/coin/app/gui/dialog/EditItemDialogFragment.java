package com.mlucky.coin.app.gui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.impl.CoinApplication;
import com.mlucky.coin.app.impl.MoneyFlow;
import org.joda.money.Money;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by m.iakymchuk on 05.12.2014.
 */
public class EditItemDialogFragment extends DialogFragment {
    private final String LAYOUT_ID_BUNDLE_KEY = "layoutId";
    private final String ITEM_POSITION_BUNDLE_KEY = "clickedCurrentItemPosition";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity mApplicationActivity = getActivity();
        final DatabaseHelper databaseHelper = DatabaseHelper.getDataBaseHelper(mApplicationActivity.getApplicationContext());
        CoinApplication coinApplication = null;
        Dao<CoinApplication, Integer> coinDao;
        try {
            coinDao = databaseHelper.getCoinApplicationDao();
            coinApplication = CoinApplication.getCoinApplication(coinDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final int layoutId = getArguments().getInt(LAYOUT_ID_BUNDLE_KEY);
        final int itemIndex = getArguments().getInt(ITEM_POSITION_BUNDLE_KEY);

        CoinApplication.ItemType itemType = chooseDialogItemType(layoutId);
        List<? extends MoneyFlow> items = coinApplication.getMoneyFlowList(itemType);
        final MoneyFlow currentItem = items.get(itemIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(mApplicationActivity);
        LayoutInflater inflater = mApplicationActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);

        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editText_title);
        final EditText editTextTotal = (EditText) dialogView.findViewById(R.id.editText_total);
        editTextTitle.setText(currentItem.getTitle());
        editTextTotal.setText(currentItem.getTotal().getAmount().toString());
        builder.setTitle(chooseDialogTitle(layoutId));
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.edit_finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String title = editTextTitle.getText().toString();
                String totalAmount = editTextTotal.getText().toString();
                Money money = Money.parse(CoinApplication.getLocalCurrency() + " " + totalAmount);
                currentItem.setTitle(title);
                currentItem.setTotal(money);
                try {
                    if (currentItem.update() != 1) {
                        throw new SQLException("Failed update item id: " + currentItem.getId().toString()
                                + " type: " + currentItem.getItemType().toString());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().cancel();
            }
        });

        return  builder.create();
    }

    private Integer chooseDialogTitle(int layoutId) {
        switch (layoutId) {
            case R.id.income_linear_layout:
                return R.string.edit_income_sources;
            case R.id.account_linear_layout:
                return R.string.edit_account;
            case R.id.spend_linear_layout:
                return R.string.edit_expenses;
            case R.id.goal_linear_layout:
                return R.string.edit_goals;
        }
        return null;
    }

    private CoinApplication.ItemType chooseDialogItemType(int layoutId) {
        switch (layoutId) {
            case R.id.income_linear_layout:
                return CoinApplication.ItemType.InCome;
            case R.id.account_linear_layout:
                return CoinApplication.ItemType.Account;
            case R.id.spend_linear_layout:
                return CoinApplication.ItemType.Spend;
            case R.id.goal_linear_layout:
                return CoinApplication.ItemType.Goal;
        }
        return null;
    }
}
