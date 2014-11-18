package com.mlucky.coin.app.gui;

import android.app.*;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mlucky.coin.app.db.DatabaseHelper;
import com.mlucky.coin.app.impl.*;

import java.sql.SQLException;
import java.util.List;

public class ApplicationActivity extends Activity {
    CoinApplication coinApplication = CoinApplication.getCoinApplication();
    private final String LOG_TAG = getClass().getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            //DatabaseHelper myDatabase = getHelper();

            setContentView(R.layout.activity_application);
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

    public void  OnAddButtonClick(View view) {
        DialogFragment addDialog = new AddItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", view.getId());
        addDialog.setArguments(bundle);
        addDialog.show(getFragmentManager(), "dialog_add_item");
    }



}
