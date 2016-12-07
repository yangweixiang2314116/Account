package com.ywxzhuangxiula.account;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ywxzhuangxiula.module.Account;
import com.ywxzhuangxiula.module.DialogHelp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/16.
 */
public class AccountGuideBudgetFragment extends Fragment {

    private WheelView wheel = null;
    private TextView start = null;
    private Resources mResources = null;
    private Activity mParent = null;
    private  ArrayList<String> mWheelListDataSource = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_guide_budget, container, false);

        wheel = (WheelView)view. findViewById(R.id.wheel_view_wv);

        Log.d(Constants.TAG, "onCreateView:  load wheel view !!!" );

        mParent =  getActivity();
        mResources = this.getResources();
        TypedArray infoItems = mResources.obtainTypedArray(R.array.guide_chose_budget_text);

        String  budgetselected = AccountCommonUtil.GetGudieBudget(mParent);
        int  focusIndex = 0;
        mWheelListDataSource.clear();
        for(int i=0;i<infoItems.length() ;i++){
            mWheelListDataSource.add( infoItems.getString(i));
            if(budgetselected.equals(infoItems.getString(i)))
            {
                focusIndex = i;
            }
        }

        infoItems.recycle();

        wheel.setOffset(2);
        wheel.setSeletion(focusIndex);
        wheel.setItems(mWheelListDataSource);
        wheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(Constants.TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
                AccountCommonUtil.SetGudieBudget(mParent, item);

                m_UpdateStartButton();
            }
        });

        start = (TextView)view. findViewById(R.id.start_use_acccount);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConfigReady()) {
                    Intent mIntent = new Intent();
                    mIntent.setClass(mParent, AccountTotalActivity.class);
                    startActivity(mIntent);
                    mParent.finish();
                }
                else
                {
                    if(AccountCommonUtil.GetGudieArea(mParent).equals("") == true)
                    {
                        m_ShowDisableButtonMessageBox(getString(R.string.account_guide_not_chose_area));
                        //Toast.makeText(mParent, R.string.account_guide_not_chose_area, Toast.LENGTH_SHORT).show();
                    }
                    else if (AccountCommonUtil.GetGudieStyle(mParent).equals("") == true)
                    {
                        //Toast.makeText(mParent, R.string.account_guide_not_chose_style, Toast.LENGTH_SHORT).show();
                        m_ShowDisableButtonMessageBox(getString(R.string.account_guide_not_chose_style));
                    }
                    else if (AccountCommonUtil.GetGudieBudget(mParent).equals("") == true)
                    {
                        m_ShowDisableButtonMessageBox(getString(R.string.account_guide_not_chose_budget));
                        //Toast.makeText(mParent, R.string.account_guide_not_chose_budget, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    }
                }
            }
        });

        start.setSelected(false);

        m_UpdateStartButton();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    void m_UpdateStartButton()
    {
        if(CheckConfigReady())
        {
            start.setSelected(true);
            Log.d(Constants.TAG, "setEnabled  start button");
        }
        else
        {
            Log.d(Constants.TAG, "set DisEnabled  start button" );
            start.setSelected(false);
        }
    }

    boolean CheckConfigReady()
    {
         if(AccountCommonUtil.GetGudieArea(mParent).equals("") == true ||
                 AccountCommonUtil.GetGudieStyle(mParent).equals("") == true ||
                 AccountCommonUtil.GetGudieBudget(mParent).equals("") == true )
         {
             Log.d(Constants.TAG, "GetGudieArea "+ AccountCommonUtil.GetGudieArea(mParent) );
             Log.d(Constants.TAG, "GetGudieStyle "+ AccountCommonUtil.GetGudieStyle(mParent));
             Log.d(Constants.TAG, "GetGudieBudget "+ AccountCommonUtil.GetGudieBudget(mParent) );

             Log.d(Constants.TAG, "CheckConfigReady  not ready" );
             return false;
         }

        Log.d(Constants.TAG, "CheckConfigReady   ready, update button " );

        return true;
    }

    private boolean m_ShowDisableButtonMessageBox(String reason) {

        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getMessageDialog(mParent, reason, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();

        return true;
    }
}
