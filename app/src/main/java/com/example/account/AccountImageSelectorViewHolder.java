package com.example.account;

import com.example.module.ImageLoader;
import com.example.module.ImageLoader.Type;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class AccountImageSelectorViewHolder
{
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private AccountImageSelectorViewHolder(Context context, ViewGroup parent, int layoutId,
			int position)
	{
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}


	public static AccountImageSelectorViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position)
	{
		AccountImageSelectorViewHolder holder = null;
		if (convertView == null)
		{
			holder = new AccountImageSelectorViewHolder(context, parent, layoutId, position);
		} else
		{
			holder = (AccountImageSelectorViewHolder) convertView.getTag();
			holder.mPosition = position;
		}
		return holder;
	}

	public View getConvertView()
	{
		return mConvertView;
	}


	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		if (view == null)
		{
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}


	public AccountImageSelectorViewHolder setText(int viewId, String text)
	{
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}


	public AccountImageSelectorViewHolder setImageResource(int viewId, int drawableId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}


	public AccountImageSelectorViewHolder setImageBitmap(int viewId, Bitmap bm)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}


	public AccountImageSelectorViewHolder setImageByUrl(int viewId, String url)
	{
		ImageLoader.getInstance(3,Type.LIFO).loadImage(url, (ImageView) getView(viewId));
		return this;
	}

	public int getPosition()
	{
		return mPosition;
	}

}
