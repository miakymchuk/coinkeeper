package com.mlucky.coin.app.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.mlucky.coin.app.gui.R;
import com.mlucky.coin.app.impl.MoneyFlow;

import java.util.List;

/**
 * Created by m.iakymchuk on 03.12.2014.
 */
public class TransactionEditModeBaseAdapter extends BaseAdapter {
    Context mContext;
    List<? extends MoneyFlow> items;
    LayoutInflater inflater;
    Integer itemCheckedId;
    boolean isChecked = false;

    public TransactionEditModeBaseAdapter(Context mContext,Integer itemCheckedId, List<? extends MoneyFlow> items) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.items = items;
        this.itemCheckedId = itemCheckedId;
    }

    @Override
    public int getCount() {
        return this.items == null ? 0 : this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHolder mHolder;
        if (view == null) {
           view = inflater.inflate(R.layout.item_edit_mode, null);
            mHolder = new ItemHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (ItemHolder)view.getTag();
        }
        int dbId = items.get(i).getId();
        isChecked = itemCheckedId.equals(dbId) ? true: false;

        String title = items.get(i).getTitle();
        mHolder.build(viewGroup, title);

        return view;
    }

    public boolean isChecked() {
        return isChecked;
    }

    private  class  ItemHolder {
        RadioButton item;
        private ItemHolder(View view) {
            this.item = (RadioButton)view;
        }

        public void build(ViewGroup viewGroup, String text) {
            this.item.setText(text);
        }
    }
}


