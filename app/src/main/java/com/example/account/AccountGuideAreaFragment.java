package com.example.account;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/16.
 */
public class AccountGuideAreaFragment extends Fragment {

    private WheelView wheelArea = null;
    private Resources mResources = null;
    private Activity mParent = null;
    private ArrayList<String> mWheelListDataSource = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_guide_area, container, false);

        wheelArea = (WheelView)view. findViewById(R.id.wheel_view_area);

        Log.d(Constants.TAG, "onCreateView:  load area wheel view !!!");

        mResources = this.getResources();
        mParent =  getActivity();
        TypedArray areaItems = mResources.obtainTypedArray(R.array.guide_chose_area_text);

        mWheelListDataSource.clear();
        for(int i=0;i<areaItems.length() ;i++){
            mWheelListDataSource.add( areaItems.getString(i));
        }

        Log.d(Constants.TAG, "onCreateView: prepare data source !");

        wheelArea.setOffset(2);
        Log.d(Constants.TAG, "onCreateView: setOffset !");

        wheelArea.setSeletion(5);
        Log.d(Constants.TAG, "onCreateView: setSeletion !");

        wheelArea.setItems(mWheelListDataSource);
        Log.d(Constants.TAG, "onCreateView: setItems !");

        wheelArea.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(Constants.TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
                AccountCommonUtil.SetGudieArea(mParent, item);
            }
        });

        Log.d(Constants.TAG, "onCreateView: setOnWheelViewListener !");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    };
}
