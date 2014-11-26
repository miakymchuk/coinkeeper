package com.mlucky.coin.app.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.gui.dialog.TransactionDialogFragment;
import com.mlucky.coin.app.impl.*;

import java.util.List;

/**
 * Created by m.iakymchuk on 19.11.2014.
 */
public class MoneyFlowBaseAdapter extends BaseAdapter {
    private final String ITEM_TYPE_BUNDLE_KEY = "itemType";
    private final String ITEM_INDEX_BUNDLE_KEY = "itemIndex";
    private final String FROM_ITEM_INDEX_BUNDLE_KEY = "fromIndex";
    private final String TO_ITEM_INDEX_BUNDLE_KEY = "toIndex";
    private final String FROM_LAYOUT_ID_BUNDLE_KEY = "fromLayoutId";
    private final String TO_LAYOUT_ID_BUNDLE_KEY = "toLayoutId";
    private final String FROM_ITEM_TYPE_BUNDLE_KEY = "fromItemType";
    private final String TO_ITEM_TYPE_BUNDLE_KEY = "toItemType";

    private final String DIALOG_TRANSACTION_VIEW_KEY = "dialog_transaction";

    private Context coinContext;
    private List<? extends MoneyFlow> moneyFlowList;
    private LayoutInflater inflater;
    private int layoutId;
    private boolean isEditMode = false;
    private MoneyFlow moneyFlowItem = null;
    private CoinApplication coinApplication = null;

    public MoneyFlowBaseAdapter(Context coinContext, CoinApplication coinApplication, List<? extends MoneyFlow> moneyFlowList, int layoutId) {
        this.coinContext = coinContext;
        this.moneyFlowList = moneyFlowList;
        this.layoutId = layoutId;
        this.inflater = LayoutInflater.from(coinContext);
        this.coinApplication = coinApplication;
    }

    @Override
    public int getCount() {
        return moneyFlowList.size()+1;
    }// +1 because we need add button to the end of List

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        IconViewHolder mHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.item, null);
            mHolder = new IconViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (IconViewHolder)view.getTag();
        }

        if (position ==  moneyFlowList.size()) {
            Drawable mIconButton = coinContext.getResources().getDrawable(R.drawable.ic_btn_add);

            int parentId = viewGroup.getId();
            Integer stringTitleAddButtonId = choosingStringOfAddButton(parentId);
            mHolder.build(R.string.set_title, mIconButton, stringTitleAddButtonId);

        } else {
            moneyFlowItem = moneyFlowList.get(position);
            Drawable mIconItem = coinContext.getResources().getDrawable(R.drawable.ic_dollar);
            mHolder.build(moneyFlowItem.getTitle().toString(), mIconItem, moneyFlowItem.getTotal().toString(), isEditMode);
            setOnDragItemListeners(mHolder.getmIconView());
        }

        return view;
    }

    private void setOnDragItemListeners(final ImageView mIconView) {
        final String itemType = moneyFlowItem.getClass().getSimpleName();
        final int itemIndex = coinApplication.getMoneyFlowList(itemType).indexOf(moneyFlowItem);

        if (layoutId != R.id.income_linear_layout) {
            mIconView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    final int action = dragEvent.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            String dropFromItemType = ((Bundle) dragEvent.getLocalState()).getString(ITEM_TYPE_BUNDLE_KEY);
                            int itemPosition = ((Bundle) dragEvent.getLocalState()).getInt(ITEM_INDEX_BUNDLE_KEY);

                            return CoinApplication.isDragAllowed(dropFromItemType, itemType, itemPosition, itemIndex);
                        case DragEvent.ACTION_DRAG_ENTERED:

                            return true;
                        case DragEvent.ACTION_DROP:
                            int fromIndex = Integer.parseInt((String) dragEvent.getClipData().getItemAt(0).getText());
                            String fromItemType = dragEvent.getClipData().getItemAt(1).getText().toString();
                            int fromLayoutId = Integer.parseInt((String) dragEvent.getClipData().getItemAt(2).getText());
                            Bundle bundle = new Bundle();
                            bundle.putInt(FROM_ITEM_INDEX_BUNDLE_KEY, fromIndex);
                            bundle.putInt(TO_ITEM_INDEX_BUNDLE_KEY, itemIndex);
                            bundle.putInt(FROM_LAYOUT_ID_BUNDLE_KEY, fromLayoutId);
                            bundle.putInt(TO_LAYOUT_ID_BUNDLE_KEY, layoutId);
                            bundle.putString(FROM_ITEM_TYPE_BUNDLE_KEY, fromItemType);
                            bundle.putString(TO_ITEM_TYPE_BUNDLE_KEY, itemType);

                            TransactionDialogFragment transactionDialog = new TransactionDialogFragment();
                            transactionDialog.setArguments(bundle);

                            FragmentManager fr = ((Activity) coinContext).getFragmentManager();
                            transactionDialog.show(fr, DIALOG_TRANSACTION_VIEW_KEY);

                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private Integer choosingStringOfAddButton(int parentId) {
        Integer stringTitleAddButtonId = null;
        switch (parentId) {
            case R.id.income_linear_layout:
                stringTitleAddButtonId = R.string.income_sources;
                break;
            case R.id.account_linear_layout:
                stringTitleAddButtonId = R.string.account;
                break;
            case R.id.spend_linear_layout:
                stringTitleAddButtonId = R.string.spend;
                break;
            case R.id.goal_linear_layout:
                stringTitleAddButtonId = R.string.goal;
                break;
        }
        return stringTitleAddButtonId;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }
}

