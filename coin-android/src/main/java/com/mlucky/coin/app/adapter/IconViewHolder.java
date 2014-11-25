package com.mlucky.coin.app.adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mlucky.coin.app.gui.AddItemDialogFragment;
import com.mlucky.coin.app.gui.R;

/**
 * Created by m.iakymchuk on 21.11.2014.
 */
public class IconViewHolder {

    private TextView mTitleView;
    private ImageView mIconView;
    private TextView mTotalAmountView;

    public IconViewHolder(View view) {
        this.mTitleView = (TextView)view.findViewById(R.id.item_title);
        this.mTotalAmountView = (TextView)view.findViewById(R.id.item_total);
        this.mIconView = (ImageView)view.findViewById(R.id.item_image);
    }

    void build(String mTitleView, Drawable mIcon, String mTotalAmountView) {
        this.mTitleView.setText(mTitleView);
        this.mIconView.setBackground(mIcon);
        this.mTotalAmountView.setText(mTotalAmountView);
    }

    void build(int mTitleView, Drawable mIcon, int mTotalAmountView) {
        this.mTitleView.setText(mTitleView);
        this.mIconView.setBackground(mIcon);
        this.mTotalAmountView.setText(mTotalAmountView);
    }

    public ImageView getmIconView() {
        return mIconView;
    }
}
