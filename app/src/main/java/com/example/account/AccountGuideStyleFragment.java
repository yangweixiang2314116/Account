package com.example.account;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.module.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;


public class AccountGuideStyleFragment extends Fragment {

    private Resources mResources = null;
    private GridView gview = null;
    private List<Map<String, Object>> data_list  = new ArrayList<Map<String, Object>>();
    private SimpleAdapter sim_adapter;
    private Activity  mParent = null;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mParent =  getActivity();
        mResources = mParent.getResources();

        View view = inflater.inflate(R.layout.fragment_guide_style, container, false);

        gview = (GridView)view. findViewById(R.id.style_grideview);

        getData();

        String [] from ={"text"};
        int [] to = {R.id.guide_style_text};
        sim_adapter = new SimpleAdapter(mParent, data_list, R.layout.guide_gridview_item, from, to);
        gview.setAdapter(sim_adapter);

        return view;
    }

    public List<Map<String, Object>> getData(){
        TypedArray infoItems = mResources.obtainTypedArray(R.array.guide_chose_style);

        data_list.clear();
        for(int i=0;i<infoItems.length() ;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", infoItems.getString(i));
            data_list.add(map);
        }

        return data_list;
    }


}