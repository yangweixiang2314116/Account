package com.example.account;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.example.account.AccountImageSelectorDirPopupWindow.OnImageDirSelected;
import com.example.module.ImageSelectorFloder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AccountImageSelectorActivity extends ActionBarActivity implements OnImageDirSelected
{
	private ProgressDialog mProgressDialog;

	private int mPicsSize;

	private File mImgDir;

	private List<String> mImgs;

	private GridView mGirdView;
	private AccountImageSelectorAdapter mAdapter;

	private HashSet<String> mDirPaths = new HashSet<String>();

	private List<ImageSelectorFloder> mImageFloders = new ArrayList<ImageSelectorFloder>();

	private RelativeLayout mBottomLy;
	private Intent  mIntent = null;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private AccountImageSelectorDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();

			data2View();

			initListDirPopupWindw();
		}
	};


	private void data2View()
	{
		if (mImgDir == null)
		{
			Toast.makeText(getApplicationContext(), R.string.image_scan_no_name,
					Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());

		mAdapter = new AccountImageSelectorAdapter(getApplicationContext(), mImgs,
				R.layout.activity_account_image_selector_grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + getText(R.string.image_number_name).toString());
		
	};


	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new AccountImageSelectorDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.activity_account_image_selector_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});

		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_image_selector);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		mIntent = getIntent();
		
		initView();
		getImages();
		initEvent();

	}

	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, R.string.image_scan_no_external_device, Toast.LENGTH_SHORT).show();
			return;
		}

		mProgressDialog = ProgressDialog.show(this, null, getText(R.string.image_scan_loading));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = AccountImageSelectorActivity.this
						.getContentResolver();


				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e(Constants.TAG, mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e(Constants.TAG, path);

					if (firstImage == null)
						firstImage = path;

					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageSelectorFloder imageFloder = null;
					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else
					{
						mDirPaths.add(dirPath);
						
						imageFloder = new ImageSelectorFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				mDirPaths = null;

				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	private void initView()
	{
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initEvent()
	{
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageSelectorFloder floder)
	{

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));

		mAdapter = new AccountImageSelectorAdapter(getApplicationContext(), mImgs,
				R.layout.activity_account_image_selector_grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + getText(R.string.image_number_name).toString());
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account_image_selector, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.image_selector_finish:
			m_SaveSelectedPicture();
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean m_SaveSelectedPicture()
	{
		Log.i(Constants.TAG, "----imageSelectedlist-data size--"+mAdapter.GetSelectdImages().size());
		
		if(mAdapter.GetSelectdImages().size() > 0)
		{
			Log.i(Constants.TAG, "----imageSelectedlist-data size--"+mAdapter.GetSelectdImages().size());
			mIntent.putStringArrayListExtra("images",  mAdapter.GetSelectdImages());
			setResult(Activity.RESULT_OK, mIntent); 
		}
		else
		{
			Log.i(Constants.TAG, "----RESULT_CANCELED--");
			
			setResult(Activity.RESULT_CANCELED, mIntent); 
		}
		
		return true;
	}

}
