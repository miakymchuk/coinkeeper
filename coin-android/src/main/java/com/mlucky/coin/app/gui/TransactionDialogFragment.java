package com.mlucky.coin.app.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mlucky.coin.app.impl.*;

import java.util.Objects;

/**
 * Created by m.iakymchuk on 13.11.2014.
 */
public class TransactionDialogFragment extends DialogFragment {
    private final Object attachingActivityLock = new Object();

    private boolean syncVariable = false;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final CoinApplication coinApplication = CoinApplication.getCoinApplication();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        LinearLayout dialogTransactionLayout = (LinearLayout)inflater.inflate(R.layout.dialog_transaction, null);
        final EditText transactionText = (EditText)dialogTransactionLayout.findViewById(R.id.transaction_amount);
        builder.setTitle(R.string.dialog_title_transaction).setView(dialogTransactionLayout);

        builder.setPositiveButton(R.string.set_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int fromIndex = getArguments().getInt("fromIndex");
                int toIndex = getArguments().getInt("toIndex");
                String fromItemType = getArguments().getString("fromItemType");
                String toItemType = getArguments().getString("toItemType");
                //Class.forName(fromItemType).newInstance();
                Object from = coinApplication.getMoneyFlowList(fromItemType).get(fromIndex);
                Object to = coinApplication.getMoneyFlowList(toItemType).get(toIndex);

                if (fromItemType.equals("InCome") && toItemType.equals("Account")) {
                    coinApplication.addInComeAccountTransaction((InCome) from, (Account) to, transactionText.getText().toString());
                } else if (fromItemType.equals("Account") && toItemType.equals("Spend")) {
                    coinApplication.addAccountSpendTransaction((Account) from, (Spend) to, transactionText.getText().toString());
                } else if (fromItemType.equals("Account") && toItemType.equals("Goal")) {
                    coinApplication.addAccountGoalTransaction((Account) from, (Goal) to, transactionText.getText().toString());
                } else if (fromItemType.equals("Goal") && toItemType.equals("Spend")) {
                    coinApplication.addGoalSpendTransaction((Goal) from, (Spend) to, transactionText.getText().toString());
                }
                //Activity coinActivity = getActivity();
                int fromLayoutId = getArguments().getInt("fromLayoutId");
                int toLayoutId = getArguments().getInt("toLayoutId");
                setTotal((MoneyFlow) from, fromLayoutId, fromIndex);
                setTotal((MoneyFlow) to, toLayoutId, toIndex);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().cancel();
            }
        });

        return builder.create();
    }

    private void setTotal(MoneyFlow moneyFlow,int fromLayoutId, int fromIndex ) {

        LinearLayout fromLinearLayout = (LinearLayout)getActivity().findViewById(fromLayoutId);
        LinearLayout  fromLinearLayoutItem = (LinearLayout)fromLinearLayout.getChildAt(fromIndex + 1);

        TextView fromTextView = (TextView)fromLinearLayoutItem.findViewById(R.id.item_total);
        fromTextView.setText(moneyFlow.getTotal().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
