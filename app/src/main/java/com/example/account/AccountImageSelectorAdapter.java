package com.example.account;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class AccountImageSelectorAdapter extends AccountImageSelectorCommonAdapter<String>
{

	public static ArrayList<String> mSelectedImage = new ArrayList<String>();


	private String mDirPath;

	public AccountImageSelectorAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
	}

	public ArrayList<String> GetSelectdImages()
	{
		return mSelectedImage;
	}
	
	
	@Override
	public void convert(final AccountImageSelectorViewHolder helper, final String item)
	{

		helper.setImageResource(R.id.id_item_image, R.mipmap.pictures_no);
				helper.setImageResource(R.id.id_item_select,
						R.mipmap.picture_unselected);
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);

		mImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				if (mSelectedImage.contains(mDirPath + "/" + item))
				{
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.mipmap.picture_unselected);
					mImageView.setColorFilter(null);
				} else
				{
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.mipmap.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}

			}
		});
		

		if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			mSelect.setImageResource(R.mipmap.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
