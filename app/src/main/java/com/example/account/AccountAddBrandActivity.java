package com.example.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.module.Account;
import com.example.module.AccountAPIInfo;
import com.example.module.BaseActivity;
import com.example.module.BrandHistory;
import com.example.module.DialogHelp;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

public class AccountAddBrandActivity extends BaseActivity {

	private FlowLayout mHotFlowLayout;
	
	private Intent  mIntent = null;
    private Context mContext = null;
    private String  mBrand = "";
	protected ArrayList<BrandHistory> mBrandHistoryDataSource = new ArrayList<BrandHistory>();
	private RelativeLayout mBrandHistoryLayout = null;
	private Button mSubmitButton = null;
	private EditText mBrandEditText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
		//setTheme(R.style.MIS_NO_ACTIONBAR);
		setContentView(R.layout.activity_account_add_brand);

		Toolbar toolbar = (Toolbar) findViewById(R.id.brand_toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
		}
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setTitle(getString(R.string.add_brand_app_name));
		}

		mHotFlowLayout = (FlowLayout) findViewById(R.id.brand_history_content);

		mBrandHistoryLayout = (RelativeLayout)findViewById(R.id.account_recently_brand_part);

		mBrandEditText = (EditText)findViewById(R.id.account_add_brand);

		mSubmitButton = (Button) findViewById(R.id.commit);
		mSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String current = mBrandEditText.getText().toString();
				if(current.isEmpty() == false) {

					Log.i(Constants.TAG, "----brand--" + current);
					mIntent.putExtra("brand", current);

					if (false == BrandHistory.IsExistBrandContent(current)) {
						BrandHistory item = new BrandHistory();
						item.Content = current;
						item.save();
					} else {
						Log.i(Constants.TAG, "------mCurSearchContent already on DB--------" + current);
						BrandHistory item = BrandHistory.GetBrandItemByContent(current);
						item.LastUseTime = System.currentTimeMillis();
						item.save();
					}

					//hide soft input
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mBrandEditText.getWindowToken(), 0);

					setResult(Activity.RESULT_OK, mIntent);
				}

				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				finish();
				overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
			}
		});

		//mHotFlowLayout = (FlowLayout) findViewById(R.id.hot_category_content);
		
		mIntent = getIntent();
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null ) {
			mBrand = bundle.getString("brand");
			
			Log.i(Constants.TAG, "------AccountAddBrandActivity----onCreate -mCost---"+mBrand);
			
		}
		else
		{
			Log.i(Constants.TAG, "------AccountAddBrandActivity----onCreate -bundle==null----");
			return ;
		}

		mBrandEditText.setText(mBrand);
		mBrandEditText.setSelection(mBrand.length());
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		mContext = this;

		TextView   clearBrandButton  = (TextView) findViewById(R.id.clear_brand_history);

		Log.i(Constants.TAG, "------setOnClickListener---clear_brand_history--");
		clearBrandButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(Constants.TAG, "------enter into onClick---clearCategoryButton--");
				BrandHistory.deleteAll();
				Toast.makeText(mContext, getString(R.string.accout_search_clear_history_success), Toast.LENGTH_SHORT).show();
				mBrandHistoryLayout.setVisibility(View.INVISIBLE);
			}
		});

		init();
		MobclickAgent.onEvent(mContext, "enter_brand");
	}

	public void init() {

		mBrandHistoryDataSource = (ArrayList<BrandHistory>) BrandHistory.GetHistoryItems();

		Log.i(Constants.TAG, "------mSearchHistoryDataSource.size()--------" + mBrandHistoryDataSource.size());

		if (mBrandHistoryDataSource.size() > 0) {
			mBrandHistoryLayout.setVisibility(View.VISIBLE);
			for (int index = 0; index < mBrandHistoryDataSource.size(); index++) {
				addTextView(mBrandHistoryDataSource.get(index));
			}
		} else {
			mBrandHistoryLayout.setVisibility(View.INVISIBLE);
		}
	}
	/*
	public void init() {
		AccountApiConnector.instance(this).getHotBrandList(mCategory, new JsonHttpResponseHandler() {
            
			 @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	                // If the response is JSONObject instead of expected JSONArray
				 	Log.i(Constants.TAG, "---getHotTagList--onSuccess--response---"+response);
				 	
				 	if(response.length() > 0)
				 	{
				 		new ProcessBrandTask(response).execute();
				 	}
				 	else
				 	{
			            Toast.makeText(mContext, R.string.no_data_brand, Toast.LENGTH_SHORT).show();
				 	}
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
	}
	*/

	public void addTextView(BrandHistory Branditem) {

		TextView brandTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mHotFlowLayout, false);
		brandTag.setText(Branditem.Content);
		brandTag.setTag(Branditem);
		brandTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tag = (TextView) v;

				String current = tag.getText().toString();

				Log.i(Constants.TAG, "----Brand--" + current);

				//mBrandEditText.setText(BrandValue);
				//mBrandEditText.setSelection(BrandValue.length());

				mIntent.putExtra("brand", current);
				BrandHistory item = BrandHistory.GetBrandItemByContent(current);
				item.LastUseTime = System.currentTimeMillis();
				item.save();

				//hide soft input
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mBrandEditText.getWindowToken(), 0);

				setResult(Activity.RESULT_OK, mIntent);
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				finish();
				overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);

			}
		});

		brandTag.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				TextView offItem = (TextView) v;
				BrandHistory chose = (BrandHistory) offItem.getTag();
				if (null == chose) {
					Log.i(Constants.TAG, "------null == chose--------");
					return false;
				}
				m_ShowDeletePoup(chose, offItem);
				return false;
			}
		});

		mHotFlowLayout.addView(brandTag);
	}

	private void m_ShowDeletePoup(BrandHistory chose , TextView view) {
		final BrandHistory itemDelete = chose;
		final TextView itemView = view;

		Log.i(Constants.TAG, "------m_ShowDeletePoup--------" + itemDelete.Content);

		android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, getString(R.string.confirm_to_delete_recently_use), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				itemDelete.delete();
				mHotFlowLayout.removeView(itemView);
				dialogInterface.dismiss();
			}
		});
		dialog.show();

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//hide soft input
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mBrandEditText.getWindowToken(), 0);

			finish();
			overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	private class BrandInfo
	{
		public long brand_id;
		public long brand_cited_times;
		public String brand_city;
		public String brand_category;
		public String brand_name;
		
		public boolean build(JSONObject response)
		{
	        if (Looper.myLooper() == Looper.getMainLooper()) {
	            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
	                    "in Main thread, it's bad performance and may block the ui thread");
	            throw new RuntimeException(warn);
	        }
	        
	        try {
	        	brand_id = response.isNull("id") ? 0 : response.getLong("id");
	        	brand_cited_times = response.isNull("brand_cited_times") ? 0 : response.getLong("brand_cited_times");
	        	brand_city = response.isNull("city") ? "" : response.getString("city");
	        	brand_category = response.isNull("tag") ? "" : response.getString("tag");
	        	brand_name = response.isNull("brand") ? "" : response.getString("brand");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return true;
		}
	}
	
	   private class ProcessBrandTask extends AsyncTask<Void, Void, Boolean> {
	        private JSONArray m_responseObject = null;
	        private ArrayList<BrandInfo> m_BrandList = null;
	        
	        
	        public ProcessBrandTask(JSONArray responseObject) {
	        	m_responseObject = responseObject;
	        	m_BrandList = new ArrayList<BrandInfo>();
	        }

	        @Override
	        protected Boolean doInBackground(Void... voids) {
	        	Boolean bResult = false;
				Log.i(Constants.TAG, "----ProcessBrandTask--doInBackground----");
	            try {
	            	//TODO
	                for (int index = 0; index < m_responseObject.length(); index++) {
	                	
	                	Log.i(Constants.TAG, "----------"+m_responseObject.getJSONObject(index));
	                	
	                	BrandInfo item = new BrandInfo();
	                	item.build(m_responseObject.getJSONObject(index));
	                	m_BrandList.add(item);
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
	            Log.i(Constants.TAG, "----ProcessBrandTask--onPostExecute----");
	    		for (int index = 0; index < m_BrandList.size(); index++ ) {
	    			addTextView(m_BrandList.get(index).brand_name);
	    		}
	    		
	        }
	    }
*/
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}