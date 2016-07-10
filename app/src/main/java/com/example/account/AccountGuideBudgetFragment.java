package com.example.account;

import android.app.Activity;
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
import android.widget.GridView;
import android.widget.TextView;

import com.example.module.Account;

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

        mWheelListDataSource.clear();
        for(int i=0;i<infoItems.length() ;i++){
            mWheelListDataSource.add( infoItems.getString(i));
        }

        wheel.setOffset(2);
        wheel.setSeletion(8);
        wheel.setItems(mWheelListDataSource);
        wheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(Constants.TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        start = (TextView)view. findViewById(R.id.start_use_acccount);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.setClass(mParent, AccountTotalActivity.class);
                startActivity(mIntent);
                mParent.finish();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    };
}
