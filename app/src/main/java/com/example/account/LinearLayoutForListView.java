package com.example.account;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
    private BaseAdapter m_adapter;
    private OnClickListener onClickListener = null;

    /*
    public void bindLinearLayout() {
        int count = m_adapter.getCount();
        
		Log.i(Constants.TAG, "------bindLinearLayout--------"+count);
		
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = m_adapter.getView(i, null, null);
            v.setOnClickListener(this.onClickListener);
            addView(v, i);
        }
        
    }
	*/

    public LinearLayoutForListView(Context context) {
        super(context);
	}
    
    public LinearLayoutForListView(Context context, AttributeSet attrs)     //Constructor that is called when inflating a view from XML
    {
    	super(context,attrs);
    }
    
    public LinearLayoutForListView(Context context, AttributeSet attrs, int defStyle)     //Perform inflation from XML and apply a class-specific base style
    {
    	super(context,attrs,defStyle);
    }

	public void setAdapter(AccountDetailImageListAdapter Adapter) {
		// TODO Auto-generated method stub
		m_adapter = Adapter;
	}

    public BaseAdapter getAdapter()
    {
        return m_adapter;
    }
}