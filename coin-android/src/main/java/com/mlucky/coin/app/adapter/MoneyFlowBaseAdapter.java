package com.mlucky.coin.app.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jess.ui.TwoWayGridView;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.gui.TransactionDialogFragment;
import com.mlucky.coin.app.impl.*;

import java.util.List;

/**
 * Created by m.iakymchuk on 19.11.2014.
 */
public class MoneyFlowBaseAdapter extends BaseAdapter {
    private Context coinContext;
    private List<? extends MoneyFlow> moneyFlowList;
    private LayoutInflater inflater;
    private int layoutId;

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
            TwoWayGridView gridView = (TwoWayGridView)((Activity) coinContext).findViewById(layoutId);
            view = inflater.inflate(R.layout.item, gridView, false);
            mHolder = new IconViewHolder(view);

            view.setTag(mHolder);
        } else {
            mHolder = (IconViewHolder)view.getTag();
        }

        if (position ==  moneyFlowList.size()) {
            Drawable mIconButton = coinContext.getResources().getDrawable(R.drawable.ic_button_add);

            int parentId = viewGroup.getId();
            Integer stringTitleAddButtonId = choosingStringOfAddButton(parentId);
            mHolder.build(R.string.set_title, mIconButton, stringTitleAddButtonId);

        } else {
            moneyFlowItem = moneyFlowList.get(position);
            Drawable mIconItem = coinContext.getResources().getDrawable(R.drawable.ic_launcher);
            mHolder.build(moneyFlowItem.getTitle().toString(), mIconItem, moneyFlowItem.getTotal().toString());
            setItemListeners(mHolder.getmIconView());
        }

        return view;
    }

    private void setItemListeners(final ImageView mIconView) {
        final String itemType = moneyFlowItem.getClass().getSimpleName();
        final int itemIndex = coinApplication.getMoneyFlowList(itemType).indexOf(moneyFlowItem);
        final String INCOME_VIEW_TAG = "income_index";
        mIconView.setTag(INCOME_VIEW_TAG);
        if (layoutId != R.id.spend_linear_layout) {
            mIconView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData dragData = ClipData.newPlainText((CharSequence) view.getTag(), Integer.toString(itemIndex));
                    ClipData.Item item = new ClipData.Item(itemType);
                    ClipData.Item itemId = new ClipData.Item(Integer.toString(layoutId));
                    View.DragShadowBuilder inComeShadow = new View.DragShadowBuilder(mIconView);
                    dragData.addItem(item);
                    dragData.addItem(itemId);

                    Bundle permissionParameter = new Bundle();
                    permissionParameter.putString("itemType", itemType);
                    permissionParameter.putInt("itemIndex", itemIndex);
                    view.startDrag(dragData, inComeShadow, permissionParameter, 0);
                    return false;
                }
            });
        }

        if (layoutId != R.id.income_linear_layout) {
            mIconView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    final int action = dragEvent.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            String dropFromItemType = ((Bundle) dragEvent.getLocalState()).getString("itemType");
                            int itemPosition = ((Bundle) dragEvent.getLocalState()).getInt("itemIndex");

                            return CoinApplication.isDragAllowed(dropFromItemType, itemType, itemPosition, itemIndex);
                        case DragEvent.ACTION_DRAG_ENTERED:

                            return true;
                        case DragEvent.ACTION_DROP:
                            int fromIndex = Integer.parseInt((String) dragEvent.getClipData().getItemAt(0).getText());
                            String fromItemType = dragEvent.getClipData().getItemAt(1).getText().toString();
                            int fromLayoutId = Integer.parseInt((String) dragEvent.getClipData().getItemAt(2).getText());
                            Bundle bundle = new Bundle();
                            bundle.putInt("fromIndex", fromIndex);
                            bundle.putInt("toIndex", itemIndex);
                            bundle.putInt("fromLayoutId", fromLayoutId);
                            bundle.putInt("toLayoutId", layoutId);
                            bundle.putString("fromItemType", fromItemType);
                            bundle.putString("toItemType", itemType);

                            TransactionDialogFragment transactionDialog = new TransactionDialogFragment();
                            transactionDialog.setArguments(bundle);

                            FragmentManager fr = ((Activity) coinContext).getFragmentManager();
                            transactionDialog.show(fr, "dialog_transaction");

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


}

