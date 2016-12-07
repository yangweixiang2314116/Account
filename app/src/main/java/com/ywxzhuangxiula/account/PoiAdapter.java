package com.ywxzhuangxiula.account;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;


import java.util.List;

public class PoiAdapter extends BaseAdapter {
    private Context context;
    private List<PoiInfo> pois;
    private LinearLayout linearLayout;

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL = 1;

    PoiAdapter(Context context, List<PoiInfo> pois) {
        this.context = context;
        this.pois = pois;
    }

    @Override
    public int getCount() {
        return pois.size();
    }

    @Override
    public Object getItem(int position) {
        return pois.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
            return position==0 ? TYPE_FIRST : TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            int type = getItemViewType(position);

            Log.i(Constants.TAG, "---position--"+ position +"--getItemViewType-----"+type);

            if(type == TYPE_NORMAL){
                convertView = LayoutInflater.from(context).inflate(R.layout.locationpois_item, null);
            }
            else
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.location_focus_item, null);
            }

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PoiInfo poiInfo = pois.get(position);
        holder.locationpoi_name.setText(poiInfo.name);

        return convertView;
    }

    class ViewHolder {
        TextView locationpoi_name;
        //TextView locationpoi_address;

        ViewHolder(View view) {
            locationpoi_name = (TextView) view.findViewById(R.id.locationpois_name);
           // locationpoi_address = (TextView) view.findViewById(R.id.locationpois_address);
        }
    }
}
