package com.example.account;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.color.tab_indicator_text;

public class AccountGuideStyleAdapter extends SimpleAdapter {
    private View v;
    private Context mContext;

    public AccountGuideStyleAdapter(Context context, List<? extends Map<String, ?>> data,
                          int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
    }


    private int selectedPosition = -1;// 选中的位置
    public void setSelectedPosition(int position) {

        selectedPosition = position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        v = super.getView(position, convertView, parent);
        TextView styleText =  ((TextView) v.findViewById(R.id.guide_style_text));
        Log.i(Constants.TAG, "-------getView----selectedPosition--------"+selectedPosition+ "--position--"+position);

        if (position == selectedPosition) {
            Log.i(Constants.TAG, "-------getView----set selected-");
            v.setBackgroundResource(R.drawable.guide_text_item_select_background);

            styleText.setTextColor(mContext.getResources().getColor(R.color.account_top_background_color));
        }
        else{

            v.setBackgroundResource(R.drawable.guide_text_item_background);

            styleText.setTextColor(mContext.getResources().getColor(android.R.color.tab_indicator_text));
        }
        return v;
    }
}