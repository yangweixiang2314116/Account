package com.example.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.module.ImageLoader;
import com.example.module.MoreInfoItem;
import com.example.module.ImageLoader.Type;
import com.squareup.picasso.Picasso;

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

public class AccountMoreInfoListAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private Resources mResources;

	protected ArrayList<MoreInfoItem> mMoreInfoItemList = new ArrayList<MoreInfoItem>();

	public static final int ACCOUNT_MORE_INFO_TYPE_COUNT = 2;
	
	private Context mContext = null;

	public AccountMoreInfoListAdapter(Context context, ArrayList<MoreInfoItem> dataSourece) {

		super();

		Log.i(Constants.TAG, "-----------AccountMoreInfoListAdapter start--------");

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mResources = context.getResources();

		mMoreInfoItemList = dataSourece;
		
		mContext = context;

		Log.i(Constants.TAG, "-----------AccountMoreInfoListAdapter end--------");
	}
	
	public Boolean replaceDataSrc( ArrayList<MoreInfoItem> dataSourece)
	{
		mMoreInfoItemList = dataSourece;
		updateUI();
		return true;
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
		Log.i(Constants.TAG, "-----------getCount--------" + mMoreInfoItemList.size());
		return mMoreInfoItemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "-----------getItem--------" + position);
		if (position < 0 || position > mMoreInfoItemList.size()) {
			Log.i(Constants.TAG, "-----------getItem-error-------" + position);
			return null;
		}
		return mMoreInfoItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "-----------getItemId--------");
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return ACCOUNT_MORE_INFO_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {

		MoreInfoItem item = (MoreInfoItem) getItem(position);
		if (item != null) {
			return item.itemType;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Log.i(Constants.TAG, "-----------getView----position----" + position);

		ViewHolderText holderText = null;
		ViewHolderImage holderImage = null;

		int nItemType = getItemViewType(position);

		if (convertView == null) {
			switch (nItemType) {
			case Constants.ACCOUNT_MORE_INFO_TYPE_TEXT:
				holderText = new ViewHolderText();

				convertView = mInflater.inflate(R.layout.activity_account_more_info_item_text, null);

				holderText.leftLabel = (TextView) convertView.findViewById(R.id.more_info_item_label);
				holderText.rightValue = (TextView) convertView.findViewById(R.id.more_info_item_value);
				holderText.check = (ImageView) convertView.findViewById(R.id.more_info_check);
				convertView.setTag(holderText);
				break;
			case Constants.ACCOUNT_MORE_INFO_TYPE_IMAGE:
				holderImage = new ViewHolderImage();

				convertView = mInflater.inflate(R.layout.activity_account_more_info_item_image, null);

				holderImage.leftLabel = (TextView) convertView.findViewById(R.id.more_info_item_label);
				holderImage.rightImage = (ImageView) convertView.findViewById(R.id.more_info_item_image);
				holderImage.check = (ImageView) convertView.findViewById(R.id.more_info_check);
				convertView.setTag(holderImage);
				break;
			default:
				break;
			}
		} else {
			switch (nItemType) {
			case Constants.ACCOUNT_MORE_INFO_TYPE_TEXT:
				holderText = (ViewHolderText) convertView.getTag();
				holderText.leftLabel.setText(mMoreInfoItemList.get(position).itemLable);
				holderText.rightValue.setText(mMoreInfoItemList.get(position).itemValue);
				Log.i(Constants.TAG, "-----position--" + position + "----holderText--------" + holderText);
				Log.i(Constants.TAG, "-----position--" + position + "----itemLable--------" + mMoreInfoItemList.get(position).itemLable);
				Log.i(Constants.TAG, "-----position--"+position+"-----------itemValue--------"+mMoreInfoItemList.get(position).itemValue);
				if(mMoreInfoItemList.get(position).itemValue.isEmpty())
				{
					holderText.check.setImageResource(R.mipmap.checkbox_normal);
				}
				else
				{
					holderText.check.setImageResource(R.mipmap.checkbox_checked);
				}
				break;
			case Constants.ACCOUNT_MORE_INFO_TYPE_IMAGE:
				holderImage = (ViewHolderImage) convertView.getTag();
				holderImage.leftLabel.setText(mMoreInfoItemList.get(position).itemLable);
				String ImagePath = mMoreInfoItemList.get(position).itemValue;
				Log.i(Constants.TAG, "-----------ImagePath--------"+ImagePath+"----position---"+position);
				Log.i(Constants.TAG, "-----------LABEL--------"+mMoreInfoItemList.get(position).itemLable);
				if(ImagePath.isEmpty())
				{
					Log.i(Constants.TAG, "-----------ImagePath == null--------");
					holderImage.check.setImageResource(R.mipmap.checkbox_normal);
				}
				else
				{
					Log.i(Constants.TAG, "-----------get(position).itemValue--------"+mMoreInfoItemList.get(position).itemValue);
					holderImage.check.setImageResource(R.mipmap.checkbox_checked);
				}


				
				//if(ImagePath.isEmpty() == false)
				//{
				//	ImageLoader.getInstance(3,Type.LIFO).loadImage(ImagePath, holderImage.rightImage);
				//}
				
				if(ImagePath.isEmpty() == false)
				{
					String DecoderImagePath = "file://" + ImagePath;
				
			        Picasso.with(mContext)
			        .load(DecoderImagePath)
			        .resizeDimen(R.dimen.image_selector_width,R.dimen.image_selector_height)
			        .centerCrop()
			        .placeholder(R.mipmap.info_item_image)
			        .into(holderImage.rightImage);
				}
		        
				break;
			default:
				break;
			}
		}

		return convertView;
	}

	class ViewHolderText {
		private TextView leftLabel;
		private TextView rightValue;
		private ImageView check;
	}

	class ViewHolderImage {
		private TextView leftLabel;
		private ImageView rightImage;
		private ImageView check;
	}

}