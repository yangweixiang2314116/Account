package com.example.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.module.Account;
import com.example.module.AccountAPIInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

public class AccountAddBrandActivity extends ActionBarActivity {

	private FlowLayout mHotFlowLayout;
	
	private Intent  mIntent = null;
    private Context mContext = null;
    private String  mCategory = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(getString(R.string.add_brand_app_name));
		
		setContentView(R.layout.activity_account_add_category);
		
		mHotFlowLayout = (FlowLayout) findViewById(R.id.hot_category_content);
		
		mIntent = getIntent();
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null ) {
			mCategory = bundle.getString("category");
			
			Log.i(Constants.TAG, "------AccountAddBrandActivity----onCreate -mCost---"+mCategory);
			
		}
		else
		{
			Log.i(Constants.TAG, "------AccountAddBrandActivity----onCreate -bundle==null----");
			return ;
		}
		
		mContext = this;
		init();
		
	}
	
	public void init() {

		AccountApiConnector.instance(this).getHotBrandList(mCategory, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {

				Log.i(Constants.TAG, "---getHotBrandList--onSuccess--response---" + responseString);

			}

			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.i(Constants.TAG, "---getHotBrandList--onSuccess--response---" + responseString);
			}
		});
		/*
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

	            
       });	*/
	}
	
	public void addTextView(String tvName) {
		//����TextView���������ƣ�����������
		TextView brandTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mHotFlowLayout, false);
		brandTag.setText(tvName);
		brandTag.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tag = (TextView)v;
				
				String BrandValue = tag.getText().toString();
				
				Log.i(Constants.TAG, "----Brand--" + BrandValue);
				mIntent.putExtra("brand", BrandValue);
				setResult(Activity.RESULT_OK, mIntent); 
				finish();
				
			}
			
		});
		//��TextView������ʽ����
		mHotFlowLayout.addView(brandTag);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account_add_category, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.add_category_new:
			{
				LayoutInflater inflater = getLayoutInflater();
				final View edit_layout = inflater.inflate(R.layout.add_popup_edit_text,
						(ViewGroup) findViewById(R.id.edit_add_view));
			   
				
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(R.string.add_new_brand)
				.setView(edit_layout)
				.setNegativeButton(R.string.btn_name_cancel, null)
				.setPositiveButton(R.string.btn_name_ok,  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						EditText  input = (EditText) edit_layout.findViewById(R.id.edit_add_content);
						
						Log.i(Constants.TAG, "--user--input--" + input.getText().toString());
					
						addTextView(input.getText().toString());
					}	
					
				});
				
				
				builder.create().show();
				//finish();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
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
}