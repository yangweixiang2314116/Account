package com.example.account;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.annotation.Column;
import com.example.account.R.id;
import com.example.module.Account;
import com.example.module.AccountAPIInfo;
import com.example.module.ImageItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

public class AccountSyncTask {

	public static final int SYNC_START = 1;
	public static final int SYNC_END = 10;
	public static final int SYNC_ERROR = 100;
	public static final int SYNC_SUCCESS = 1000;


	private Account m_CurrentItem = null;
	
	private Context mContext = null;

	public AccountSyncTask(Context context) {
		mContext = context;
	}
	
	public synchronized void sync(final boolean syncUp, final boolean syncDown, Handler hanler) {
		if (hanler != null) {
			hanler.sendEmptyMessage(SYNC_START);
		}
		toSync(syncUp, syncDown, hanler);
	}

	private synchronized void toSync(final boolean syncUp, final boolean syncDown, Handler handler) {
		new SyncTask(syncUp, syncDown, handler).execute();
	}
	
	class SyncTask extends AsyncTask<Void, Integer, Void> {

		Handler mHandler;
		boolean mSyncUp;
		boolean mSyncDown;
		ArrayList<Account> mAccountSyncUpList = new ArrayList<Account>();
		int        mCurrentSyncUpIndex;

		public SyncTask(Boolean syncUp, Boolean syncDown, Handler handler) {
			this(syncUp, syncDown);
			mHandler = handler;
		}

		private SyncTask(Boolean syncUp, Boolean syncDown) {
			mSyncUp = syncUp;
			mSyncDown = syncDown;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mSyncUp == false && mSyncDown == false) {
				return null;
			}

			publishProgress(new Integer[] { SYNC_START });
			try {
				// makeSureNotebookExsits(NOTEBOOK_NAME);
				if (mSyncUp)
					syncUp();
				if (mSyncDown)
					syncDown();
				publishProgress(new Integer[] { SYNC_SUCCESS });
			} catch (Exception e) {
				publishProgress(new Integer[] { SYNC_ERROR });
				return null;
			} finally {
				publishProgress(new Integer[] { SYNC_END });
			}
			return null;
		}

		private void syncUp() {
			
			Log.i(Constants.TAG, "------start to sync up-1234--");

			mAccountSyncUpList.clear();
			mCurrentSyncUpIndex = 0;
			Log.i(Constants.TAG, "------start to sync up-4--");
			List<Account> AccountList =   Account.getAllAccounts();
			Log.i(Constants.TAG, "------start to sync AccountList ---"+AccountList.size());
			for(int index = 0 ; index < AccountList.size(); index++)
			{
				Account  item = AccountList.get(index);
				if(item.isNeedSyncCreate() ||item.isNeedSyncUp() || item.isNeedSyncDelete() )
				{
					mAccountSyncUpList.add(item);
				}
			}

			Log.i(Constants.TAG, "------start to sync mAccountSyncUpList ---"+mAccountSyncUpList.size());
			m_ProcessSyncUpItems();
		}

		private class AccountCreateJsonHttpResponseHandler extends JsonHttpResponseHandler {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				Log.i(Constants.TAG, "---postAccountItem--onSuccess--response---"+response+ "--statusCode--"+statusCode);

				new ProcessSyncUpTask(response,Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD).execute();

				//process next item
				m_ProcessSyncUpItems();
			}

			@Override

			public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
			{
				super.onFailure(statusCode, headers, throwable, response);
				Log.i(Constants.TAG, "---postAccountItem--onFailure--statusCode---"+statusCode);
				Log.i(Constants.TAG, "---postAccountItem--onFailure--responseString---"+response);

				//process next item
				m_ProcessSyncUpItems();
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

		}

		private class AccountUpdateJsonHttpResponseHandler extends JsonHttpResponseHandler {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				Log.i(Constants.TAG, "---updateAccountItem--onSuccess--response---"+response);
				new ProcessSyncUpTask(response,Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP).execute();
				//process next item
				m_ProcessSyncUpItems();
			}

			@Override

			public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
			{
				super.onFailure(statusCode, headers, throwable, response);
				Log.i(Constants.TAG, "---updateAccountItem--onFailure--statusCode---"+statusCode);
				Log.i(Constants.TAG, "---updateAccountItem--onFailure--responseString---"+response);

				//process next item
				m_ProcessSyncUpItems();
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		}

		private class AccountDeleteJsonHttpResponseHandler extends JsonHttpResponseHandler {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// If the response is JSONObject instead of expected JSONArray
				Log.i(Constants.TAG, "---postAccountItem--onSuccess--response---"+response);
				new ProcessSyncUpTask(response,Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE).execute();

				//process next item
				m_ProcessSyncUpItems();
			}

			@Override

			public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
			{
				super.onFailure(statusCode, headers, throwable, response);
				Log.i(Constants.TAG, "---deleteAccountItem--onFailure--statusCode---"+statusCode);
				Log.i(Constants.TAG, "---deleteAccountItem--onFailure--responseString---"+response);

				//process next item
				m_ProcessSyncUpItems();
			}

		}

		private boolean m_ProcessSyncUpItems()
		{
			if(mCurrentSyncUpIndex >= mAccountSyncUpList.size() )
			{
				Log.i(Constants.TAG, "----- sync up-had finish , done--");
				mAccountSyncUpList.clear();
				mCurrentSyncUpIndex = 0;
				return true;
			}


				m_CurrentItem = mAccountSyncUpList.get(mCurrentSyncUpIndex++);
				Log.i(Constants.TAG, "------start to sync up-m_CurrentItem --"+ m_CurrentItem.getId());
				if(m_CurrentItem.isNeedSyncCreate())
				{
					Log.i(Constants.TAG, "------try to---Create on server -id----"+m_CurrentItem.getId());
					
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AccountApiConnector.instance(mContext).postAccountItem(m_CurrentItem, new AccountCreateJsonHttpResponseHandler());
						}
						
					});
	                
				}
				else if (m_CurrentItem.isNeedSyncUp())
				{
					Log.i(Constants.TAG, "------try to---Sync up on server -id----"+m_CurrentItem.getId());
				
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AccountApiConnector.instance(mContext).updateAccountItem(m_CurrentItem, new AccountUpdateJsonHttpResponseHandler());
							
						}
						
					});
					
				}
				else if ( m_CurrentItem.isNeedSyncDelete())
				{
					Log.i(Constants.TAG, "------try to---Delete on server -id----"+m_CurrentItem.getId());
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AccountApiConnector.instance(mContext).deleteAccountItem(m_CurrentItem, new AccountDeleteJsonHttpResponseHandler() );
						}
						
					});
				}
				else
				{
					Log.i(Constants.TAG, "------do nothing----"+m_CurrentItem.getId());
				}	

				return true;
		}
		
		private void syncDown() {

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					AccountApiConnector.instance(mContext).getDetailList(new JsonHttpResponseHandler() {
		            
			            @Override
			            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
			               
			            	Log.i(Constants.TAG, "---getDetailList--onSuccess--response---"+response);
			            	new ProcessSyncDownTask(response).execute();
			            }
				            
		            });
		            }
			});
			
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (mHandler == null) {
				return;
			}
			switch (values[0]) {
			case SYNC_START:
				mHandler.sendEmptyMessage(SYNC_START);
				break;
			case SYNC_END:
				mHandler.sendEmptyMessage(SYNC_END);
				break;
			case SYNC_ERROR:
				mHandler.sendEmptyMessage(SYNC_ERROR);
				break;
			case SYNC_SUCCESS:
				mHandler.sendEmptyMessage(SYNC_SUCCESS);
				break;
			default:
				break;
			}
		}
	}
	
    private class ProcessSyncUpTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject m_responseObject;
        private int m_Action = Constants.ACCOUNT_ITEM_ACTION_NEED_NOTHING;

        public ProcessSyncUpTask(JSONObject responseObject, int Action) {
        	m_responseObject = responseObject;
        	m_Action = Action;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.i(Constants.TAG, "----ProcessSyncDownTask--doInBackground----");
        	Boolean bResult = false;
            try {
            	switch(m_Action)
            	{
            	case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE:
            	{
            		Log.i(Constants.TAG, "------already delete on server , now delete on DB----");
            		Account item = Account.parseDelete(m_responseObject);
            		
            		Account.deleteOne(item);
            	}
            	break;
            	case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP:
            	case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD:
            	{
            		Account item = Account.build(m_responseObject);
            		Log.i(Constants.TAG, "------already sync on server- local id---"+item.getId()+ "accound id"+item.AccountId);
            		item.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_NOTHING;
            		item.save();
            	}
            	break;
            	default:
            		break;
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
            Log.i(Constants.TAG, "----ProcessSyncUpTask--doInBackground----");

        }
    }

    private class ProcessSyncDownTask extends AsyncTask<Void, Void, Boolean> {
        private JSONArray m_responseObject = null;
        private ArrayList<AccountAPIInfo> m_detailList = null;
        
        
        public ProcessSyncDownTask(JSONArray responseObject) {
        	m_responseObject = responseObject;
        	m_detailList = new ArrayList<AccountAPIInfo>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
        	Boolean bResult = false;
            Log.i(Constants.TAG, "----ProcessSyncDownTask--doInBackground----");
            try {
            	//TODO
                for (int index = 0; index < m_responseObject.length(); index++) {
                	Log.i(Constants.TAG, "----------"+m_CurrentItem.getId());
                	
                	Log.i(Constants.TAG, "----------"+m_responseObject.getJSONObject(index));
                	
                	AccountAPIInfo item = new AccountAPIInfo();
                	item.build(m_responseObject.getJSONObject(index));
                	m_detailList.add(item);
                }
                
                
                //List<Account> localList = Account.getAllAccounts();
                for(int index = 0; index < m_detailList.size(); index++)
                {
                	AccountAPIInfo Serveritem = m_detailList.get(index);
                	
                	Log.i(Constants.TAG, "----Serveritem.AccountId------"+Serveritem.AccountId);
					Log.i(Constants.TAG, "---Serveritem.UpdatedTime------"+Serveritem.UpdatedTime);

                	Account LocalItem = Account.findAccountByAccountId(Serveritem.AccountId);
            		
                	if(LocalItem == null)
                	{
                    	Log.i(Constants.TAG, "---can not find --Serveritem.AccountId------"+Serveritem.AccountId);
                		//download and create this item
                		 Account.buildDownLoad(Serveritem);

                	}
                	else if (LocalItem.UpdatedTime != Serveritem.UpdatedTime)
                	{
						Log.i(Constants.TAG, "---LocalItem.UpdatedTime------"+LocalItem.UpdatedTime);
                		//sync this item
                		LocalItem.sync(Serveritem);
                	}
                	else
                	{
                		continue;
                	}
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
        }
    }

}
