package com.example.account;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.module.Account;
import com.example.module.ImageItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class AccountDetailActivity extends ActionBarActivity  {


	private Account m_CurrentAccount = null;
    private LinearLayoutForListView m_AccountImageList = null;
    private  ArrayList<ImageItem> mImageListDataSource = null;
    private AccountDetailImageListAdapter m_DetailImageListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
		
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		Bundle bundle = getIntent().getExtras();
		if (getIntent() != null ) {
			Long id = getIntent().getLongExtra("id", 0);
			Log.i(Constants.TAG, "------AccountDetailActivity----onCreate -id----"+id);
			
			m_CurrentAccount = Account.load(Account.class,id);
			
		}
		else
		{
			Log.i(Constants.TAG, "------AccountTotalActivity----onCreate -bundle==null----");
			return ;
		}
		
		m_InitAccountDetail();
		Log.i(Constants.TAG, "------AccountTotalActivity----onCreate -----");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_detail, menu);
		Log.i(Constants.TAG, "------onCreateOptionsMenu--------");
		return true;
	}

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
		case R.id.menu_detail_edit:
			{
				Bundle mBundle = new Bundle();
				if (m_CurrentAccount == null) {
					Log.i(Constants.TAG, "--m_CurrentAccount == null--");
				}
				Log.i(Constants.TAG, "-m_CurrentAccount-id--" + m_CurrentAccount.getId());

				mBundle.putLong("id", m_CurrentAccount.getId());
				
				Intent intent = new Intent(this, AccountStartActivity.class);
				intent.putExtras(mBundle);
				this.startActivity(intent);
				finish();
			}
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
        
		//m_AccountImageList.setOnClickListener((OnClickListener) AccountDetailActivity.this);
		m_AccountImageList.bindLinearLayout();
		
		//m_DetailImageListAdapter.updateUI();
		
		return true;
	}


}
