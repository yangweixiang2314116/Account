package com.example.account;

import java.util.List;

import com.example.module.ImageSelectorFloder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class AccountImageSelectorDirPopupWindow extends BasePopupWindowForListView<ImageSelectorFloder>
{
	private ListView mListDir;

	public AccountImageSelectorDirPopupWindow(int width, int height,
			List<ImageSelectorFloder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new AccountImageSelectorCommonAdapter<ImageSelectorFloder>(context, mDatas,
				R.layout.activity_account_image_selector_list_dir_item)
		{
			@Override
			public void convert(AccountImageSelectorViewHolder helper, ImageSelectorFloder item)
			{
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image,
						item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "寮�");
			}
		});
	}

	public interface OnImageDirSelected
	{
		void selected(ImageSelectorFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
