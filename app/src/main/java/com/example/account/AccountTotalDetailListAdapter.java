package com.example.account;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.module.Account;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountTotalDetailListAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private Resources mResources;

	protected ArrayList<Account> mTotalDetailList = new ArrayList<Account>();

	public AccountTotalDetailListAdapter(Context context, ArrayList<Account> dataSourece) {

		super();

		Log.i(Constants.TAG, "-----------AccountTotalDetailListAdapter start--------");

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mResources = context.getResources();

		mTotalDetailList = dataSourece;

		Log.i(Constants.TAG, "-----------AccountTotalDetailListAdapter end--------");
	}

	private Handler WindTalker = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			notifyDataSetChanged();
		}
	};

	public void updateUI() {
		WindTalker.sendEmptyMessage(0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "-----------getCount--------" + mTotalDetailList.size());
		return mTotalDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "-----------getItem--------" + position);
		if (position < 0 || position > mTotalDetailList.size()) {
			Log.i(Constants.TAG, "-----------getItem-error-------" + position);
			return null;
		}
		return mTotalDetailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "-----------getItemId--------");
		return position;
	}
	
	public boolean removeItem(Account item)
	{
		Log.i(Constants.TAG, "-----------removeItem--------");
		mTotalDetailList.remove(item);
		return true;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Log.i(Constants.TAG, "-----------getView----position----" + position);

		ViewHolderText holderText = null;

		if (convertView == null) {

				holderText = new ViewHolderText();

				convertView = mInflater.inflate(R.layout.activity_account_total_list_item, null);

				holderText.date = (TextView) convertView.findViewById(R.id.total_list_item_date);
				holderText.brand = (TextView) convertView.findViewById(R.id.total_list_item_brand);
				holderText.category = (TextView) convertView.findViewById(R.id.total_list_item_category);
				holderText.cost = (TextView) convertView.findViewById(R.id.total_list_item_value);
				convertView.setTag(holderText);

		} else {

				holderText = (ViewHolderText) convertView.getTag();
				holderText.date.setText(AccountCommonUtil.ConverDateToString(mTotalDetailList.get(position).CreateTime));
				if(mTotalDetailList.get(position).Brand.isEmpty())
				{
					holderText.brand.setText(mResources.getText(R.string.total_list_brand_default));
				}
				else
				{
					holderText.brand.setText(mTotalDetailList.get(position).Brand);
				}
				
				if(mTotalDetailList.get(position).Category.isEmpty())
				{
					holderText.category.setText(mResources.getText(R.string.total_list_category_default));
				}
				else
				{
					holderText.category.setText(mTotalDetailList.get(position).Category);
				}
				
				Log.i(Constants.TAG, "--Cost---" + mTotalDetailList.get(position).Cost);
				
				Double cost = mTotalDetailList.get(position).Cost;
				DecimalFormat df= new DecimalFormat("#,##0.00");

				String formatCost = df.format(cost);
				
				holderText.cost.setText(formatCost);
		}

		return convertView;
	}

	class ViewHolderText {
		private TextView date;
		private TextView brand;
		private TextView category;
		private TextView cost;
	}
}