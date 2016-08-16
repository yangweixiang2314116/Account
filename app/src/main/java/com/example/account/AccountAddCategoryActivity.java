package com.example.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.module.Account;
import com.example.module.AccountAPIInfo;
import com.example.module.CategoryHistory;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

public class AccountAddCategoryActivity extends ActionBarActivity {

	private FlowLayout mHotFlowLayout = null;
	
	private Intent  mIntent = null;
    private Context mContext = null;

	protected ArrayList<CategoryHistory> mCategoryHistoryDataSource = new ArrayList<CategoryHistory>();
	private RelativeLayout mCategoryHistoryLayout = null;
	private Button mSubmitButton = null;
	private EditText mCategoryEditText = null;
	private String mCategory  = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
		setTheme(R.style.MIS_NO_ACTIONBAR);

		setContentView(R.layout.activity_account_add_category);

		Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
		}
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setTitle(getString(R.string.add_category_app_name));
		}

		mHotFlowLayout = (FlowLayout) findViewById(R.id.category_history_content);

		mCategoryHistoryLayout = (RelativeLayout)findViewById(R.id.account_recently_category_part);

		mCategoryEditText = (EditText)findViewById(R.id.account_add_category);

		mIntent = getIntent();

		mSubmitButton = (Button) findViewById(R.id.commit);
		mSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String current = mCategoryEditText.getText().toString();
				if(current.isEmpty() == false) {

					Log.i(Constants.TAG, "----Category--" + current);
					mIntent.putExtra("category", current);

					if (false == CategoryHistory.IsExistCategoryContent(current)) {
						CategoryHistory item = new CategoryHistory();
						item.Content = current;
						item.save();
					} else {
						Log.i(Constants.TAG, "------mCurSearchContent already on DB--------" + current);
						CategoryHistory item = CategoryHistory.GetCategoryItemByContent(current);
						item.LastUseTime = System.currentTimeMillis();
						item.save();
					}
					setResult(Activity.RESULT_OK, mIntent);
				}

				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				finish();
				overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
			}
		});

		Bundle bundle = getIntent().getExtras();

		if (bundle != null ) {
			mCategory = bundle.getString("category");

			Log.i(Constants.TAG, "------AccountAddCategoryActivity----onCreate -mCost---"+mCategory);

		}
		else
		{
			Log.i(Constants.TAG, "------AccountAddCategoryActivity----onCreate -bundle==null----");
			return ;
		}

		mCategoryEditText.setText(mCategory);
		mCategoryEditText.setSelection(mCategory.length());
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		TextView   clearCategoryButton  = (TextView) findViewById(R.id.clear_label_history);

		Log.i(Constants.TAG, "------setOnClickListener---clearSearchButton--");
		clearCategoryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(Constants.TAG, "------enter into onClick---clearSearchButton--");
				CategoryHistory.deleteAll();
				Toast.makeText(mContext, getString(R.string.accout_search_clear_history_success), Toast.LENGTH_SHORT).show();
				mCategoryHistoryLayout.setVisibility(View.INVISIBLE);
			}
		});

		mContext = this;
		init();
		MobclickAgent.onEvent(mContext, "enter_category");
	}
	
	public void init() {

		mCategoryHistoryDataSource = (ArrayList<CategoryHistory>) CategoryHistory.GetHistoryItems();

		Log.i(Constants.TAG, "------mSearchHistoryDataSource.size()--------" + mCategoryHistoryDataSource.size());

		if(mCategoryHistoryDataSource.size() > 0) {
			mCategoryHistoryLayout.setVisibility(View.VISIBLE);
			for (int index = 0; index < mCategoryHistoryDataSource.size(); index++ ) {
				addTextView(mCategoryHistoryDataSource.get(index).Content);
			}
		}
		else
		{
			mCategoryHistoryLayout.setVisibility(View.INVISIBLE);
		}
	/*
			AccountApiConnector.instance(this).getHotTagList(mCost, new JsonHttpResponseHandler() {
            
			 @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	                // If the response is JSONObject instead of expected JSONArray
				 	Log.i(Constants.TAG, "---getHotTagList--onSuccess--response---"+response);
				 	
				 	new ProcessCategoryTask(response).execute();
	            }
			 
		        @Override
		        
		        public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
		        {
		            super.onFailure(statusCode, headers, throwable, response);
				 	Log.i(Constants.TAG, "---getHotTagList--onFailure--statusCode---"+statusCode);
				 	Log.i(Constants.TAG, "---getHotTagList--onFailure--responseString---"+response);
				 	
		            Toast.makeText(mContext, R.string.get_data_error, Toast.LENGTH_SHORT).show();
		        }

               @Override
               public void onFinish() {
                   super.onFinish();
               }
	            
       });
       	*/
	}

	public void addTextView(String tvName) {

		TextView categoryTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mHotFlowLayout, false);
		categoryTag.setText(tvName);
		categoryTag.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tag = (TextView)v;
				
				String current = tag.getText().toString();
				Log.i(Constants.TAG, "----Category--" + current);
				//mCategoryEditText.setText(CategoryValue);
				//mCategoryEditText.setSelection(CategoryValue.length());

				mIntent.putExtra("category", current);
				CategoryHistory item = CategoryHistory.GetCategoryItemByContent(current);
				item.LastUseTime = System.currentTimeMillis();
				item.save();

				setResult(Activity.RESULT_OK, mIntent);
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				finish();
				overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
			}
			
		});

		mHotFlowLayout.addView(categoryTag);
	}

	/*
	private class TagInfo
	{
		public long tag_id;
		public String tag_category;
		public String tag_name;
		
		public boolean build(JSONObject response)
		{
	        if (Looper.myLooper() == Looper.getMainLooper()) {
	            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
	                    "in Main thread, it's bad performance and may block the ui thread");
	            throw new RuntimeException(warn);
	        }
	        
	        try {
				tag_id = response.isNull("id") ? 0 : response.getLong("id");
		        tag_category = response.isNull("category") ? "" : response.getString("category");
		        tag_name = response.isNull("name") ? "" : response.getString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return true;
		}
	}
	*/

	/*
	   private class ProcessCategoryTask extends AsyncTask<Void, Void, Boolean> {
	        private JSONArray m_responseObject = null;
	        private ArrayList<TagInfo> m_TagList = null;
	        
	        
	        public ProcessCategoryTask(JSONArray responseObject) {
	        	m_responseObject = responseObject;
	        	m_TagList = new ArrayList<TagInfo>();
	        }

	        @Override
	        protected Boolean doInBackground(Void... voids) {
	        	Boolean bResult = false;
	            Log.i(Constants.TAG, "----ProcessCategoryTask--doInBackground----");
	            try {
	            	//TODO
	                for (int index = 0; index < m_responseObject.length(); index++) {
	                	
	                	Log.i(Constants.TAG, "----------"+m_responseObject.getJSONObject(index));
	                	
	                	TagInfo item = new TagInfo();
	                	item.build(m_responseObject.getJSONObject(index));
	                	m_TagList.add(item);
	                }
	                
	            	bResult = true;
	            } catch (Exception e) {
	                e.printStackTrace();
	                bResult = false;
	            } finally {
	                return bResult;
	            }
	        }

	        @Override
	        protected void onPostExecute(Boolean result) {
	            super.onPostExecute(result);
	            Log.i(Constants.TAG, "----ProcessSyncDownTask--onPostExecute----");
	    		for (int index = 0; index < m_TagList.size(); index++ ) {
	    			addTextView(m_TagList.get(index).tag_name);
	    		}
	    		
	        }
	    }
	    */

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
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