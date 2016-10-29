package com.example.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.module.Account;
import com.example.module.BaseActivity;
import com.example.module.CategoryHistory;
import com.example.module.ImageItem;
import com.example.module.PoiItem;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class AccountDetailActivity extends BaseActivity {


	private Account m_CurrentAccount = null;
    private LinearLayoutForListView m_AccountImageList = null;
    private  ArrayList<ImageItem> mImageListDataSource = null;
    private AccountDetailImageListAdapter m_DetailImageListAdapter = null;
	private Button mEditButton = null;
	private LayoutInflater mLayoutInflater = null;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(R.style.MIS_NO_ACTIONBAR);

		setContentView(R.layout.activity_account_detail);

		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
			Log.i(Constants.TAG, "------AccountDetailActivity----setSupportActionBar---" );
		}
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setTitle(getString(R.string.cost_detail_title));
			Log.i(Constants.TAG, "------AccountDetailActivity----ActionBar Setting---");
		}
		
		Bundle bundle = getIntent().getExtras();
		if (getIntent() != null ) {
			Long id = getIntent().getLongExtra("id", 0);
			Log.i(Constants.TAG, "------AccountDetailActivity----onCreate -id----"+id);
			
			m_CurrentAccount = Account.load(Account.class,id);
			
		}
		else
		{
			Log.i(Constants.TAG, "------AccountDetailActivity----onCreate -bundle==null----");
			return ;
		}

		mContext = this;

		m_InitAccountDetail();

		mEditButton = (Button) findViewById(R.id.account_edit);
		mEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle mBundle = new Bundle();
				if (m_CurrentAccount == null) {
					Log.i(Constants.TAG, "--m_CurrentAccount == null--");
				}
				Log.i(Constants.TAG, "-m_CurrentAccount-id--" + m_CurrentAccount.getId());

				mBundle.putLong("id", m_CurrentAccount.getId());

				Intent intent = new Intent(mContext, AccountStartActivity.class);
				intent.putExtras(mBundle);
				startActivity(intent);
				finish();
			}
		});
		MobclickAgent.onEvent(mContext, "enter_detail");
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.i(Constants.TAG, "------AccountTotalActivity----onCreate -----");
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_detail, menu);
		Log.i(Constants.TAG, "------onCreateOptionsMenu--------");
		return true;
	}
	*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.i(Constants.TAG, "------onOptionsItemSelected--------");
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	public boolean m_InitAccountDetail()
	{
		TextView total = (TextView)findViewById(R.id.detail_total_value);
		
		DecimalFormat df= new DecimalFormat("#,##0.00");

		String formatCost = df.format(m_CurrentAccount.Cost);
		
		total.setText(formatCost);
		
		TextView CurrentTimeText = (TextView) findViewById(R.id.detail_total_date);
		String sCurrentDate = AccountCommonUtil.ConverDateToString(m_CurrentAccount.CreateTime);
		CurrentTimeText.setText(sCurrentDate);
		
		
		TextView Category = (TextView) findViewById(R.id.detail_category_value);
		Category.setText(m_CurrentAccount.Category);
	
		TextView Brand = (TextView) findViewById(R.id.detail_brand_value);
		Brand.setText(m_CurrentAccount.Brand);

		TextView Position = (TextView) findViewById(R.id.detail_position_value);
		Position.setText(m_CurrentAccount.Position);
		
		TextView Comments = (TextView) findViewById(R.id.detail_comments_value);
		Comments.setText(m_CurrentAccount.Comments);
		
		m_AccountImageList = (LinearLayoutForListView) findViewById(R.id.account_detail_image_list);
		
		mImageListDataSource = (ArrayList<ImageItem>) m_CurrentAccount.Imageitems();
		m_DetailImageListAdapter = new AccountDetailImageListAdapter(this,mImageListDataSource);
		m_AccountImageList.setAdapter(m_DetailImageListAdapter);

		int count = m_DetailImageListAdapter.getCount();

		Log.i(Constants.TAG, "------bindLinearLayout--------"+count);

		m_AccountImageList.removeAllViews();
		for (int i = 0; i < count; i++) {
			final int currentIndex = i;
			final View thumbnail = m_DetailImageListAdapter.getView(i, null, null);
			thumbnail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ImageItem item = mImageListDataSource.get(currentIndex);
					Log.i(Constants.TAG, "------currentIndex--------"+currentIndex);
					Log.i(Constants.TAG, "------item.Path--------"+item.Path);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + item.Path), "image/*");
					startActivity(intent);
				}
			});

			thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					final ImageItem item = mImageListDataSource.get(currentIndex);
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

					View messageContent = mLayoutInflater.inflate(R.layout.dialog_content_info, null);
					builder.setView(messageContent);

					TextView content = (TextView) messageContent.findViewById(R.id.dialog_message_content);
					content.setText(getString(R.string.confirm_to_delete_image));
					builder.setPositiveButton(R.string.give_up_sure,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									item.delete();

									//account need sync up
									item.account.setNeedSyncUp();
									item.account.save();

									m_AccountImageList.removeView(thumbnail);
									Toast.makeText(mContext, R.string.give_up_success, Toast.LENGTH_SHORT)
											.show();

								}
							}).setNegativeButton(R.string.give_up_cancel, null)
							.create().show();
					return false;
				}
			});
			m_AccountImageList.addView(thumbnail, i);
		}
		
		return true;
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
