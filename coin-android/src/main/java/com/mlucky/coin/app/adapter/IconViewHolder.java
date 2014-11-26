package com.mlucky.coin.app.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mlucky.coin.app.gui.R;

/**
 * Created by m.iakymchuk on 21.11.2014.
 */
public class IconViewHolder {

    private TextView mTitleView;
    private ImageView mIconView;
    private TextView mTotalAmountView;
    private ImageView mRemoveIcon;

    public IconViewHolder(View view) {
        this.mTitleView = (TextView)view.findViewById(R.id.item_title);
        this.mTotalAmountView = (TextView)view.findViewById(R.id.item_total);
        this.mIconView = (ImageView)view.findViewById(R.id.item_image);
        this.mRemoveIcon =  (ImageView)view.findViewById(R.id.ic_btn_remove_item);
    }

    void build(String mTitleView, Drawable mIcon, String mTotalAmountView, boolean isEditMode) {
        this.mTitleView.setText(mTitleView);
        this.mIconView.setBackground(mIcon);
        this.mTotalAmountView.setText(mTotalAmountView);
        setEditMode(isEditMode);
    }

    void build(int mTitleView, Drawable mIcon, int mTotalAmountView) {
        this.mTitleView.setText(mTitleView);
        this.mIconView.setBackground(mIcon);
        this.mTotalAmountView.setText(mTotalAmountView);
        setEditMode(false);

    }

    public ImageView getmIconView() {
        return mIconView;
    }

    private void setEditMode(boolean isEditMode) {
        if (isEditMode) {
            mRemoveIcon.setVisibility(View.VISIBLE);
        } else {
            mRemoveIcon.setVisibility(View.GONE);
        }
    }
}
